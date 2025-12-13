package com.utils.reflect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	@ApiMethod
	public static <
			SingletonSubType> void fillSingletonList(
					final String prefix,
					final Class<SingletonSubType> singletonSubTypeClass,
					final List<SingletonSubType> singletonList) {

		final Reflections reflections = new Reflections(prefix);
		final Set<Class<? extends SingletonSubType>> singletonClassSet =
				reflections.getSubTypesOf(singletonSubTypeClass);
		for (final Class<? extends SingletonSubType> singletonClass : singletonClassSet) {

			try {
				Field fieldInstance = null;
				try {
					fieldInstance = singletonClass.getField("INSTANCE");
				} catch (final Throwable ignored) {
				}
				if (fieldInstance != null) {

					final SingletonSubType singleton =
							singletonSubTypeClass.cast(fieldInstance.get(null));
					singletonList.add(singleton);
				}

			} catch (final Throwable throwable) {
				Logger.printError("failed to get instance of " +
						singletonSubTypeClass.getSimpleName());
				Logger.printThrowable(throwable);
			}
		}
	}
}
