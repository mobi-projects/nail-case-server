package com.nailcase.util;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;

public class ServiceUtils {

	public static <T> void checkNullValue(T thing) {
		if (thing == null) {
			throw new BusinessException(CommonErrorCode.NOT_FOUND);
		}
	}
}
