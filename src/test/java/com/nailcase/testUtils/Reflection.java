package com.nailcase.testUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 객체의 필드와 메서드에 접근하는 유틸리티 메서드를 제공합니다.
 * 이 클래스는 주어진 객체의 특정 필드 값을 설정하거나 가져오고, 특정 메서드를 호출할 수 있는 기능을 제공합니다.
 * 테스트 환경에서 private 필드와 메서드에 접근하여 테스트할 수 있도록 지원합니다.
 */
public final class Reflection {

	/**
	 * 주어진 객체의 특정 필드에 값을 설정하는 메소드입니다.
	 *
	 * @param target 값을 설정할 객체
	 * @param fieldName 값을 설정할 필드의 이름
	 * @param value 설정할 값
	 * @throws Exception 필드를 찾지 못하거나 접근할 수 없는 경우 발생할 수 있는 예외
	 */
	public static void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = getFieldFromClass(target.getClass(), fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	/**
	 * 주어진 객체의 특정 필드 값을 가져오는 메소드입니다.
	 *
	 * @param target 값을 가져올 객체
	 * @param fieldName 값을 가져올 필드의 이름
	 * @return 필드의 값
	 * @throws Exception 필드를 찾지 못하거나 접근할 수 없는 경우 발생할 수 있는 예외
	 */
	public static Object getField(Object target, String fieldName) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(target);
	}

	/**
	 * 주어진 객체의 특정 메소드를 호출하는 메소드입니다.
	 *
	 * @param target 메소드를 호출할 객체
	 * @param methodName 호출할 메소드의 이름
	 * @param parameterTypes 메소드의 파라미터 타입들
	 * @param args 메소드 호출에 사용될 인자들
	 * @return 메소드 호출 결과
	 * @throws Exception 메소드를 찾지 못하거나 접근할 수 없는 경우 발생할 수 있는 예외
	 */
	public static Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes,
		Object... args) throws Exception {
		Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(target, args);
	}

	/**
	 * 주어진 클래스의 새로운 인스턴스를 생성하는 메소드입니다.
	 *
	 * @param clazz 인스턴스를 생성할 클래스
	 * @return 생성된 객체 인스턴스
	 * @throws Exception 클래스를 찾지 못하거나 접근할 수 없는 경우 발생할 수 있는 예외
	 */
	public static Object createInstance(Class<?> clazz) throws Exception {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		return constructor.newInstance();
	}

	private static Field getFieldFromClass(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}
}
