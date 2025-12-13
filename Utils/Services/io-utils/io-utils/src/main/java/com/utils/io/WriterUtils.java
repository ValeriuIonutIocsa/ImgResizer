package com.utils.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.utils.annotations.ApiMethod;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.io.ro_flag_clearers.FactoryReadOnlyFlagClearer;
import com.utils.log.Logger;

public final class WriterUtils {

	private WriterUtils() {
	}

	@ApiMethod
	public static BufferedWriter openBufferedWriter(
			final String filePathString) throws IOException {

		return openBufferedWriter(filePathString, StandardCharsets.UTF_8);
	}

	@ApiMethod
	public static BufferedWriter openBufferedWriter(
			final String filePathString,
			final Charset charset) throws IOException {

		final Path filePath = Paths.get(filePathString);
		return Files.newBufferedWriter(filePath, charset);
	}

	@ApiMethod
	public static boolean byteArrayToFile(
			final byte[] byteArray,
			final String filePathString) {

		boolean success = false;
		try {
			final Path filePath = Paths.get(filePathString);
			Files.write(filePath, byteArray);
			success = true;

		} catch (final Throwable throwable) {
			Logger.printError("failed to write byte array to file:" +
					System.lineSeparator() + filePathString);
			Logger.printThrowable(throwable);
		}
		return success;
	}

	@ApiMethod
	public static void tryStringToFile(
			final String string,
			final Charset charset,
			final String filePathString) {

		try {
			stringToFile(string, charset, filePathString);

		} catch (final Throwable throwable) {
			Logger.printError("failed to write string to file:" +
					System.lineSeparator() + filePathString);
			Logger.printThrowable(throwable);
		}
	}

	@ApiMethod
	public static void stringToFile(
			final String string,
			final Charset charset,
			final String filePathString) throws Exception {

		FactoryFolderCreator.getInstance().createParentDirectories(filePathString, false, true);
		FactoryReadOnlyFlagClearer.getInstance().clearReadOnlyFlagFile(filePathString, false, true);

		try (PrintStream printStream = StreamUtils.openPrintStream(filePathString, false, charset)) {
			printStream.print(string);
		}
	}

	@ApiMethod
	public static void stringToOutputStream(
			final String string,
			final OutputStream outputStream) throws IOException {

		stringToOutputStream(string, outputStream, StandardCharsets.UTF_8.name());
	}

	@ApiMethod
	public static void stringToOutputStream(
			final String string,
			final OutputStream outputStream,
			final String encoding) throws IOException {

		IOUtils.write(string, outputStream, encoding);
	}

	@ApiMethod
	public static void tryLineListToFile(
			final List<String> lineList,
			final Charset charset,
			final String filePathString) {

		FactoryFolderCreator.getInstance().createParentDirectories(filePathString, false, true);
		FactoryReadOnlyFlagClearer.getInstance().clearReadOnlyFlagFile(filePathString, false, true);

		try (PrintStream printStream = StreamUtils.openPrintStream(filePathString, false, charset)) {

			for (final String line : lineList) {

				printStream.print(line);
				printStream.println();
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to write line list to file:" +
					System.lineSeparator() + filePathString);
			Logger.printThrowable(throwable);
		}
	}
}
