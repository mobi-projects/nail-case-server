package com.nailcase.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {
	private final int SIZE = 2;
	private int page;
	private String sort;
	private Sort.Direction direction = Sort.Direction.ASC;

	public static PageRequestDtoBuilder builder() {
		return new PageRequestDtoBuilder();
	}

	public Pageable toPageable() {
		if (sort == null)
			return PageRequest.of(page, SIZE);
		return PageRequest.of(page, SIZE, direction, sort);
	}

	public static class PageRequestDtoBuilder {
		private int page;
		private String sort;
		private Sort.Direction direction = Sort.Direction.ASC;

		public PageRequestDtoBuilder page(int page) {
			this.page = page - 1;
			return this;
		}

		public PageRequestDtoBuilder sort(String sort) {
			this.sort = sort;
			return this;
		}

		public PageRequestDtoBuilder direction(String direction) {
			if ("DESC".equals(direction))
				this.direction = Sort.Direction.DESC;
			else
				this.direction = Sort.Direction.ASC;
			return this;
		}

		public PageRequestDto build() {
			PageRequestDto pageRequestDto = new PageRequestDto();
			pageRequestDto.page = this.page;
			pageRequestDto.sort = this.sort;
			pageRequestDto.direction = this.direction;
			return pageRequestDto;
		}
	}
}