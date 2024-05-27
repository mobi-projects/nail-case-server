package com.nailcase.response;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ResponseTest {

	@Test
	public void testCommonResponse() {
		CommonResponse response = new CommonResponse();
		response.setSuccess(true);
		response.setCode(CommonResponse.SUCCESS_CODE);
		response.setMessage(CommonResponse.SUCCESS_MESSAGE);

		assertTrue(response.isSuccess());
		assertEquals(CommonResponse.SUCCESS_CODE, response.getCode());
		assertEquals(CommonResponse.SUCCESS_MESSAGE, response.getMessage());
	}
}