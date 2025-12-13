package com.utils.string.compression;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GZipCompressionUtilsTest {

	@Test
	void testCompress() {

		final String input = "ABC BCD CBD EDC";
		final byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

		final byte[] compressedBytes = GZipCompressionUtils.compress(bytes);
		final byte[] decompressedBytes = GZipCompressionUtils.decompress(compressedBytes);
		Assertions.assertArrayEquals(bytes, decompressedBytes);
	}
}
