package com.utils.string.converters;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.utils.annotations.ApiMethod;

public final class ConverterByteArray {

	private ConverterByteArray() {
	}

	@ApiMethod
	public static String byteArrayToString(
			final byte[] bytes) {

		final byte[] encodedBytes = Base64.getEncoder().encode(bytes);
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}

	@ApiMethod
	public static byte[] stringToByteArray(
			final String str) {

		final byte[] encodedBytes = str.getBytes(StandardCharsets.UTF_8);
		return Base64.getDecoder().decode(encodedBytes);
	}
}
