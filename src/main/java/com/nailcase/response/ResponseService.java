package com.nailcase.response;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nailcase.exception.codes.ErrorCodeInterface;
import com.nailcase.exception.codes.ErrorResponse;

@Service
public class ResponseService {

	public <T> SingleResponse<T> getSingleResponse(T data) {
		SingleResponse<T> singleResponse = new SingleResponse<>();
		singleResponse.setData(data);
		setSuccessResponse(singleResponse);
		return singleResponse;
	}

	public <T> ListResponse<T> getListResponse(List<T> dataList) {
		ListResponse<T> listResponse = new ListResponse<>();
		listResponse.setDataList(dataList);
		setSuccessResponse(listResponse);
		return listResponse;
	}

	private void setSuccessResponse(CommonResponse response) {
		response.setCode(CommonResponse.SUCCESS_CODE);
		response.setSuccess(true);
		response.setMessage(CommonResponse.SUCCESS_MESSAGE);
	}

	public ErrorResponse getErrorResponse(Object error, int status) {
		if (error instanceof ErrorCodeInterface errorCode) {
			return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
		}
		return new ErrorResponse(status, "예상치 못한 에러 발생");
	}
}