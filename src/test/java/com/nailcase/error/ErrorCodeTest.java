package com.nailcase.error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.nailcase.exception.codes.ErrorCodeInterface;

public class ErrorCodeTest {

	private final List<Class<? extends ErrorCodeInterface>> errorCodeClasses;

	ErrorCodeTest() throws Exception {
		String packageName = "com.nailcase.exception.codes";
		errorCodeClasses = findAllErrorCodeClasses(packageName);
	}

	private List<Class<? extends ErrorCodeInterface>> findAllErrorCodeClasses(String packageName) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		URL resource = classLoader.getResource(path);
		assert resource != null;
		File directory = new File(resource.getFile());

		List<Class<? extends ErrorCodeInterface>> classes = new ArrayList<>();
		if (directory.exists()) {
			for (File file : Objects.requireNonNull(directory.listFiles())) {
				if (file.getName().endsWith(".class")) {
					String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
					Class<?> clazz = Class.forName(className);
					if (ErrorCodeInterface.class.isAssignableFrom(clazz) && clazz.isEnum()) {
						classes.add((Class<? extends ErrorCodeInterface>)clazz);
					}
				}
			}
		}
		return classes;
	}

	@Test
	void testUniqueErrorCodes() {
		Set<Integer> allCodes = new HashSet<>();
		Set<String> allMessages = new HashSet<>();

		for (Class<? extends ErrorCodeInterface> clazz : errorCodeClasses) {
			for (ErrorCodeInterface errorCode : clazz.getEnumConstants()) {
				assertTrue(allCodes.add(errorCode.getCode()),
					"중복된 에러 코드 발견: " + errorCode.getCode() + " in " + clazz.getSimpleName());
				assertTrue(allMessages.add(errorCode.getMessage()),
					"중복된 에러 메시지 발견: " + errorCode.getMessage() + " in " + clazz.getSimpleName());
			}
		}
	}

	@Test
	void testNoNullValues() {
		for (Class<? extends ErrorCodeInterface> clazz : errorCodeClasses) {
			for (ErrorCodeInterface errorCode : clazz.getEnumConstants()) {
				errorCode.getCode();
				assertNotNull(errorCode.getMessage(),
					"Null 에러 메시지 발견: " + clazz.getSimpleName() + "." + ((Enum<?>)errorCode).name());
			}
		}
	}

}
