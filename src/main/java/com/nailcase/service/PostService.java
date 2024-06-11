package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.domain.post.Post;
import com.nailcase.domain.post.comment.PostComment;
import com.nailcase.domain.post.comment.dto.PostCommentDto;
import com.nailcase.domain.post.dto.PostDto;
import com.nailcase.repository.PostCommentsRepository;
import com.nailcase.repository.PostsRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostsRepository postRepository;
	private final PostCommentsRepository commentRepository;

	public PostDto.Response registerPost(Long shopId, PostDto.Request postRequest) {
		Post post = Post.builder()
			.title(postRequest.getTitle())
			.body(postRequest.getBody())
			.likeCounts(0L)
			.liked(false)
			.build();
		postRepository.save(post);
		return PostDto.Response.from(post);
	}

	public PostDto.Response updatePost(Long shopId, Long postId, PostDto.Request postRequest) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		post = post.builder()
			.title(postRequest.getTitle())
			.body(postRequest.getBody())
			.build();
		return PostDto.Response.from(post);
	}

	public List<PostDto.Response> listShopNews(Long shopId) {
		List<Post> posts = postRepository.findByShopId(shopId);
		return posts.stream()
			.map(PostDto.Response::from)
			.collect(Collectors.toList());
	}

	public PostDto.Response viewShopNews(Long shopId, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		return PostDto.Response.from(post);
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

	public PostCommentDto.Response updateComment(Long shopId, Long postId, Long commentId,
		PostCommentDto.Request commentRequest) {
		PostComment postComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
		postComment.updateBody(commentRequest.getBody());
		return PostCommentDto.Response.from(postComment);
	}

	public void deleteComment(Long shopId, Long postId, Long commentId) {
		commentRepository.deleteById(commentId);
	}
}