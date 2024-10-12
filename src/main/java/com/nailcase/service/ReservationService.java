package com.nailcase.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.common.ReservationEvent;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.model.dto.NailArtistDto;
import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.MonthlyArtImageRepository;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.util.DateUtils;
import com.nailcase.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReservationService {

	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final ReservationDetailRepository reservationDetailRepository;
	private final MemberRepository memberRepository;
	private final MonthlyArtImageRepository monthlyArtImageRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final ShopRepository shopRepository;

	@Transactional
	public ReservationDto.RegisterResponse createReservation(Long shopId, Long memberId, ReservationDto.Post dto) {
		// startTime 유효성 검사
		LocalDateTime startTime = DateUtils.unixTimeStampToLocalDateTime(dto.getStartTime());
		// 예약 초과인지 여부 검토
		validateReservationAvailability(shopId, startTime);

		// 멤버 조회
		Member member = memberRepository.findByMemberIdAndRole(memberId, Role.MEMBER)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		// MonthlyArtImage 조회
		MonthlyArtImage monthlyArtImage = Optional.ofNullable(dto.getTreatment().getImageId())
			.flatMap(monthlyArtImageRepository::findByImageId)
			.orElse(null);

		if (monthlyArtImage == null && dto.getTreatment().getImageId() != null) {
			throw new BusinessException(CommonErrorCode.NOT_FOUND);
		}
		Reservation reservation = reservationMapper.toEntity(shopId, memberId, member.getNickname(), dto,
			monthlyArtImage);

		// 예약 생성
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		NailArtist nailArtist = shop.getNailArtist();
		reservation.updateNailArtist(nailArtist);
		Reservation savedReservation = reservationRepository.save(reservation);

		String notificationContent = "님이 예약을 요청하였습니다.";

		ReservationEvent reservationEvent = ReservationEvent.builder()
			.reservation(savedReservation)
			.nickname(member.getNickname())
			.notificationType(NotificationType.RESERVATION_REQUEST)
			.content(notificationContent)
			.startTime(startTime)
			.endTime(null).build();

		eventPublisher.publishEvent(reservationEvent);

		return reservationMapper.toRegisterResponse(savedReservation);
	}

	public ReservationDto.pageableResponse listReservation(Long shopId, Long startUnixTimeStamp,
		Long endUnixTimeStamp, ReservationStatus status, Pageable pageable) {

		LocalDateTime start = LocalDate.now().atTime(LocalTime.MIN);
		LocalDateTime startDate = startUnixTimeStamp != null
			? DateUtils.unixTimeStampToLocalDateTime(startUnixTimeStamp)
			: start;
		LocalDateTime endDate = endUnixTimeStamp != null
			? DateUtils.unixTimeStampToLocalDateTime(endUnixTimeStamp)
			: start.plusMonths(1);
		Page<Reservation> reservationPage = reservationRepository.findReservationListWithinDateRange(
			shopId, startDate, endDate, status, pageable);

		return reservationMapper.toPageableResponse(reservationPage);
	}

	public ReservationDto.viewResponse viewReservation(Long shopId, Long reservationId) {
		return reservationRepository.findById(reservationId)
			.filter(reservation -> reservation.getShop().getShopId().equals(shopId))
			.map(reservationMapper::toDetailedResponse)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
	}

	private void validateReservationAvailability(Long shopId, LocalDateTime startTime) {
		// eq(shopId), eq(startTime), eq(PENDING), eq(CONFIRMED)
		List<ReservationDetail> reservationDetails =
			reservationDetailRepository.findOngoingReservationDetailList(shopId, startTime);
		if (!reservationDetails.isEmpty() && checkReservationAvailability(reservationDetails)) {
			throw new BusinessException(ReservationErrorCode.RESERVATION_OVERBOOKED);
		}
	}

	private boolean checkReservationAvailability(
		List<ReservationDetail> reservationDetails
	) {
		int pendingMultiplier = 3;
		int tempAvailableSeats = reservationDetails.getFirst().getShop().getAvailableSeats();

		Map<ReservationStatus, List<ReservationDetail>> groupByStatus = reservationDetails.stream()
			.collect(Collectors.groupingBy(ReservationDetail::getStatus));
		List<ReservationDetail> confirmed = groupByStatus.get(ReservationStatus.CONFIRMED);
		List<ReservationDetail> pending = groupByStatus.get(ReservationStatus.PENDING);
		tempAvailableSeats -= confirmed.size();
		tempAvailableSeats *= pendingMultiplier;
		tempAvailableSeats -= pending.size();
		return tempAvailableSeats > 0;
	}

	public ReservationDto.MainPageResponse findEarliestReservationByCustomer(Member member) {
		Pageable pageable = PageRequest.of(0, 1);
		return reservationRepository.fetchUpcomingReservationWithReservationDetails(member.getMemberId(), pageable)
			.stream()
			.findFirst()
			.map(reservation -> {
				ReservationDto.MainPageResponse response = reservationMapper.toMainPageResponse(reservation);
				if (response == null || response.getDetails() == null || response.getDetails().isEmpty() ||
					response.getDetails()
						.stream()
						.anyMatch(detail -> detail.getStartTime() == null)) {
					return null;
				}

				// Shop 이미지 URL 설정
				if (response.getShop() != null && reservation.getShop() != null &&
					!reservation.getShop().getShopImages().isEmpty()) {
					ShopImage firstImage = reservation.getShop().getShopImages().iterator().next();
					String imageUrl = StringUtils.generateImageUrl(firstImage.getBucketName(),
						firstImage.getObjectName());
					response.getShop().setShopImageUrl(imageUrl);
				}

				return response;
			})
			.orElse(null);
	}

	public List<ReservationDto.CompletedReservationResponse> findRecentlyCompletedReservationByCustomer(Member member) {
		Pageable pageable = PageRequest.of(0, 3);
		return reservationRepository.fetchCompletedReservationsWithDetailAndShop(member.getMemberId(), pageable)
			.stream()
			.sorted(Comparator.comparing(reservation ->
				reservation.getReservationDetail() != null
					? reservation.getReservationDetail().getStartTime()
					: LocalDateTime.MIN
			))
			.limit(3)
			.map(this::convertToCompletedReservationResponse)
			.collect(Collectors.toList());
	}

	private ReservationDto.CompletedReservationResponse convertToCompletedReservationResponse(Reservation reservation) {
		ReservationDto.CompletedReservationResponse response = new ReservationDto.CompletedReservationResponse();
		response.setReservationId(reservation.getReservationId());
		response.setShop(convertToCompletedShopInfo(reservation.getShop()));
		response.setStartTime(getStartTime(reservation.getReservationDetail()));
		return response;
	}

	private ReservationDto.CompletedReservationResponse.ShopInfo convertToCompletedShopInfo(Shop shop) {
		ReservationDto.CompletedReservationResponse.ShopInfo shopInfo = new ReservationDto.CompletedReservationResponse.ShopInfo();
		shopInfo.setId(shop.getShopId());
		shopInfo.setName(shop.getShopName());
		shopInfo.setImage(getFirstShopImage(shop));
		return shopInfo;
	}

	private String getFirstShopImage(Shop shop) {
		return shop.getShopImages().stream()
			.findFirst()
			.map(image -> String.format("%s/%s", image.getBucketName(), image.getObjectName()))
			.orElse(null);
	}

	private Long getStartTime(ReservationDetail detail) {
		return detail != null && detail.getStartTime() != null
			? DateUtils.localDateTimeToUnixTimeStamp(detail.getStartTime())
			: null;
	}

	public List<ReservationDto.Available> listAvailableTime(Shop shop, Long[] artistIds, WorkHour workHour, Long date) {
		LocalDateTime time = getReservationTimeIfBeforeEnableTime(date);
		List<ReservationDetail> reservationDetails = reservationDetailRepository.findReservationByShopIdAndOnDate(
			shop.getShopId(), time);

		int availableSeats = shop.getAvailableSeats();
		long openTime = DateUtils.combineLocalDateTimeToUnixTimeStamp(time, workHour.getOpenTime());
		long closeTime = DateUtils.combineLocalDateTimeToUnixTimeStamp(time, workHour.getCloseTime());
		Set<NailArtist> nailArtists = shop.getNailArtists();
		List<NailArtist> requestedArtists = nailArtists.stream()
			.filter(nailArtist -> Arrays.stream(artistIds)
				.anyMatch(artistId -> nailArtist.getNailArtistId().equals(artistId)))
			.toList();

		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId = reservationDetails.stream()
			.collect(Collectors.groupingBy(
				r -> r.getNailArtist() == null ? 0L : r.getNailArtist().getNailArtistId()));

		// TODO: intervalUnit이 WorkHour에 들어가거나 Shop에 들어가거나 결정되면 수정
		int intervalUnit = 60 * 30;
		int timeTableCount = (int)((closeTime - openTime) / intervalUnit);

		// 영업시간을 시간 단위로 나누어 예약 시간 생성
		List<Long> times = LongStream.range(0, timeTableCount)
			.mapToObj(i -> openTime + (i * intervalUnit))
			.toList();

		return getAvailables(times, reservationDetails, availableSeats, requestedArtists,
			reservationDetailsOrderByArtistId);
	}

	private @NotNull LocalDateTime getReservationTimeIfBeforeEnableTime(Long date) {
		long reservationPeriod = 1L;
		LocalDateTime time = DateUtils.unixTimeStampToLocalDateTime(date);
		LocalDateTime today = LocalDate.now().atTime(LocalTime.MIN);
		if (time.isAfter(today.plusMonths(reservationPeriod))) {
			throw new BusinessException(ReservationErrorCode.INVALID_TIME);
		}
		return time;
	}

	private List<ReservationDto.Available> getAvailables(List<Long> times,
		List<ReservationDetail> reservationDetails,
		int availableSeats,
		List<NailArtist> requestedArtists,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId
	) {
		List<ReservationDto.Available> availables = new ArrayList<>();

		int timeIdx = 0;
		int reservationIdx = 0;
		while (timeIdx < times.size() && reservationIdx < reservationDetails.size()) {
			Long time = times.get(timeIdx);
			if (isBeforeReservationStartTime(time, reservationDetails.get(reservationIdx).getStartTime())) {
				ReservationDto.Available available = getAvailableWithArtistNear(availableSeats, requestedArtists,
					reservationDetailsOrderByArtistId, time);
				availables.add(available);
				timeIdx++;
				continue;
			}
			if (isAfterReservationEndTime(time, reservationDetails.get(reservationIdx).getEndTime())) {
				reservationIdx++;
				continue;
			}
			// startTime <= time < endTime 시작
			List<NailArtistDto.Response> artistResponses = getNailArtistResponses(requestedArtists);
			int tempIdx = reservationIdx;
			int tempAvailableSeats = availableSeats;
			long endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetails.get(tempIdx).getEndTime());
			while (tempIdx < reservationDetails.size() && time < endTime) {
				ReservationDetail reservationDetail = reservationDetails.get(tempIdx);
				endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetail.getEndTime());
				setNearToArtistResponses(reservationDetailsOrderByArtistId, artistResponses, reservationDetail, time);
				tempAvailableSeats--;
				tempIdx++;
			}

			ReservationDto.Available available = getAvailable(tempAvailableSeats, time, artistResponses);
			availables.add(available);

			timeIdx++;
			reservationIdx++;
		}

		while (timeIdx < times.size()) {
			Long time = times.get(timeIdx);
			List<NailArtistDto.Response> responses = getNailArtistResponses(requestedArtists);
			ReservationDto.Available available = getAvailable(availableSeats, time, responses);
			availables.add(available);
			timeIdx++;
		}

		return availables;
	}

	private void setNearToArtistResponses(Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		List<NailArtistDto.Response> artistResponses, ReservationDetail reservationDetail, Long time) {
		for (NailArtistDto.Response artist : artistResponses) {
			if (isArtistAlreadyReserved(artist, reservationDetail)) {
				Long near = getNear(reservationDetailsOrderByArtistId, artist.getId(), time);
				artist.setEnable(false);
				artist.setNear(near);
			}
		}
	}

	private boolean isAfterReservationEndTime(Long timeUnixTimeStamp, LocalDateTime reservationEndTime) {
		return timeUnixTimeStamp >= DateUtils.localDateTimeToUnixTimeStamp(reservationEndTime);
	}

	private boolean isBeforeReservationStartTime(Long timeUnixTimeStamp, LocalDateTime reservationStartTimeUnixTime) {
		return timeUnixTimeStamp < DateUtils.localDateTimeToUnixTimeStamp(reservationStartTimeUnixTime);
	}

	private boolean isArtistAlreadyReserved(NailArtistDto.Response artist, ReservationDetail reservationDetail) {
		return reservationDetail.getNailArtist() != null
			&& artist.getId().equals(reservationDetail.getNailArtist().getNailArtistId());
	}

	private ReservationDto.Available getAvailable(
		int tempAvailableSeats,
		long time,
		List<NailArtistDto.Response> artistResponses
	) {
		ReservationDto.Available available = new ReservationDto.Available();
		available.setAvailableSeats(tempAvailableSeats);
		available.setStartTime(time);
		available.setArtists(artistResponses);
		return available;
	}

	private List<NailArtistDto.Response> getNailArtistResponses(List<NailArtist> requestedArtists) {
		return requestedArtists.stream()
			.map(NailArtistDto.Response::fromEntity)
			.toList();
	}

	private ReservationDto.Available getAvailableWithArtistNear(
		int availableSeats,
		List<NailArtist> requestedArtists,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		long startTime
	) {
		ReservationDto.Available available = new ReservationDto.Available();

		List<NailArtistDto.Response> artistResponses = requestedArtists.stream()
			.map(NailArtistDto.Response::fromEntity)
			.map(response -> response.setNear(
				getNear(reservationDetailsOrderByArtistId, response.getId(), startTime)))
			.toList();

		available.setStartTime(startTime);
		available.setAvailableSeats(availableSeats);
		available.setArtists(artistResponses);

		return available;
	}

	private @Nullable Long getNear(
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		long nailArtistId,
		long timeUnixTimeStamp
	) {
		return reservationDetailsOrderByArtistId.getOrDefault(nailArtistId, List.of())
			.stream()
			.filter(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) > timeUnixTimeStamp)
			.map(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) - timeUnixTimeStamp)
			.findFirst()
			.orElse(null);
	}

	@Transactional
	public ReservationDto.Response updateReservationStatus(Shop shop, Long reservationId, Long memberId,
		ReservationStatus status) {
		Reservation reservation = reservationRepository.findByIdWithReservationDetail(reservationId)
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		if (status.equals(ReservationStatus.CANCELED) && !reservation.getCustomer().getMemberId().equals(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_UPDATABLE_USER);
		}

		if ((status.equals(ReservationStatus.REJECTED) || status.equals(ReservationStatus.COMPLETED))
			&& !shop.hasNailArtist(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_UPDATABLE_USER);
		}

		if (reservation.isStatusUpdatable(status)) {
			reservation.updateStatus(status);
		}

		if (status == ReservationStatus.CANCELED) {
			memberRepository.findById(memberId)
				.ifPresent(member -> {
					String notificationContent = "님이 예약을 취소하였습니다.";
					ReservationEvent reservationEvent = ReservationEvent.builder()
						.reservation(reservation)
						.nickname(member.getNickname())
						.notificationType(NotificationType.RESERVATION_CANCEL)
						.content(notificationContent)
						.startTime(reservation.getReservationDetail().getStartTime())
						.endTime(null).build();

					eventPublisher.publishEvent(reservationEvent);
				});
		}

		return reservationMapper.toResponse(reservation);
	}

	@Transactional
	public ReservationDto.Response updateReservationStatusToReject(Shop shop, Long reservationId, Long memberId,
		ReservationStatus status, String rejectReason) {

		Reservation reservation = reservationRepository.findByIdWithReservationDetail(reservationId)
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		if (status.equals(ReservationStatus.CANCELED) && !reservation.getCustomer().getMemberId().equals(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_UPDATABLE_USER);
		}

		if ((status.equals(ReservationStatus.REJECTED) || status.equals(ReservationStatus.COMPLETED))
			&& !shop.hasNailArtist(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_UPDATABLE_USER);
		}

		if (reservation.isStatusUpdatable(status)) {
			reservation.updateStatus(status);
		}
		reservation.updateRejectReason(rejectReason);

		NotificationDto.Request notificationRequest = new NotificationDto.Request();
		notificationRequest.setSenderId(memberId);

		if (status.equals(ReservationStatus.REJECTED)) {
			String notificationContent = "에서 예약을 거절하였습니다.";
			ReservationEvent reservationEvent = ReservationEvent.builder()
				.reservation(reservation)
				.notificationType(NotificationType.RESERVATION_REJECT)
				.content(notificationContent)
				.nickname(shop.getShopName())
				.startTime(reservation.getReservationDetail().getStartTime())
				.endTime(null).build();

			eventPublisher.publishEvent(reservationEvent);
		}
		return reservationMapper.toResponse(reservation);
	}

	@Transactional
	public ReservationDto.Response confirmReservation(Shop shop, Long reservationId, Long memberId,
		ReservationDto.Confirm request) {
		Reservation reservation = reservationRepository.findByIdWithReservationDetail(reservationId)
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		if (!shop.hasNailArtist(memberId)) {
			throw new BusinessException(ReservationErrorCode.NOT_UPDATABLE_USER);
		}

		ReservationDetail reservationDetail = reservation.getReservationDetail();
		if (reservationDetail == null) {
			throw new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND);
		}

		if (request == null) {
			throw new BusinessException(CommonErrorCode.BAD_REQUEST);
		}

		LocalDateTime endTime = DateUtils.unixTimeStampToLocalDateTime(request.getEndTime());
		String price = request.getPrice();

		if (!reservationDetail.isReservationTimeUpdatable(endTime)) {
			throw new BusinessException(ReservationErrorCode.INVALID_TIME);
		}
		reservationDetail.updateReservationTime(endTime);

		if (price != null) {
			reservationDetail.updatePrice(price);
		}

		if (!reservation.isConfirmable()) {
			throw new BusinessException(ReservationErrorCode.END_TIME_NOT_SET);
		}

		reservation.confirm();

		String notificationContent = "에서 예약을 승인하였습니다.";
		ReservationEvent reservationEvent = ReservationEvent.builder()
			.reservation(reservation)
			.notificationType(NotificationType.RESERVATION_APPROVE)
			.nickname(shop.getShopName())
			.content(notificationContent)
			.startTime(reservation.getReservationDetail().getStartTime())
			.endTime(endTime).build();

		eventPublisher.publishEvent(reservationEvent);
		return reservationMapper.toResponse(reservation);
	}

}
