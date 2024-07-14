package com.nailcase.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.nailcase.exception.codes.ShopErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
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
@Transactional
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

	@Transactional
	public List<PostImageDto> uploadImages(List<MultipartFile> files, Long memberId) {
		if (files.size() > 6) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "게시물당 최대 5개의 이미지만 업로드할 수 있습니다.");
		}

		List<PostImage> tempImages = files.stream()
			.map(file -> {
				PostImage tempImage = new PostImage();
				tempImage.setCreatedBy(memberId);
				return tempImage;
			})
			.collect(Collectors.toList());

		List<ImageDto> savedImageDtos = postImageService.saveImages(files, tempImages);

		return savedImageDtos.stream()
			.map(savedImageDto -> PostImageDto.builder()
				.id(savedImageDto.getId())
				.bucketName(savedImageDto.getBucketName())
				.objectName(savedImageDto.getObjectName())
				.url(savedImageDto.getUrl())
				.createdBy(savedImageDto.getCreatedBy())
				.modifiedBy(savedImageDto.getModifiedBy())
				.build())
			.collect(Collectors.toList());
	}

	public PostDto.Response registerPost(Long shopId, PostDto.Request postRequest) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		Post post = Post.builder()
			.title(postRequest.getTitle())
			.shop(shop)
			.category(postRequest.getCategory())
			.contents(postRequest.getContents())
			.build();

		if (postRequest.getImageIds() != null && !postRequest.getImageIds().isEmpty()) {
			List<PostImage> postImages = postRequest.getImageIds().stream()
				.map(imageId -> {
					PostImage image = postImageRepository.findByImageId(imageId)
						.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));
					image.setPost(post); // 게시물과 이미지 연결
					return image;
				})
				.collect(Collectors.toList());

			postImageRepository.saveAll(postImages);
			post.registerPostImages(postImages);
		}
		postRepository.save(post);

		return PostDto.Response.from(post, false);
	}

	public PostDto.Response updatePost(Long shopId, Long postId, PostDto.Request postRequest, Long memberId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		boolean alreadyLiked = postLikedMemberRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);

		post.updateTitle(postRequest.getTitle());
		post.updateContents(postRequest.getContents());
		// 기존 이미지 삭제
		post.getPostImages().forEach(postImage -> {
			postImageService.deleteImage(postImage.getObjectName());
			postImageRepository.delete(postImage);
		});
		post.getPostImages().clear();

		// 새로운 이미지 연결
		List<PostImage> newPostImages = postRequest.getImageIds().stream()
			.map(imageId -> postImageRepository.findByImageId(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)))
			.collect(Collectors.toList());
		post.getPostImages().addAll(newPostImages);
		postImageRepository.saveAll(newPostImages);

		return PostDto.Response.from(post, alreadyLiked);
	}

	@Transactional
	public void addImageToPost(Long postId, List<MultipartFile> files) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		List<PostImage> postImages = files.stream()
			.map(file -> {
				PostImage postImage = new PostImage();
				postImage.setPost(post);
				return postImage;
			})
			.collect(Collectors.toList());

		List<ImageDto> savedImages = postImageService.saveImages(files, postImages);

		postImages = savedImages.stream()
			.map(savedImage -> {
				PostImage postImage = new PostImage();
				postImage.setBucketName(savedImage.getBucketName());
				postImage.setObjectName(savedImage.getObjectName());
				postImage.setPost(post);
				return postImage;
			})
			.collect(Collectors.toList());

		post.getPostImages().addAll(postImages);
		postImageRepository.saveAll(postImages);
		postRepository.save(post);
	}

	public void removeImageFromPost(Long postId, Long imageId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		PostImage postImage = postImageRepository.findByImageId(imageId)
			.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

		String objectName = postImage.getObjectName();
		postImageService.deleteImage(objectName);

		post.removePostImage(postImage);
		postImageRepository.delete(postImage);
	}

	public List<PostDto.Response> listShopNews(Long shopId, Long memberId) {
		List<Post> posts = postRepository.findByShop_ShopId(shopId);
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

	public PostDto.Response viewShopNews(Long shopId, Long postId, Long memberId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		boolean alreadyLiked = postLikedMemberRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);

		// 조회수 증가 로직
		String key = "post:view:count:" + postId;
		Long offset = memberId;
		Boolean alreadyViewed = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyViewed) {
			bitmapService.setBit(key, offset, true);
			Long viewCount = bitmapService.bitCount(key).orElse(0L);
			post.incrementViews(viewCount);
			postRepository.save(post);
		}

		Long currentViewCount = bitmapService.bitCount(key).orElse(0L);
		return PostDto.Response.from(post, alreadyLiked);
	}

	public void deletePost(Long shopId, Long postId) {
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

	public PostCommentDto.Response updateComment(Long commentId,
		PostCommentDto.Request commentRequest) {
		PostComment postComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.COMMENT_NOT_FOUND));
		postComment.updateBody(commentRequest.getBody());
		commentRepository.save(postComment); // 엔티티 저장 후 업데이트된 값을 반영
		return PostCommentDto.Response.from(postComment);
	}

	public void deleteComment(Long shopId, Long postId, Long commentId) {
		commentRepository.deleteById(commentId);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public boolean toggleLike(Long shopId, Long postId, Long memberId) {
		try {
			Shop shop = shopRepository.findById(shopId)
				.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
			Member currentMember = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

			PostLikedMember existingLike = postLikedMemberRepository.findByPost_PostIdAndMember_MemberId(postId,
					memberId)
				.orElse(null);

			boolean liked;

			if (existingLike == null) {
				log.info("좋아요가 없음: shopId={}, memberId={}", shopId, memberId);
				// 좋아요가 없는 경우 추가
				PostLikedMember newLike = new PostLikedMember();
				newLike.updatePost(post);
				newLike.updateMember(currentMember);
				postLikedMemberRepository.save(newLike);
				post.incrementLikes();
				liked = true;
				log.info("좋아요 추가됨: postId={}, memberId={}", postId, memberId);
			} else {
				log.info("좋아요가 이미 있음: postId={}, memberId={}", postId, memberId);
				// 좋아요가 있는 경우 제거
				postLikedMemberRepository.delete(existingLike);
				post.decrementLikes();
				liked = false;
				log.info("좋아요 제거됨: postId={}, memberId={}", postId, memberId);
			}

			postRepository.save(post);
			log.info("최종 좋아요 상태: postId={}, memberId={}, liked={}", postId, memberId, liked);
			return liked;
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
}