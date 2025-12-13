package com.utils.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class ResourceFileUtils {

	private ResourceFileUtils() {
	}

	@ApiMethod
	public static List<String> resourceFileToLineList(
			final String resourceFilePathString,
			final Charset charset) {

		final List<String> lineList = new ArrayList<>();
		try {
			final InputStream inputStream = resourceFileToInputStream(resourceFilePathString);
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, charset))) {

				String line;
				while ((line = bufferedReader.readLine()) != null) {
					lineList.add(line);
				}
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to get resource file line list for:" +
					System.lineSeparator() + resourceFilePathString);
			Logger.printThrowable(throwable);
		}
		return lineList;
	}

	@ApiMethod
	public static String resourceFileToString(
			final String resourceFilePathString) {

		String str = "";
		try {
			final InputStream inputStream = resourceFileToInputStream(resourceFilePathString);
			str = ReaderUtils.inputStreamToString(inputStream);

		} catch (final Throwable throwable) {
			Logger.printError("failed to get resource file content for:" +
					System.lineSeparator() + resourceFilePathString);
			Logger.printThrowable(throwable);
		}
		return str;
	}

	@ApiMethod
	public static byte[] resourceFileToByteArray(
			final String resourceFilePathString) {

		byte[] bytes = null;
		try {
			final InputStream inputStream = resourceFileToInputStream(resourceFilePathString);
			bytes = IOUtils.toByteArray(inputStream);

		} catch (final Throwable throwable) {
			Logger.printError("failed to get resource file content for:" +
					System.lineSeparator() + resourceFilePathString);
			Logger.printThrowable(throwable);
		}
		return bytes;
	}

	@ApiMethod
	public static InputStream resourceFileToInputStream(
			final String resourceFilePathString) {

		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resourceFilePathString);
	}

	@ApiMethod
	public static URL resourceFileToUrl(
			final String resourceFilePathString) {

		return Thread.currentThread().getContextClassLoader()
				.getResource(resourceFilePathString);
	}
}
