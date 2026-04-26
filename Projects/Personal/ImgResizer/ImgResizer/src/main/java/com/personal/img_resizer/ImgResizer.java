package com.personal.img_resizer;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

import com.utils.io.PathUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandler;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.io.processes.ReadBytesHandlerLinesPrint;
import com.utils.log.Logger;

final class ImgResizer {

	private ImgResizer() {
	}

	static boolean work(
			final String filePathString,
			final String outputFilePathString,
			final boolean verbose,
			final ImageType imageType,
			final int length) {

		boolean success = false;
		final List<String> tmpFilePathStringList = new ArrayList<>();
		try {
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

					final String tmpJpgFilePathString;
					if (imageType != ImageType.JPG) {

						tmpJpgFilePathString = PathUtils.computePathWoExt(filePathString) + "_tmp.jpg";
						tmpFilePathStringList.add(tmpJpgFilePathString);
						success = convertImageToJpg(filePathString, tmpJpgFilePathString);

					} else {
						tmpJpgFilePathString = filePathString;
					}
					if (success) {

						final MetadataExporter metadataExporter =
								new MetadataExporter(tmpJpgFilePathString, ImageType.JPG, verbose);
						metadataExporter.work();

						final String metadataXmlPathString =
								metadataExporter.getMetadataXmlPathString();
						tmpFilePathStringList.add(metadataXmlPathString);

						success = metadataExporter.isSuccess();
						if (success) {

							final ResizeImageOutput resizeImageL2Return = workL2(
									tmpJpgFilePathString, outputFilePathString, metadataExporter, length, verbose);
							success = resizeImageL2Return.success();
							if (success) {

								final boolean needToImportMetadata = resizeImageL2Return.needToImportMetadata();
								if (needToImportMetadata) {

									final MetadataImporter metadataImporter =
											new MetadataImporter(outputFilePathString, metadataXmlPathString, verbose);
									metadataImporter.work();

									success = metadataImporter.isSuccess();
								}
							}
						}
					}
				}
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);

		} finally {
			if (!success) {
				Logger.printError("failed to copy image file:" +
						System.lineSeparator() + filePathString);
			}
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

			ProcessStarter.setGlobalSearchPath("D:\\IVI_PERS\\Apps\\ImageMagick");
			final ConvertCmd convertCmd = new ConvertCmd();

			final IMOperation imOperation = new IMOperation();

			imOperation.addImage(filePathString);

			imOperation.quality(100.0);

			imOperation.addImage(jpgFilePathString);

			convertCmd.run(imOperation);
			success = true;

		} catch (final Throwable throwable) {
			Logger.printError("failed to convert image to JPG");
			Logger.printThrowable(throwable);
		}
		return success;
	}

	private static ResizeImageOutput workL2(
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

				final Process process = new ProcessBuilder()
						.command(commandPartArray)
						.directory(new File(outputFilePathString).getParentFile())
						.redirectErrorStream(true)
						.start();

				final ReadBytesHandler readBytesHandler;
				if (verbose) {
					readBytesHandler = new ReadBytesHandlerLinesPrint();
				} else {
					readBytesHandler = new ReadBytesHandlerLinesCollect();
				}
				final InputStream inputStream = process.getInputStream();
				final InputStreamReaderThread inputStreamReaderThread =
						new InputStreamReaderThread("resize image", inputStream,
								Charset.defaultCharset(), readBytesHandler);
				inputStreamReaderThread.start();

				final int exitCode = process.waitFor();
				success = exitCode == 0;

				inputStreamReaderThread.join();

				needToImportMetadata = true;
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to resize image " +
					System.lineSeparator() + jpgFilePathString +
					System.lineSeparator() + "to:" +
					System.lineSeparator() + outputFilePathString);
			Logger.printThrowable(throwable);
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
