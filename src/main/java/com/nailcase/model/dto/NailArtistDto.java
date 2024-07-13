package com.nailcase.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NailArtistDto {
	private Long nailArtistId;
	private String name;
	private String email;
	private String profileImgUrl;
	private Role role;
	private String socialId;
	private SocialType socialType;

	public static NailArtistDto fromEntity(NailArtist nailArtist) {
		NailArtistDto dto = new NailArtistDto();
		dto.setNailArtistId(nailArtist.getNailArtistId());
		dto.setName(nailArtist.getName());
		dto.setEmail(nailArtist.getEmail());
		dto.setProfileImgUrl(nailArtist.getProfileImgUrl());
		dto.setRole(nailArtist.getRole());
		dto.setSocialId(nailArtist.getSocialId());
		dto.setSocialType(nailArtist.getSocialType());
		return dto;
	}

	@Data
	public static class Response {

		private Long id;

		private String nickname;

		private Long near;

		public static Response fromEntity(NailArtist nailArtist) {
			NailArtistDto.Response response = new NailArtistDto.Response();
			response.setId(nailArtist.getNailArtistId());
			response.setNickname(nailArtist.getName());
			return response;
		}

		public Response setNear(Long near) {
			this.near = near;
			return this;
		}

	}

	@Data
	public static class ListResponse {
		private Long nailArtistId;
		private String nickname;

		public static ListResponse fromEntity(NailArtist nailArtist) {
			ListResponse response = new ListResponse();
			response.setNailArtistId(nailArtist.getNailArtistId());
			response.setNickname(nailArtist.getName());
			return response;
		}
	}

}