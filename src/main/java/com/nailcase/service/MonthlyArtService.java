package com.nailcase.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.exception.codes.PostErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.MonthlyArtDto;
import com.nailcase.model.dto.MonthlyArtImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.MonthlyArt;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.model.entity.MonthlyArtLikedMember;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.MonthlyArtImageRepository;
import com.nailcase.repository.MonthlyArtLikedMemberRepository;
import com.nailcase.repository.MonthlyArtRepository;
import com.nailcase.repository.NailArtistRepository;
import com.nailcase.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyArtService {
	private final MonthlyArtRepository monthlyArtRepository;
	private final MonthlyArtImageRepository monthlyArtImageRepository;
	private final MonthlyArtImageService monthlyArtImageService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;
	private final ShopRepository shopRepository;
	private final MonthlyArtLikedMemberRepository monthlyArtLikedMemberRepository;
	private final BitmapService bitmapService;

	@Transactional
	public List<MonthlyArtImageDto> uploadImages(List<MultipartFile> files, Long managerId) {
		if (files.size() > 6) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "이달의 아트 게시물당 최대 5개의 이미지만 업로드할 수 있습니다.");
		}

		List<MonthlyArtImage> tempImages = files.stream()
			.map(file -> {
				MonthlyArtImage tempImage = new MonthlyArtImage();
				tempImage.setCreatedBy(managerId);
				return tempImage;
			})
			.collect(Collectors.toList());

		List<ImageDto> savedImageDtos = monthlyArtImageService.saveImages(files, tempImages);

		return savedImageDtos.stream()
			.map(savedImageDto -> MonthlyArtImageDto.builder()
				.id(savedImageDto.getId())
				.bucketName(savedImageDto.getBucketName())
				.objectName(savedImageDto.getObjectName())
				.url(savedImageDto.getUrl())
				.createdBy(savedImageDto.getCreatedBy())
				.modifiedBy(savedImageDto.getModifiedBy())
				.build())
			.collect(Collectors.toList());
	}

	public MonthlyArtDto.Response registerMonthlyArt(Long shopId, MonthlyArtDto.Request monthlyArtRequest) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		MonthlyArt monthlyArt = MonthlyArt.builder()
			.title(monthlyArtRequest.getTitle())
			.shop(shop)
			.build();

		if (monthlyArtRequest.getImageIds() != null && !monthlyArtRequest.getImageIds().isEmpty()) {
			List<MonthlyArtImage> monthlyArtImages = monthlyArtRequest.getImageIds().stream()
				.map(imageId -> {
					MonthlyArtImage image = monthlyArtImageRepository.findByImageId(imageId)
						.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));
					image.setMonthlyArt(monthlyArt); // 이달의 아트 게시물과 이미지 연결
					return image;
				})
				.collect(Collectors.toList());

			monthlyArtImageRepository.saveAll(monthlyArtImages);
			monthlyArt.setMonthlyArtImages(monthlyArtImages);
		}
		monthlyArtRepository.save(monthlyArt);

		return MonthlyArtDto.Response.from(monthlyArt, false);
	}

	public MonthlyArtDto.Response updateMonthlyArt(Long shopId, Long monthlyArtId,
		MonthlyArtDto.Request monthlyArtRequest, Long managerId) {
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		boolean alreadyLiked = monthlyArtLikedMemberRepository.existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(
			monthlyArtId, managerId);

		monthlyArt.updateTitle(monthlyArtRequest.getTitle());
		// 기존 이미지 삭제
		monthlyArt.getMonthlyArtImages().forEach(monthlyArtImage -> {
			monthlyArtImageService.deleteImage(monthlyArtImage.getObjectName());
			monthlyArtImageRepository.delete(monthlyArtImage);
		});
		monthlyArt.getMonthlyArtImages().clear();

		// 새로운 이미지 연결
		List<MonthlyArtImage> newMonthlyArtImages = monthlyArtRequest.getImageIds().stream()
			.map(imageId -> monthlyArtImageRepository.findByImageId(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)))
			.collect(Collectors.toList());
		monthlyArt.getMonthlyArtImages().addAll(newMonthlyArtImages);
		monthlyArtImageRepository.saveAll(newMonthlyArtImages);

		return MonthlyArtDto.Response.from(monthlyArt, alreadyLiked);
	}

	@Transactional
	public void addImageToMonthlyArt(Long monthlyArtId, List<MultipartFile> files) {
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		List<MonthlyArtImage> monthlyArtImages = files.stream()
			.map(file -> {
				MonthlyArtImage monthlyArtImage = new MonthlyArtImage();
				monthlyArtImage.setMonthlyArt(monthlyArt);
				return monthlyArtImage;
			})
			.collect(Collectors.toList());

		List<ImageDto> savedImages = monthlyArtImageService.saveImages(files, monthlyArtImages);

		monthlyArtImages = savedImages.stream()
			.map(savedImage -> {
				MonthlyArtImage monthlyArtImage = new MonthlyArtImage();
				monthlyArtImage.setBucketName(savedImage.getBucketName());
				monthlyArtImage.setObjectName(savedImage.getObjectName());
				monthlyArtImage.setMonthlyArt(monthlyArt);
				return monthlyArtImage;
			})
			.collect(Collectors.toList());

		monthlyArt.getMonthlyArtImages().addAll(monthlyArtImages);
		monthlyArtImageRepository.saveAll(monthlyArtImages);
		monthlyArtRepository.save(monthlyArt);
	}

	public void removeImageFromMonthlyArt(Long monthlyArtId, Long imageId) {
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		MonthlyArtImage monthlyArtImage = monthlyArtImageRepository.findByImageId(imageId)
			.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

		String objectName = monthlyArtImage.getObjectName();
		monthlyArtImageService.deleteImage(objectName);

		monthlyArt.removeImage(monthlyArtImage);
		monthlyArtImageRepository.delete(monthlyArtImage);
	}

	public List<MonthlyArtDto.Response> listMonthlyArts(Long shopId, Long memberId) {
		List<MonthlyArt> monthlyArts = monthlyArtRepository.findByShop_ShopId(shopId);
		List<Long> monthlyArtIds = monthlyArts.stream().map(MonthlyArt::getMonthlyArtId).collect(Collectors.toList());
		List<MonthlyArtLikedMember> likedMonthlyArts = monthlyArtLikedMemberRepository.findByMonthlyArt_MonthlyArtIdInAndMember_MemberId(
			monthlyArtIds, memberId);
		Set<Long> likedMonthlyArtIds = likedMonthlyArts.stream()
			.map(monthlyArtLikedMember -> monthlyArtLikedMember.getMonthlyArt().getMonthlyArtId())
			.collect(Collectors.toSet());
		return monthlyArts.stream()
			.map(monthlyArt -> {
				boolean liked = likedMonthlyArtIds.contains(monthlyArt.getMonthlyArtId());
				return MonthlyArtDto.Response.from(monthlyArt, liked);
			})
			.collect(Collectors.toList());
	}

	public MonthlyArtDto.Response viewMonthlyArt(Long shopId, Long monthlyArtId, Long memberId) {
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		boolean alreadyLiked = monthlyArtLikedMemberRepository.existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(
			monthlyArtId, memberId);
		// 조회수 증가 로직
		String key = "monthlyArt:view:count:" + monthlyArtId;
		Long offset = memberId;
		Boolean alreadyViewed = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyViewed) {
			bitmapService.setBit(key, offset, true);
			Long viewCount = bitmapService.bitCount(key).orElse(0L);
			monthlyArt.incrementViews(viewCount);
			monthlyArtRepository.save(monthlyArt);
		}

		Long currentViewCount = bitmapService.bitCount(key).orElse(0L);
		return MonthlyArtDto.Response.from(monthlyArt, alreadyLiked);
	}

	public void deleteMonthlyArt(Long shopId, Long monthlyArtId, Long managerId) {
		monthlyArtRepository.deleteById(monthlyArtId);
	}

	@Transactional
	public void likeMonthlyArt(Long monthlyArtId, Long memberId) {
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		boolean alreadyLiked = monthlyArtLikedMemberRepository.existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(
			monthlyArtId,
			memberId);

		if (!alreadyLiked) {
			MonthlyArtLikedMember monthlyArtLikedMember = new MonthlyArtLikedMember();
			monthlyArtLikedMember.updateMonthlyArt(monthlyArt);
			monthlyArtLikedMember.updateMember(currentMember);
			monthlyArtLikedMemberRepository.save(monthlyArtLikedMember);
			monthlyArt.incrementLikes();
			monthlyArtRepository.save(monthlyArt);
		}
	}

	@Transactional
	public void unlikeMonthlyArt(Long monthlyArtId, Long memberId) {
		MonthlyArtLikedMember monthlyArtLikedMember = monthlyArtLikedMemberRepository.findByMonthlyArt_MonthlyArtIdAndMember_MemberId(
				monthlyArtId,
				memberId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.LIKE_NOT_FOUND));

		monthlyArtLikedMemberRepository.delete(monthlyArtLikedMember);
		MonthlyArt monthlyArt = monthlyArtRepository.findById(monthlyArtId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		monthlyArt.decrementLikes();
		monthlyArtRepository.save(monthlyArt);
	}
}