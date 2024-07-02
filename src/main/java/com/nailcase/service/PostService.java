package com.nailcase.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Post;
import com.nailcase.model.entity.PostComment;
import com.nailcase.model.entity.PostImage;
import com.nailcase.model.entity.PostLikedMember;
import com.nailcase.model.entity.QMember;
import com.nailcase.model.entity.QPost;
import com.nailcase.model.entity.QPostComment;
import com.nailcase.model.entity.QPostImage;
import com.nailcase.model.entity.QPostLikedMember;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.PostCommentsRepository;
import com.nailcase.repository.PostImageRepository;
import com.nailcase.repository.PostLikedMemberRepository;
import com.nailcase.repository.PostsRepository;
import com.nailcase.util.ServiceUtils;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

	private final Executor imageExecutor;
	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;
	private final TransactionTemplate transactionTemplate;
	private final PostsRepository postRepository;
	private final PostCommentsRepository commentRepository;
	private final PostImageRepository postImageRepository;
	private final BitmapService bitmapService;
	private final PostImageService postImageService;
	private final PostLikedMemberRepository postLikedMemberRepository;

	@Transactional
	@Async("imageExecutor")
	public CompletableFuture<List<PostImageDto>> uploadImages(List<MultipartFile> files, Long memberId) {
		if (files.size() > 5) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "게시물당 최대 5개의 이미지만 업로드할 수 있습니다.");
		}
		List<PostImage> tempImages = files.stream()
			.map(file -> {
				PostImage image = new PostImage();
				image.setCreatedBy(memberId);  // createdBy 설정
				return image;
			})
			.collect(Collectors.toList());

		return postImageService.saveImagesAsync(files, tempImages)
			.thenApply(savedImageDtos -> savedImageDtos.stream()
				.map(savedImageDto -> PostImageDto.builder()
					.id(savedImageDto.getId())
					.bucketName(savedImageDto.getBucketName())
					.objectName(savedImageDto.getObjectName())
					.url(savedImageDto.getUrl())
					.createdBy(savedImageDto.getCreatedBy())
					.build())
				.collect(Collectors.toList()));
	}

	@Transactional
	public PostDto.Response registerPost(Long shopId, PostDto.Request postRequest) {
		Shop shop = queryFactory.selectFrom(QShop.shop)
			.where(QShop.shop.shopId.eq(shopId)).fetchOne();

		ServiceUtils.checkNullValue(shop);

		Post post = Post.builder()
			.title(postRequest.getTitle())
			.category(postRequest.getCategory())
			.shop(shop)
			.contents(postRequest.getContents())
			.build();

		Post savedPost = postRepository.save(post);

		if (postRequest.getImageIds() != null && !postRequest.getImageIds().isEmpty()) {
			List<PostImage> postImages = postImageRepository.findAllById(postRequest.getImageIds());

			if (postImages.size() != postRequest.getImageIds().size()) {
				throw new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND);
			}

			postImages.forEach(image -> {
				image.setPost(savedPost);
				savedPost.addPostImage(image);
			});
			postImageRepository.saveAll(postImages);
		}

		return PostDto.Response.from(savedPost, false);
	}

	@Transactional
	@Async("imageExecutor")
	public CompletableFuture<PostDto.Response> updatePost(Long shopId, Long postId, PostDto.Request postRequest,
		Long memberId) {
		return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
			Post post = queryFactory.selectFrom(QPost.post)
				.leftJoin(QPost.post.shop).fetchJoin()
				.where(QPost.post.shop.shopId.eq(shopId).and(QPost.post.postId.eq(postId)))
				.fetchOne();
			ServiceUtils.checkNullValue(post);

			boolean alreadyLiked = postLikedMemberRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);

			post.updateTitle(postRequest.getTitle());
			post.updateContents(postRequest.getContents());

			List<Long> oldImageIds = queryFactory
				.select(QPostImage.postImage.imageId)
				.from(QPostImage.postImage)
				.where(QPostImage.postImage.post.eq(post))
				.fetch();

			List<Long> imagesToDelete = oldImageIds.stream()
				.filter(id -> !postRequest.getImageIds().contains(id))
				.collect(Collectors.toList());

			CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					if (!imagesToDelete.isEmpty()) {
						queryFactory.update(QPostImage.postImage)
							.setNull(QPostImage.postImage.post)
							.where(QPostImage.postImage.imageId.in(imagesToDelete))
							.execute();
						imagesToDelete.forEach(imageId ->
							postImageService.deleteImageAsync(
								queryFactory
									.select(QPostImage.postImage.objectName)
									.from(QPostImage.postImage)
									.where(QPostImage.postImage.imageId.eq(imageId))
									.fetchOne()
							)
						);
					}
					return null;
				});
			});

			CompletableFuture<Void> addFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					List<Long> newImageIds = postRequest.getImageIds().stream()
						.filter(id -> !oldImageIds.contains(id))
						.collect(Collectors.toList());

					if (!newImageIds.isEmpty()) {
						queryFactory.update(QPostImage.postImage)
							.set(QPostImage.postImage.post, post)
							.where(QPostImage.postImage.imageId.in(newImageIds))
							.execute();
					}
					return null;
				});
			});

			CompletableFuture.allOf(deleteFuture, addFuture).join();

			entityManager.flush();
			entityManager.clear();

			Post refreshedPost = queryFactory
				.selectFrom(QPost.post)
				.leftJoin(QPost.post.postImages, QPostImage.postImage).fetchJoin()
				.where(QPost.post.postId.eq(postId))
				.fetchOne();

			ServiceUtils.checkNullValue(refreshedPost);

			return PostDto.Response.from(refreshedPost, alreadyLiked);
		}), imageExecutor);
	}

	@Transactional
	public void addImageToPost(Long shopId, Long postId, List<MultipartFile> files) {
		Post post = queryFactory.selectFrom(QPost.post)
			.leftJoin(QPost.post.shop).fetchJoin()
			.where(QPost.post.shop.shopId.eq(shopId)
				.and(QPost.post.postId.eq(postId)))
			.fetchOne();

		ServiceUtils.checkNullValue(post);

		List<PostImage> postImages = files.stream()
			.map(file -> {
				PostImage postImage = new PostImage();
				postImage.setPost(post);
				return postImage;
			})
			.collect(Collectors.toList());

		postImageService.saveImagesAsync(files, postImages)
			.thenCompose(savedImages -> {
				List<PostImage> newPostImages = savedImages.stream()
					.map(savedImage -> {
						PostImage postImage = new PostImage();
						postImage.setBucketName(savedImage.getBucketName());
						postImage.setObjectName(savedImage.getObjectName());
						postImage.setPost(post);
						return postImage;
					})
					.collect(Collectors.toList());

				// QueryDSL을 사용하여 새 이미지와 Post를 연결
				CompletableFuture<Void> updateImagesFuture = CompletableFuture.runAsync(() -> {
					queryFactory.update(QPostImage.postImage)
						.set(QPostImage.postImage.post, post)
						.where(QPostImage.postImage.imageId.in(newPostImages.stream()
							.map(PostImage::getImageId)
							.collect(Collectors.toList())))
						.execute();
				}, imageExecutor);

				return updateImagesFuture.thenRun(() -> {
					post.getPostImages().addAll(newPostImages);
					postImageRepository.saveAll(newPostImages);
					postRepository.save(post);
				});
			});
	}

	@Transactional
	public CompletableFuture<Void> removeImageFromPost(Long shopId, Long postId, Long imageId) {
		Post post = queryFactory.selectFrom(QPost.post)
			.leftJoin(QPost.post.postImages, QPostImage.postImage).fetchJoin() // Post와 관련된 PostImage를 함께 가져오기
			.where(QPost.post.shop.shopId.eq(shopId).and(QPost.post.postId.eq(postId)))
			.fetchOne();
		ServiceUtils.checkNullValue(post);

		// Post와 관련된 PostImage 중 하나를 가져오기
		PostImage postImage = post.getPostImages().stream().findFirst().orElse(null);
		ServiceUtils.checkNullValue(postImage);

		String objectName = postImage.getObjectName();

		// 비동기 이미지 삭제
		return postImageService.deleteImageAsync(objectName)
			.thenRun(() -> transactionTemplate.execute(status -> {
				// QueryDSL을 사용하여 PostImage 엔티티 삭제 및 Post와의 관계 해제
				queryFactory.update(QPostImage.postImage)
					.setNull(QPostImage.postImage.post)
					.where(QPostImage.postImage.imageId.eq(imageId))
					.execute();

				queryFactory.delete(QPostImage.postImage)
					.where(QPostImage.postImage.imageId.eq(imageId))
					.execute();

				return null;
			}));
	}

	public List<PostDto.Response> listShopNews(Long shopId, Long memberId) {
		QPost post = QPost.post;
		QPostLikedMember postLikedMember = QPostLikedMember.postLikedMember;

		// Shop ID를 기준으로 Posts와 PostLikedMembers를 함께 조회
		List<Tuple> results = queryFactory
			.select(post, postLikedMember.post.postId)
			.from(post)
			.leftJoin(postLikedMember)
			.on(postLikedMember.post.postId.eq(post.postId).and(postLikedMember.member.memberId.eq(memberId)))
			.where(post.shop.shopId.eq(shopId))
			.fetch();

		// Post와 Liked 정보를 매핑
		return results.stream()
			.filter(tuple -> tuple != null && tuple.get(post) != null)
			.collect(Collectors.groupingBy(tuple -> Optional.ofNullable(tuple.get(post))))
			.entrySet().stream()
			.map(entry -> {
				Post postEntity = entry.getKey().orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
				boolean liked = entry.getValue().stream()
					.anyMatch(tuple -> tuple.get(postLikedMember.post.postId) != null);
				return PostDto.Response.from(postEntity, liked);
			})
			.collect(Collectors.toList());
	}

	public PostDto.Response viewShopNews(Long shopId, Long postId, Long memberId) {
		Post post = queryFactory.selectFrom(QPost.post)
			.join(QPost.post.shop, QShop.shop).fetchJoin()
			.where(QPost.post.shop.shopId.eq(shopId).and(QPost.post.postId.eq(postId)))
			.fetchOne();

		ServiceUtils.checkNullValue(post);

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

	@Transactional
	public void deletePost(Long shopId, Long postId) {
		Post post = queryFactory.select(QPost.post)
			.from(QPost.post)
			.join(QPost.post.shop, QShop.shop).fetchJoin()
			.where(QShop.shop.shopId.eq(shopId).and(QPost.post.postId.eq(postId)))
			.fetchOne();

		ServiceUtils.checkNullValue(post);

		List<CompletableFuture<Void>> deleteImageFutures = post.getPostImages().stream()
			.map(postImage -> postImageService.deleteImageAsync(postImage.getObjectName()))
			.toList();

		CompletableFuture.allOf(deleteImageFutures.toArray(new CompletableFuture[0]))
			.thenRun(() -> {
				post.getPostImages().clear();
				postRepository.delete(post);
			});
	}

	@Transactional
	public PostCommentDto.Response registerPostComment(Long shopId, Long postId, PostCommentDto.Request commentRequest,
		Long memberId) {
		Post post = queryFactory.selectFrom(QPost.post)
			.join(QPost.post.shop, QShop.shop).fetchJoin()
			.where(QPost.post.postId.eq(postId)
				.and(QShop.shop.shopId.eq(shopId)))
			.fetchOne();
		ServiceUtils.checkNullValue(post);

		PostComment postComment = PostComment.builder()
			.body(commentRequest.getBody())
			.post(post)
			.createdBy(memberId)
			.build();
		commentRepository.save(postComment);
		return PostCommentDto.Response.from(postComment);
	}

	@Transactional
	public PostCommentDto.Response updateComment(Long commentId, PostCommentDto.Request commentRequest) {
		PostComment postComment = queryFactory.selectFrom(QPostComment.postComment)
			.where(QPostComment.postComment.commentId.eq(commentId))
			.fetchOne();

		ServiceUtils.checkNullValue(postComment);

		postComment.updateBody(commentRequest.getBody());
		commentRepository.save(postComment); // 엔티티 저장 후 업데이트된 값을 반영
		return PostCommentDto.Response.from(postComment);
	}

	@Transactional
	public void deleteComment(Long shopId, Long postId, Long commentId) {
		queryFactory.delete(QPostComment.postComment)
			.where(QPostComment.postComment.commentId.eq(commentId))
			.execute();
	}

	@Transactional
	public void likePost(Long postId, Long memberId) {
		Post post = queryFactory.selectFrom(QPost.post)
			.where(QPost.post.postId.eq(postId))
			.fetchOne();

		ServiceUtils.checkNullValue(post);

		Member currentMember = queryFactory.selectFrom(QMember.member)
			.where(QMember.member.memberId.eq(memberId))
			.fetchOne();

		ServiceUtils.checkNullValue(currentMember);

		boolean alreadyLiked = !queryFactory.selectFrom(QPostLikedMember.postLikedMember)
			.where(QPostLikedMember.postLikedMember.post.postId.eq(postId)
				.and(QPostLikedMember.postLikedMember.member.memberId.eq(memberId)))
			.fetch().isEmpty();

		if (!alreadyLiked) {
			PostLikedMember postLike = new PostLikedMember();
			postLike.updatePost(post);
			postLike.updateMember(currentMember);
			postLikedMemberRepository.save(postLike);
			post.incrementLikes();
			postRepository.save(post);
		}
	}

	@Transactional
	public void unlikePost(Long postId, Long memberId) {
		PostLikedMember postLike = queryFactory.selectFrom(QPostLikedMember.postLikedMember)
			.where(QPostLikedMember.postLikedMember.post.postId.eq(postId)
				.and(QPostLikedMember.postLikedMember.member.memberId.eq(memberId)))
			.fetchOne();

		ServiceUtils.checkNullValue(postLike);

		postLikedMemberRepository.delete(postLike);

		Post post = queryFactory.selectFrom(QPost.post)
			.where(QPost.post.postId.eq(postId))
			.fetchOne();

		ServiceUtils.checkNullValue(post);

		post.decrementLikes();
		postRepository.save(post);
	}

}