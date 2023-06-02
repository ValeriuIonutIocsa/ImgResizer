package com.personal.img_resizer;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

final class AppStartImgResizer {

	private AppStartImgResizer() {
	}

	public static void main(
			final String[] args) {

		Logger.setDebugMode(true);

		boolean success = true;
		try {
			final Instant start = Instant.now();
			Logger.printProgress("--> starting ImgResizer");

			if (args.length >= 1 && "-help".equals(args[0])) {
				final String helpMessage = createHelpMessage();
				Logger.printLine(helpMessage);

			} else {
				if (args.length < 3) {
					final String helpMessage = createHelpMessage();
					Logger.printError("insufficient arguments" + System.lineSeparator() + helpMessage);
					success = false;

				} else {
					final String lengthString = args[0];
					final int length = StrUtils.tryParsePositiveInt(lengthString);
					if (length < 0) {
						Logger.printError("invalid length");
						success = false;

					} else {
						final String inputPathString = PathUtils.computeNormalizedPath("input path", args[1]);
						final String outputPathString = PathUtils.computeNormalizedPath("output path", args[2]);
						success = work(length, inputPathString, outputPathString);
					}
				}
			}

			Logger.printFinishMessage(start);

		} catch (final Exception exc) {
			Logger.printError("error occurred while running image resizer");
			Logger.printException(exc);
		}
		if (!success) {
			System.exit(1);
		}
	}

	private static String createHelpMessage() {

		return "usage: img_resizer <length> <input_path> <output_path>";
	}

	private static boolean work(
			final int length,
			final String inputPathString,
			final String outputPathString) {

		boolean success = true;

		Logger.printLine("input path: " + inputPathString);
		Logger.printLine("output path: " + outputPathString);
		Logger.printLine("length: " + length);

		if (IoUtils.directoryExists(inputPathString)) {

			success = FactoryFolderCreator.getInstance()
					.createDirectories(outputPathString, false, true);
			if (success) {

				boolean foundImages = false;
				final List<String> filePathStringList =
						ListFileUtils.listFilesRecursively(inputPathString, Files::isRegularFile);
				for (final String filePathString : filePathStringList) {

					final ImageType imageType = FactoryImageType.computeImageType(filePathString);
					if (imageType != null) {

						foundImages = true;

						final String fileName = PathUtils.computeFileName(filePathString);
						final String outputFilePathString = PathUtils.computePath(outputPathString, fileName);

						final boolean resizedImageSuccess =
								resizeImage(filePathString, outputFilePathString, imageType, length);
						if (!resizedImageSuccess) {
							success = false;
						}
					}
				}
				if (!foundImages) {
					Logger.printWarning("found no image files in the input folder");
				}
			}

		} else if (IoUtils.fileExists(inputPathString)) {

			final ImageType imageType = FactoryImageType.computeImageType(inputPathString);
			if (imageType != null) {

				success = FactoryFolderCreator.getInstance()
						.createParentDirectories(outputPathString, false, true);
				if (success) {

					final boolean resizedImageSuccess =
							resizeImage(inputPathString, outputPathString, imageType, length);
					if (!resizedImageSuccess) {
						success = false;
					}
				}

			} else {
				Logger.printWarning("unsupported file type of input file");
			}

		} else {
			Logger.printWarning("input file does not exist");
		}
		return success;
	}

	private static boolean resizeImage(
			final String filePathString,
			final String outputFilePathString,
			final ImageType imageType,
			final int length) {

		boolean success = false;
		try {
			Logger.printProgress("copying image file:");
			Logger.printLine(filePathString);
			Logger.printLine("to:");
			Logger.printLine(outputFilePathString);

			success = FactoryFileDeleter.getInstance().deleteFile(outputFilePathString, false, true);
			if (success) {

				final MetadataExporter metadataExporter = new MetadataExporter(filePathString, imageType);
				metadataExporter.work();
				success = metadataExporter.isSuccess();
				if (success) {

					final String scale;
					final int imageWidth = metadataExporter.getImageWidth();
					final int imageHeight = metadataExporter.getImageHeight();
					if (imageWidth > imageHeight) {
						scale = "scale=-1:" + length;
					} else {
						scale = "scale=" + length + ":-1";
					}

					final Process process = new ProcessBuilder()
							.command("ffmpeg", "-i", filePathString,
									"-movflags", "use_metadata_tags", "-map_metadata", "0",
									"-vf", scale, outputFilePathString)
							.directory(new File(outputFilePathString).getParentFile())
							.redirectOutput(ProcessBuilder.Redirect.DISCARD)
							.redirectError(ProcessBuilder.Redirect.DISCARD)
							.start();
					final int exitCode = process.waitFor();
					success = exitCode == 0;
					if (success) {

						final String metadataXmlPathString = metadataExporter.getMetadataXmlPathString();
						final MetadataImporter metadataImporter =
								new MetadataImporter(outputFilePathString, metadataXmlPathString);
						metadataImporter.work();

						success = metadataImporter.isSuccess();
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to resize image " +
					System.lineSeparator() + filePathString +
					System.lineSeparator() + "to:" +
					System.lineSeparator() + outputFilePathString);
			Logger.printException(exc);
		}
		return success;
	}
}
