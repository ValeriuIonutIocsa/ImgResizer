package com.utils.obj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class ObjUtils {

	private ObjUtils() {
	}

	/**
	 * similar to Objects.requireNonNullElse but skips the check if the default object is null or not
	 * 
	 * @param obj
	 *            the object that can be null or not
	 * @param defaultObj
	 *            default value to be returned if obj is null
	 * @param <T>
	 *            type of obj
	 * @return defaultObj if obj is null, obj otherwise
	 */
	@ApiMethod
	public static <
			T> T nonNullElse(
					final T obj,
					final T defaultObj) {

		final T result;
		if (obj != null) {
			result = obj;
		} else {
			result = defaultObj;
		}
		return result;
	}

	@ApiMethod
	public static String serialize(
			final Object object) {

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

			objectOutputStream.writeObject(object);

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
		final byte[] byteArray = byteArrayOutputStream.toByteArray();
		return Base64.getEncoder().encodeToString(byteArray);
	}

	@ApiMethod
	public static <
			ObjectT> ObjectT deserialize(
					final String serializedObject,
					final Class<ObjectT> objectClass) {

		ObjectT objectT = null;
		final byte[] byteArray = Base64.getDecoder().decode(serializedObject);
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

			final Object object = objectInputStream.readObject();
			if (objectClass.isInstance(object)) {
				objectT = objectClass.cast(object);
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
		return objectT;
	}
}
