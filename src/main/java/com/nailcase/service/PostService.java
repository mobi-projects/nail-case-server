package com.nailcase.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ConcurrencyErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.exception.codes.PostErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Post;
import com.nailcase.model.entity.PostComment;
import com.nailcase.model.entity.PostImage;
import com.nailcase.model.entity.PostLikedMember;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.PostCommentsRepository;
import com.nailcase.repository.PostImageRepository;
import com.nailcase.repository.PostLikedMemberRepository;
import com.nailcase.repository.PostsRepository;
import com.nailcase.repository.ShopRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
	private final PostsRepository postRepository;
	private final PostCommentsRepository commentRepository;
	private final PostImageRepository postImageRepository;
	private final BitmapService bitmapService;
	private final PostImageService postImageService;
	private final MemberRepository memberRepository;
	private final PostLikedMemberRepository postLikedMemberRepository;
	private final ShopRepository shopRepository;
	private final PostCommentsRepository postCommentsRepository;

	@Transactional
	public CompletableFuture<List<PostImageDto>> uploadImages(List<MultipartFile> files,
		NailArtistDetails nailArtistDetails,
		Long shopId) {

		if (files.size() > 6) {
			return CompletableFuture.failedFuture(
				new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "게시물당 최대 5개의 이미지만 업로드할 수 있습니다."));
		}

		List<PostImage> tempImages = files.stream()
			.map(file -> {
				PostImage tempImage = new PostImage();
				tempImage.setCreatedBy(nailArtistDetails.getNailArtistId());
				return tempImage;
			})
			.collect(Collectors.toList());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return postImageService.saveImagesAsync(files, tempImages, auth)
			.thenApply(savedImageDtos -> savedImageDtos.stream()
				.map(this::mapToPostImageDto)
				.collect(Collectors.toList()));
	}

	@Transactional
	public PostDto.Response registerPost(Long shopId, PostDto.Request postRequest,
		NailArtistDetails nailArtistDetails) {
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);

		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		Post post = buildPostFromRequest(postRequest, shop);
		associateImagesWithPost(postRequest.getImageIds(), post);
		postRepository.save(post);

		return PostDto.Response.from(post, false);
	}

	// TODO existsByPost_PostIdAndMember_MemberId 맞는지 체크
	@Async("imageExecutor")
	public CompletableFuture<PostDto.Response> updatePost(Long shopId, Long postId, PostDto.Request postRequest,
		NailArtistDetails nailArtistDetails) {
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);

		return CompletableFuture.supplyAsync(() -> {
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

			updatePostDetails(post, postRequest);
			removeAndReplaceImages(post, postRequest.getImageIds());

			return PostDto.Response.from(post, null);
		}).exceptionally(ex -> {
			log.error("Post update failed", ex);
			throw new BusinessException(PostErrorCode.UPDATE_FAILURE, "업데이트 실패한 게시물" + ex.getMessage());
		});
	}

	@Async("imageExecutor")
	@Transactional
	public CompletableFuture<Void> addImageToPost(Long shopId, Long postId, List<MultipartFile> files,
		NailArtistDetails nailArtistDetails) {
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);

		return CompletableFuture.runAsync(() -> {
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

			List<PostImage> postImages = files.stream()
				.map(file -> new PostImage(post))
				.collect(Collectors.toList());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			List<ImageDto> savedImages = postImageService.saveImagesAsync(files, postImages, auth).join();

			List<PostImage> savedPostImages = savedImages.stream()
				.map(savedImage -> new PostImage(post, savedImage.getBucketName(), savedImage.getObjectName()))
				.collect(Collectors.toList());

			post.getPostImages().addAll(savedPostImages);
			postImageRepository.saveAll(savedPostImages);
		});
	}

	@Async("imageExecutor")
	public CompletableFuture<Void> removeImageFromPost(Long shopId, Long postId, Long imageId,
		NailArtistDetails nailArtistDetails) {
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);

		return CompletableFuture.runAsync(() -> {
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
			PostImage postImage = postImageRepository.findByImageId(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

			String objectName = postImage.getObjectName();
			postImageService.deleteImageAsync(objectName).join();

			post.removePostImage(postImage);
			postImageRepository.delete(postImage);
		});
	}

	public List<PostDto.Response> listShopNews(Long shopId, UserPrincipal userPrincipal) {
		List<Post> posts = postRepository.findByShop_ShopId(shopId);

		if (userPrincipal == null) {
			// 로그인하지 않은 사용자
			return posts.stream()
				.map(post -> PostDto.Response.from(post, false))
				.collect(Collectors.toList());
		} else if (userPrincipal instanceof NailArtistDetails) {
			// NailArtist (매니저)
			return posts.stream()
				.map(post -> PostDto.Response.from(post, null))  // liked 상태를 null로 설정
				.collect(Collectors.toList());
		} else {
			// 일반 사용자 (MemberDetails)
			Long memberId = userPrincipal.getId();
			List<Long> postIds = posts.stream().map(Post::getPostId).collect(Collectors.toList());
			List<PostLikedMember> likedPosts = postLikedMemberRepository.findByPost_PostIdInAndMember_MemberId(postIds,
				memberId);
			Set<Long> likedPostIds = likedPosts.stream()
				.map(postLikedMember -> postLikedMember.getPost().getPostId())
				.collect(Collectors.toSet());

			return posts.stream()
				.map(post -> {
					boolean liked = likedPostIds.contains(post.getPostId());
					return PostDto.Response.from(post, liked);
				})
				.collect(Collectors.toList());
		}
	}

	// PostService.java
	public PostDto.Response viewShopNews(Long shopId, Long postId, UserPrincipal userPrincipal) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		Boolean liked = null;
		Long memberId = null;

		if (userPrincipal != null) {
			memberId = userPrincipal.getId();
			if (!(userPrincipal instanceof NailArtistDetails)) {
				liked = postLikedMemberRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);
			}
		}

		// 조회수 증가 로직
		String viewKey = "post:view:count:" + postId;
		String uniqueViewerKey = "post:unique_viewers:" + postId;
		Long offset = (memberId != null) ? memberId : 0L; // 비로그인 사용자는 0으로 처리
		boolean isNewViewer = bitmapService.getBit(uniqueViewerKey, offset).orElse(false);

		if (!isNewViewer) {
			bitmapService.setBit(uniqueViewerKey, offset, true);
			bitmapService.increment(viewKey);
		}

		Long currentViewCount = bitmapService.get(viewKey);
		return PostDto.Response.from(post, liked, currentViewCount);
	}

	@Transactional
	public void deletePost(Long shopId, Long postId, NailArtistDetails nailArtistDetails) {
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);
		postRepository.deleteById(postId);
	}

	@Transactional
	public PostCommentDto.Response registerComment(Long shopId, Long postId, PostCommentDto.Request commentRequest,
		Long memberId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		PostComment postComment = PostComment.builder()
			.body(commentRequest.getBody())
			.post(post)
			.createdBy(memberId)
			.build();
		commentRepository.save(postComment);
		return PostCommentDto.Response.from(postComment);
	}

	@Transactional
	public PostCommentDto.Response updateComment(
		Long commentId,
		PostCommentDto.Request commentRequest, Long memberId) {

		PostComment postComment = postCommentsRepository.findByCommentIdAndCreatedBy(commentId, memberId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.COMMENT_NOT_FOUND));

		postComment.updateBody(commentRequest.getBody());
		commentRepository.save(postComment); // 엔티티 저장 후 업데이트된 값을 반영
		return PostCommentDto.Response.from(postComment);
	}

	@Transactional
	public void deleteComment(Long shopId, Long postId, Long commentId, Long memberId) {
		PostComment postComment = postCommentsRepository.findByCommentIdAndCreatedBy(commentId, memberId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.COMMENT_NOT_FOUND));
		commentRepository.deleteById(commentId);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public boolean toggleLike(Long shopId, Long postId, Long memberId) {
		try {
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
			Member currentMember = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

			return postLikedMemberRepository.findByPost_PostIdAndMember_MemberId(postId, memberId)
				.map(existingLike -> {
					postLikedMemberRepository.delete(existingLike);
					post.decrementLikes();
					log.info("좋아요 제거됨: postId={}, memberId={}", postId, memberId);
					return false;
				})
				.orElseGet(() -> {
					PostLikedMember newLike = new PostLikedMember();
					newLike.updatePost(post);
					newLike.updateMember(currentMember);
					postLikedMemberRepository.save(newLike);
					post.incrementLikes();
					log.info("좋아요 추가됨: postId={}, memberId={}", postId, memberId);
					return true;
				});
		} catch (OptimisticLockException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("낙관적 락킹 실패 ", e);
			throw new BusinessException(ConcurrencyErrorCode.OPTIMISTIC_LOCK_ERROR,
				"현재 다른 사용자가 같은 작업을 수행 중입니다. 잠시 후 다시 시도해주세요.");
		} catch (Exception e) {
			log.error("toggle like 실행중 예상치 못한 예외 발생 : ", e);
			throw new BusinessException(ConcurrencyErrorCode.UPDATE_FAILURE,
				"좋아요 상태 변경 중 예기치 않은 에러가 발생했습니다.");
		}
	}

	private void associateImagesWithPost(List<Long> imageIds, Post post) {
		if (imageIds != null && !imageIds.isEmpty()) {
			List<PostImage> postImages = imageIds.stream()
				.map(imageId -> {
					PostImage image = postImageRepository.findByImageId(imageId)
						.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));
					image.setPost(post);
					return image;
				})
				.collect(Collectors.toList());

			postImageRepository.saveAll(postImages);
			post.registerPostImages(postImages);
		}
	}

	private PostImageDto mapToPostImageDto(ImageDto dto) {
		return PostImageDto.builder()
			.id(dto.getId())
			.bucketName(dto.getBucketName())
			.objectName(dto.getObjectName())
			.url(dto.getUrl())
			.createdBy(dto.getCreatedBy())
			.modifiedBy(dto.getModifiedBy())
			.build();
	}

	private Post buildPostFromRequest(PostDto.Request request, Shop shop) {
		return Post.builder()
			.title(request.getTitle())
			.shop(shop)
			.category(request.getCategory())
			.contents(request.getContents())
			.build();
	}

	private void updatePostDetails(Post post, PostDto.Request request) {
		post.updateTitle(request.getTitle());
		post.updateContents(request.getContents());
	}

	private void removeAndReplaceImages(Post post, List<Long> newImageIds) {
		List<CompletableFuture<Void>> deleteFutures = post.getPostImages().stream()
			.map(image -> postImageService.deleteImageAsync(image.getObjectName()))
			.toList();

		CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0])).join();

		postImageRepository.deleteAll(post.getPostImages());
		post.clearPostImages();

		List<PostImage> newPostImages = newImageIds.stream()
			.map(imageId -> postImageRepository.findByImageId(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)))
			.collect(Collectors.toList());

		post.getPostImages().addAll(newPostImages);
		postImageRepository.saveAll(newPostImages);
	}
}

