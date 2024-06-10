package com.personal.img_resizer;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
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
						final boolean verbose = args.length >= 4 && "-verbose".equals(args[3]);
						success = work(length, inputPathString, outputPathString, verbose);
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

		if (IoUtils.directoryExists(inputPathString)) {

			boolean foundImages = false;
			final List<String> filePathStringList = new ArrayList<>();
			ListFileUtils.visitFilesRecursively(inputPathString,
					dirPath -> {
					},
					filePath -> {
						final String filePathString = filePath.toString();
						filePathStringList.add(filePathString);
					});
			for (final String filePathString : filePathStringList) {

				final String relativePath = PathUtils.computeRelativePath(inputPathString, filePathString);
				String outputFilePathString = PathUtils.computePath(outputPathString, relativePath);

				final ImageType imageType = FactoryImageType.computeImageType(filePathString);
				if (imageType != null) {

					foundImages = true;
					outputFilePathString = PathUtils.computePathWoExt(outputFilePathString) + ".jpg";

					final boolean resizedImageSuccess =
							resizeImage(filePathString, outputFilePathString, verbose, imageType, length);
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
						resizeImage(inputPathString, outputPathString, verbose, imageType, length);
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

	private static boolean resizeImage(
			final String filePathString,
			final String outputFilePathString,
			final boolean verbose,
			final ImageType imageType,
			final int length) {

		boolean success = false;
		final List<String> tmpFilePathStringList = new ArrayList<>();
		try {
			Logger.printNewLine();
			Logger.printProgress("copying image file:");
			Logger.printLine(filePathString);
			Logger.printLine("to:");
			Logger.printLine(outputFilePathString);

			success = FactoryFileDeleter.getInstance()
					.deleteFile(outputFilePathString, false, true);
			if (success) {

				success = FactoryFolderCreator.getInstance()
						.createParentDirectories(outputFilePathString, false, true);
				if (success) {

					final String jpgFilePathString;
					if (imageType != ImageType.JPG) {

						jpgFilePathString = PathUtils.computePathWoExt(filePathString) + ".jpg";
						tmpFilePathStringList.add(jpgFilePathString);
						success = convertImageToJpg(filePathString, jpgFilePathString);

					} else {
						jpgFilePathString = filePathString;
					}
					if (success) {

						final MetadataExporter metadataExporter =
								new MetadataExporter(jpgFilePathString, ImageType.JPG);
						metadataExporter.work();

						final String metadataXmlPathString =
								metadataExporter.getMetadataXmlPathString();
						tmpFilePathStringList.add(metadataXmlPathString);

						success = metadataExporter.isSuccess();
						if (success) {

							final ResizeImageOutput resizeImageL2Return = resizeImageL2(
									jpgFilePathString, outputFilePathString, metadataExporter, length, verbose);
							success = resizeImageL2Return.success();
							if (success) {

								final boolean needToImportMetadata = resizeImageL2Return.needToImportMetadata();
								if (needToImportMetadata) {

									final MetadataImporter metadataImporter =
											new MetadataImporter(outputFilePathString, metadataXmlPathString);
									metadataImporter.work();

									success = metadataImporter.isSuccess();
								}
							}
						}
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to copy image file:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);

		} finally {
			if (!verbose) {
				for (final String tmpFilePathString : tmpFilePathStringList) {
					FactoryFileDeleter.getInstance().deleteFile(tmpFilePathString, true, true);
				}
			}
		}
		return success;
	}

	private static boolean convertImageToJpg(
			final String filePathString,
			final String jpgFilePathString) {

		boolean success = false;
		try {
			Logger.printProgress("converting image to JPG");

			ProcessStarter.setGlobalSearchPath("D:\\IVI_MISC\\Apps\\ImageMagick");
			final ConvertCmd convertCmd = new ConvertCmd();

			final IMOperation imOperation = new IMOperation();

			imOperation.addImage(filePathString);

			imOperation.quality(100.0);

			imOperation.addImage(jpgFilePathString);

			convertCmd.run(imOperation);
			success = true;

		} catch (final Exception exc) {
			Logger.printError("failed to convert image to JPG");
			Logger.printException(exc);
		}
		return success;
	}

	private static ResizeImageOutput resizeImageL2(
			final String jpgFilePathString,
			final String outputFilePathString,
			final MetadataExporter metadataExporter,
			final int length,
			final boolean verbose) {

		boolean success = false;
		boolean needToImportMetadata = false;
		try {
			final int imageWidth = metadataExporter.getImageWidth();
			final int imageHeight = metadataExporter.getImageHeight();
			final boolean needToResizeImage =
					checkNeedToResizeImage(imageWidth, imageHeight, length, jpgFilePathString);
			if (!needToResizeImage) {
				success = FactoryFileCopier.getInstance()
						.copyFile(jpgFilePathString, outputFilePathString, true, false, true);

			} else {
				Logger.printProgress("resizing image");

				final String scale;
				if (imageWidth > imageHeight) {
					scale = "scale=-1:" + length;
				} else {
					scale = "scale=" + length + ":-1";
				}

				final String[] commandPartArray = { "ffmpeg", "-i", jpgFilePathString,
						"-movflags", "use_metadata_tags", "-map_metadata", "0",
						"-vf", scale, outputFilePathString };
				if (verbose) {

					Logger.printProgress("executing command:");
					Logger.printLine(StringUtils.join(commandPartArray, ' '));
				}

				final ProcessBuilder.Redirect processBuilderRedirect;
				if (verbose) {
					processBuilderRedirect = ProcessBuilder.Redirect.INHERIT;
				} else {
					processBuilderRedirect = ProcessBuilder.Redirect.DISCARD;
				}

				final Process process = new ProcessBuilder()
						.command(commandPartArray)
						.directory(new File(outputFilePathString).getParentFile())
						.redirectOutput(processBuilderRedirect)
						.redirectError(processBuilderRedirect)
						.start();
				final int exitCode = process.waitFor();
				success = exitCode == 0;

				needToImportMetadata = true;
			}

		} catch (final Exception exc) {
			Logger.printError("failed to resize image " +
					System.lineSeparator() + jpgFilePathString +
					System.lineSeparator() + "to:" +
					System.lineSeparator() + outputFilePathString);
			Logger.printException(exc);
		}
		return new ResizeImageOutput(success, needToImportMetadata);
	}

	private record ResizeImageOutput(
			boolean success,
			boolean needToImportMetadata) {
	}

	private static boolean checkNeedToResizeImage(
			final int imageWidth,
			final int imageHeight,
			final int length,
			final String jpgFilePathString) {

		boolean needToResizeImage = false;
		if (imageWidth <= 0) {
			Logger.printError("unknown width for image:" +
					System.lineSeparator() + jpgFilePathString);

		} else {
			if (imageHeight <= 0) {
				Logger.printError("unknown height for image:" +
						System.lineSeparator() + jpgFilePathString);

			} else {
				if (Math.min(imageWidth, imageHeight) > length) {
					needToResizeImage = true;
				}
			}
		}
		return needToResizeImage;
	}
}
