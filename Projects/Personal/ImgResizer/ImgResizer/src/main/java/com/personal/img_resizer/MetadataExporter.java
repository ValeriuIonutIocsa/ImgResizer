package com.personal.img_resizer;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.PathUtils;
import com.utils.io.WriterUtils;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

class MetadataExporter {

	private static final Pattern JPG_IMAGE_WIDTH_PATTERN =
			Pattern.compile("<File:ImageWidth>(.*)</File:ImageWidth>");
	private static final Pattern JPG_IMAGE_HEIGHT_PATTERN =
			Pattern.compile("<File:ImageHeight>(.*)</File:ImageHeight>");

	private static final Pattern PNG_IMAGE_WIDTH_PATTERN =
			Pattern.compile("<PNG:ImageWidth>(.*)</PNG:ImageWidth>");
	private static final Pattern PNG_IMAGE_HEIGHT_PATTERN =
			Pattern.compile("<PNG:ImageHeight>(.*)</PNG:ImageHeight>");

	private final String filePathString;
	private final ImageType imageType;

	private int imageWidth;
	private int imageHeight;
	private String metadataXmlPathString;
	private boolean success;

	MetadataExporter(
			final String filePathString,
			final ImageType imageType) {

		this.filePathString = filePathString;
		this.imageType = imageType;
	}

	void work() {

		try {
			Logger.printProgress("exporting metadata of file:");
			Logger.printLine(filePathString);

			final String folderPathString = PathUtils.computeParentPath(filePathString);
			final Process process = new ProcessBuilder()
					.command("exiftool", "-X", filePathString)
					.directory(new File(folderPathString))
					.redirectError(ProcessBuilder.Redirect.INHERIT)
					.start();

			final InputStream inputStream = process.getInputStream();
			final ReadBytesHandlerLinesCollect readBytesHandlerLinesCollect =
					new ReadBytesHandlerLinesCollect();
			new InputStreamReaderThread("export metadata", inputStream, Charset.defaultCharset(),
					readBytesHandlerLinesCollect).start();

			final int exitCode = process.waitFor();

			final List<String> lineList = readBytesHandlerLinesCollect.getLineList();
			for (final String line : lineList) {

				final String trimmedLine = line.trim();

				if (imageType == ImageType.JPG || imageType == ImageType.HEIC) {

					final Matcher imageWidthMatcher = JPG_IMAGE_WIDTH_PATTERN.matcher(trimmedLine);
					if (imageWidthMatcher.matches()) {

						final String firstGroup = imageWidthMatcher.group(1);
						imageWidth = StrUtils.tryParsePositiveInt(firstGroup);
					}
					final Matcher imageHeightMatcher = JPG_IMAGE_HEIGHT_PATTERN.matcher(trimmedLine);
					if (imageHeightMatcher.matches()) {

						final String firstGroup = imageHeightMatcher.group(1);
						imageHeight = StrUtils.tryParsePositiveInt(firstGroup);
					}

				} else if (imageType == ImageType.PNG) {

					final Matcher imageWidthMatcher = PNG_IMAGE_WIDTH_PATTERN.matcher(trimmedLine);
					if (imageWidthMatcher.matches()) {

						final String firstGroup = imageWidthMatcher.group(1);
						imageWidth = StrUtils.tryParsePositiveInt(firstGroup);
					}
					final Matcher imageHeightMatcher = PNG_IMAGE_HEIGHT_PATTERN.matcher(trimmedLine);
					if (imageHeightMatcher.matches()) {

						final String firstGroup = imageHeightMatcher.group(1);
						imageHeight = StrUtils.tryParsePositiveInt(firstGroup);
					}
				}
			}

			Logger.printLine("image width: " + StrUtils.positiveIntToString(imageWidth, true));
			Logger.printLine("image height: " + StrUtils.positiveIntToString(imageHeight, true));

			metadataXmlPathString = filePathString + ".xml";
			final String metadataXmlContent = StringUtils.join(lineList, System.lineSeparator());
			WriterUtils.stringToFile(metadataXmlContent, Charset.defaultCharset(), metadataXmlPathString);

			success = exitCode == 0;

		} catch (final Exception exc) {
			Logger.printError("failed to export metadata for file:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
	}

	int getImageWidth() {
		return imageWidth;
	}

	int getImageHeight() {
		return imageHeight;
	}

	String getMetadataXmlPathString() {
		return metadataXmlPathString;
	}

	boolean isSuccess() {
		return success;
	}
}
