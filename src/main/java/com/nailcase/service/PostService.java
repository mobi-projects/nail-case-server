package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.exception.codes.PostErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Post;
import com.nailcase.model.entity.PostComment;
import com.nailcase.model.entity.PostImage;
import com.nailcase.model.entity.PostLikedMember;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.PostCommentsRepository;
import com.nailcase.repository.PostImageRepository;
import com.nailcase.repository.PostLikedMemberRepository;
import com.nailcase.repository.PostsRepository;
import com.nailcase.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

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

	public List<PostImageDto> uploadImages(List<MultipartFile> files) {
		List<PostImage> tempImages = files.stream()
			.map(file -> {
				PostImage tempImage = new PostImage();
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
		Post post = Post.builder()
			.title(postRequest.getTitle())
			.category(postRequest.getCategory())
			.contents(postRequest.getContents())
			.build();
		postRepository.save(post);

		if (postRequest.getImageIds() != null && !postRequest.getImageIds().isEmpty()) {
			List<PostImage> postImages = postRequest.getImageIds().stream()
				.map(imageId -> {
					PostImage image = postImageRepository.findById(imageId)
						.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));
					image.setPost(post); // 게시물과 이미지 연결
					return image;
				})
				.collect(Collectors.toList());

			postImageRepository.saveAll(postImages);
		}

		return PostDto.Response.from(post, 0L);
	}

	public PostDto.Response updatePost(Long shopId, Long postId, PostDto.Request postRequest) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
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
			.map(imageId -> postImageRepository.findById(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)))
			.collect(Collectors.toList());
		post.getPostImages().addAll(newPostImages);
		postImageRepository.saveAll(newPostImages);

		return PostDto.Response.from(post, post.getViews());
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
		PostImage postImage = postImageRepository.findById(imageId)
			.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

		String objectName = postImage.getObjectName();
		postImageService.deleteImage(objectName);

		post.removePostImage(postImage);
		postImageRepository.delete(postImage);
	}

	public List<PostDto.Response> listShopNews(Long shopId) {
		// List<Post> posts = postRepository.findByShopId();
		List<Post> posts = postRepository.findAll();
		return posts.stream()
			.map(post -> PostDto.Response.from(post, post.getViews()))
			.collect(Collectors.toList());
	}

	public PostDto.Response viewShopNews(Long shopId, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));

		// 조회수 증가 로직
		String key = "post:view:count:" + postId;  // Redis에서 조회수를 저장할 키
		long offset = SecurityUtil.getCurrentMemberId();
		Boolean alreadyViewed = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyViewed) {
			bitmapService.setBit(key, offset, true);
			Long viewCount = bitmapService.bitCount(key).orElse(0L);
			post.incrementViews(viewCount);
			postRepository.save(post);
		}

		Long currentViewCount = bitmapService.bitCount(key).orElse(0L);
		return PostDto.Response.from(post, currentViewCount);
	}

	public void deletePost(Long shopId, Long postId) {
		postRepository.deleteById(postId);
	}

	public PostCommentDto.Response registerComment(Long shopId, Long postId, PostCommentDto.Request commentRequest) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		PostComment postComment = PostComment.builder()
			.body(commentRequest.getBody())
			.post(post)
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

	@Transactional
	public void likePost(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		Member currentMember = memberRepository.findById(SecurityUtil.getCurrentMemberId())
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		boolean alreadyLiked = postLikedMemberRepository.existsByPost_PostIdAndMember_MemberId(postId,
			currentMember.getMemberId());
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
	public void unlikePost(Long postId) {
		Member currentMember = memberRepository.findById(SecurityUtil.getCurrentMemberId())
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		PostLikedMember postLike = postLikedMemberRepository.findByPost_PostIdAndMember_MemberId(postId,
				currentMember.getMemberId())
			.orElseThrow(() -> new BusinessException(PostErrorCode.LIKE_NOT_FOUND));

		postLikedMemberRepository.delete(postLike);
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(PostErrorCode.NOT_FOUND));
		post.decrementLikes();
		postRepository.save(post);
	}
}