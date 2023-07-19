package com.personal.img_resizer;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

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
			final List<String> filePathStringList =
					ListFileUtils.listFilesRecursively(inputPathString, Files::isRegularFile);
			for (final String filePathString : filePathStringList) {

				final ImageType imageType = FactoryImageType.computeImageType(filePathString);
				if (imageType != null) {

					foundImages = true;

					final String relativePath = PathUtils.computeRelativePath(inputPathString, filePathString);
					String outputFilePathString = PathUtils.computePath(outputPathString, relativePath);
					outputFilePathString = PathUtils.computePathWoExt(outputFilePathString) + ".jpg";

					final boolean resizedImageSuccess =
							resizeImage(filePathString, outputFilePathString, verbose, imageType, length);
					if (!resizedImageSuccess) {
						success = false;
					}
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

		Logger.printProgress("copying image file:");
		Logger.printLine(filePathString);
		Logger.printLine("to:");
		Logger.printLine(outputFilePathString);

		boolean success = FactoryFileDeleter.getInstance()
				.deleteFile(outputFilePathString, false, true);
		if (success) {

			success = FactoryFolderCreator.getInstance()
					.createParentDirectories(outputFilePathString, false, true);
			if (success) {

				final String jpgFilePathString;
				if (imageType != ImageType.JPG) {

					jpgFilePathString = PathUtils.computePathWoExt(filePathString) + ".jpg";
					success = false;
					try {
						ProcessStarter.setGlobalSearchPath("D:\\IVI_MISC\\Apps\\ImageMagick");
						final ConvertCmd convertCmd = new ConvertCmd();

						final IMOperation imOperation = new IMOperation();

						imOperation.addImage(filePathString);

						imOperation.quality(100.0);

						imOperation.addImage(jpgFilePathString);

						convertCmd.run(imOperation);
						success = true;

					} catch (final Exception exc) {
						exc.printStackTrace();
					}

				} else {
					jpgFilePathString = filePathString;
				}
				if (success) {

					try {
						final MetadataExporter metadataExporter =
								new MetadataExporter(jpgFilePathString, imageType);
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

							final ProcessBuilder.Redirect processBuilderRedirect;
							if (verbose) {
								processBuilderRedirect = ProcessBuilder.Redirect.INHERIT;
							} else {
								processBuilderRedirect = ProcessBuilder.Redirect.DISCARD;
							}

							final Process process = new ProcessBuilder()
									.command("ffmpeg", "-i", jpgFilePathString,
											"-movflags", "use_metadata_tags", "-map_metadata", "0",
											"-vf", scale, outputFilePathString)
									.directory(new File(outputFilePathString).getParentFile())
									.redirectOutput(processBuilderRedirect)
									.redirectError(processBuilderRedirect)
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

					} catch (final Exception exc) {
						Logger.printError("failed to resize image " +
								System.lineSeparator() + jpgFilePathString +
								System.lineSeparator() + "to:" +
								System.lineSeparator() + outputFilePathString);
						Logger.printException(exc);
					}
				}
			}
		}
		return success;
	}
}
