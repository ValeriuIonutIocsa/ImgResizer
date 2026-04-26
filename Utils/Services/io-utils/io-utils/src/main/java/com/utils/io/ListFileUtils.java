package com.utils.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;

public final class ListFileUtils {

	private ListFileUtils() {
	}

	@ApiMethod
	public static void visitFiles(
			final String rootDirPathString,
			final Function<Path, FileVisitResult> visitDirectoryFunction,
			final Function<Path, FileVisitResult> visitFileFunction) {

		try {
			final Path rootDirPath = Paths.get(rootDirPathString);
			Files.walkFileTree(rootDirPath, new SimpleFileVisitor<>() {

				@Override
				public FileVisitResult preVisitDirectory(
						final Path dir,
						final BasicFileAttributes attrs) {

					final FileVisitResult fileVisitResult;
					if (rootDirPath.equals(dir)) {
						fileVisitResult = FileVisitResult.CONTINUE;

					} else {
						final FileVisitResult tmpFileVisitResult = visitDirectoryFunction.apply(dir);
						if (tmpFileVisitResult == FileVisitResult.CONTINUE) {
							fileVisitResult = FileVisitResult.SKIP_SUBTREE;
						} else {
							fileVisitResult = tmpFileVisitResult;
						}
					}
					return fileVisitResult;
				}

				@Override
				public FileVisitResult visitFile(
						final Path file,
						final BasicFileAttributes attrs) {

					return visitFileFunction.apply(file);
				}

				@Override
				public FileVisitResult postVisitDirectory(
						final Path dir,
						final IOException exc) {

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(
						final Path file,
						final IOException exc) {

					Logger.printWarning("cannot visit \"" + file + "\" due to " +
							exc.getClass().getSimpleName());
					return FileVisitResult.CONTINUE;
				}
			});

		} catch (final Throwable throwable) {
			Logger.printError("failed to visit files recursively of directory:" +
					System.lineSeparator() + rootDirPathString);
			Logger.printThrowable(throwable);
		}
	}

	@ApiMethod
	public static void visitFilesRecursively(
			final String rootDirPathString,
			final Function<Path, FileVisitResult> visitDirectoryFunction,
			final Function<Path, FileVisitResult> visitFileFunction) {

		try {
			final Path rootDirPath = Paths.get(rootDirPathString);
			Files.walkFileTree(rootDirPath, new SimpleFileVisitor<>() {

				@Override
				public FileVisitResult preVisitDirectory(
						final Path dir,
						final BasicFileAttributes attrs) {

					final FileVisitResult fileVisitResult;
					if (rootDirPath.equals(dir)) {
						fileVisitResult = FileVisitResult.CONTINUE;
					} else {
						fileVisitResult = visitDirectoryFunction.apply(dir);
					}
					return fileVisitResult;
				}

				@Override
				public FileVisitResult visitFile(
						final Path file,
						final BasicFileAttributes attrs) {

					return visitFileFunction.apply(file);
				}

				@Override
				public FileVisitResult postVisitDirectory(
						final Path dir,
						final IOException exc) {

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(
						final Path file,
						final IOException exc) {

					Logger.printWarning("cannot visit \"" + file + "\" due to " +
							exc.getClass().getSimpleName());
					return FileVisitResult.CONTINUE;
				}
			});

		} catch (final Throwable throwable) {
			Logger.printError("failed to visit files recursively of directory:" +
					System.lineSeparator() + rootDirPathString);
			Logger.printThrowable(throwable);
		}
	}
}
