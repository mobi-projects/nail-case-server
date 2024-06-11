package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.entity.post.Post;
import com.nailcase.model.entity.post.PostImage;
import com.nailcase.model.entity.post.comment.PostComment;
import com.nailcase.model.entity.post.comment.dto.PostCommentDto;
import com.nailcase.model.entity.post.dto.PostDto;
import com.nailcase.repository.PostCommentsRepository;
import com.nailcase.repository.PostImageRepository;
import com.nailcase.repository.PostsRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostsRepository postRepository;
	private final PostCommentsRepository commentRepository;
	private final PostImageRepository postImageRepository;
	private final BitmapService bitmapService;
	private final MinioService minioService;

	public PostDto.Response registerPost(Long shopId, PostDto.Request postRequest) {
		Post post = Post.builder()
			.title(postRequest.getTitle())
			.contents(postRequest.getContents())
			.likes(0L)
			.views(0L)
			.build();

		List<PostImage> postImages = postRequest.getImageUrls().stream()
			.map(url -> PostImage.builder().url(url).post(post).build())
			.collect(Collectors.toList());
		post.setPostImages(postImages);

		postRepository.save(post);
		return PostDto.Response.from(post, 0L);
	}

	public PostDto.Response updatePost(Long shopId, Long postId, PostDto.Request postRequest) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		post.updateTitle(postRequest.getTitle());
		post.updateContents(postRequest.getContents());

		List<PostImage> postImages = postRequest.getImageUrls().stream()
			.map(url -> PostImage.builder().url(url).post(post).build())
			.collect(Collectors.toList());
		post.setPostImages(postImages);

		postRepository.save(post);
		return PostDto.Response.from(post, post.getViews());
	}

	public void addImageToPost(Long postId, MultipartFile file) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		if (post.getPostImages().size() >= 5) {
			throw new IllegalArgumentException("Cannot add more than 5 images to a post");
		}
		String objectName = "post-images/" + postId + "/" + file.getOriginalFilename();
		String url = minioService.uploadFile("your-bucket-name", objectName, file);
		PostImage postImage = PostImage.builder().url(url).post(post).build();
		post.addPostImage(postImage);
		postImageRepository.save(postImage);
		postRepository.save(post);
	}

	public void removeImageFromPost(Long postId, Long imageId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		PostImage postImage = postImageRepository.findById(imageId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid image ID"));
		String objectName =
			"post-images/" + postId + "/" + postImage.getUrl().substring(postImage.getUrl().lastIndexOf('/') + 1);
		minioService.deleteFile("your-bucket-name", objectName);
		post.removePostImage(postImage);
		postImageRepository.delete(postImage);
		postRepository.save(post);
	}

	public List<PostDto.Response> listShopNews(Long shopId) {
		List<Post> posts = postRepository.findByShopId(shopId);
		return posts.stream()
			.map(post -> PostDto.Response.from(post, post.getViews()))
			.collect(Collectors.toList());
	}

	public PostDto.Response viewShopNews(Long shopId, Long postId, Long memberId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("조회된 게시물이 없습니다."));

		// 조회수 증가 로직
		String key = "post:view:count:" + postId;  // Redis에서 조회수를 저장할 키
		long offset = memberId; // 사용자 ID를 오프셋으로 사용
		Boolean alreadyViewed = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyViewed) {
			bitmapService.setBit(key, offset, true);
			Long viewCount = bitmapService.bitCount(key).orElse(0L);
			post.incrementViews(viewCount);  // 조회수 증가 메서드 호출
			postRepository.save(post); // 엔티티 저장 후 업데이트된 값을 반영
		}

		Long currentViewCount = bitmapService.bitCount(key).orElse(0L);
		return PostDto.Response.from(post, currentViewCount);
	}

	public void deletePost(Long shopId, Long postId) {
		postRepository.deleteById(postId);
	}

	public PostCommentDto.Response registerComment(Long shopId, Long postId, PostCommentDto.Request commentRequest) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
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
			.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
		postComment.updateBody(commentRequest.getBody());
		commentRepository.save(postComment); // 엔티티 저장 후 업데이트된 값을 반영
		return PostCommentDto.Response.from(postComment);
	}

	public void deleteComment(Long shopId, Long postId, Long commentId) {
		commentRepository.deleteById(commentId);
	}

	public void likePost(Long postId, Long memberId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("조회된 게시물이 없습니다."));
		String key = "post:like:count:" + postId;
		long offset = memberId;
		Boolean alreadyLiked = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyLiked) {
			bitmapService.setBit(key, offset, true);
			post.incrementLikes();
			postRepository.save(post);
		}
	}
}
