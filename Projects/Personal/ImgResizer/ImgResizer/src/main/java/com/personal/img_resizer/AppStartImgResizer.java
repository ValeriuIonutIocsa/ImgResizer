package com.personal.img_resizer;

import java.nio.file.FileVisitResult;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
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
			Logger.printProgress("starting ImgResizer");

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
						final boolean verbose = args.length >= 4 && "-verbose".equals(args[3]);
						success = work(length, inputPathString, outputPathString, verbose);
					}
				}
			}

			Logger.printFinishMessage(start);

		} catch (final Throwable throwable) {
			Logger.printError("error occurred while running image resizer");
			Logger.printThrowable(throwable);
		}
		if (!success) {
			System.exit(1);
		}
	}

	private static String createHelpMessage() {

		return "usage: img_resizer <length> <input_path> <output_path> (-verbose)";
	}

	private static boolean work(
			final int length,
			final String inputPathString,
			final String outputPathString,
			final boolean verbose) {

		boolean success = true;

		Logger.printLine("input path: " + inputPathString);
		Logger.printLine("output path: " + outputPathString);
		Logger.printLine("length: " + length);
		Logger.printLine("verbose: " + verbose);

		if (IoUtils.directoryExists(inputPathString)) {

			boolean foundImages = false;
			final List<String> filePathStringList = new ArrayList<>();
			ListFileUtils.visitFilesRecursively(inputPathString,
					dirPath -> FileVisitResult.CONTINUE,
					filePath -> {
						final String filePathString = filePath.toString();
						filePathStringList.add(filePathString);
						return FileVisitResult.CONTINUE;
					});
			for (final String filePathString : filePathStringList) {

				Logger.printNewLine();

				final String relativePath = PathUtils.computeRelativePath(inputPathString, filePathString);
				String outputFilePathString = PathUtils.computePath(outputPathString, relativePath);

				final ImageType imageType = FactoryImageType.computeImageType(filePathString);
				if (imageType != null) {

					foundImages = true;
					outputFilePathString = PathUtils.computePathWoExt(outputFilePathString) + ".jpg";

					final boolean resizedImageSuccess =
							ImgResizer.work(filePathString, outputFilePathString, verbose, imageType, length);
					if (!resizedImageSuccess) {
						success = false;
					}

				} else {
					success = FactoryFileCopier.getInstance()
							.copyFile(filePathString, outputFilePathString, true, true, true);
				}
			}
			if (!foundImages) {
				Logger.printWarning("found no image files in the input folder");
			}

		} else if (IoUtils.fileExists(inputPathString)) {

			final ImageType imageType = FactoryImageType.computeImageType(inputPathString);
			if (imageType != null) {

				final boolean resizedImageSuccess =
						ImgResizer.work(inputPathString, outputPathString, verbose, imageType, length);
				if (!resizedImageSuccess) {
					success = false;
				}

			} else {
				Logger.printWarning("unsupported file type of input file");
			}

		} else {
			Logger.printWarning("input file does not exist");
		}
		return success;
	}
}
