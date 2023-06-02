package com.personal.img_resizer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.folder_copiers.FactoryFolderCopier;
import com.utils.io.folder_deleters.FactoryFolderDeleter;

class AppStartImgResizerTest {

	@Test
	void testMain() {

		final String[] args;
		final int input = Integer.parseInt("1");
		if (input == 1) {

			final String originalInputFolderPathString = "D:\\tmp\\ImageResizer\\orig_input";
			final String inputFolderPathString = "D:\\tmp\\ImageResizer\\input";
			final String outputFolderPathString = "D:\\tmp\\ImageResizer\\output";

			final boolean deleteFolderSuccess = FactoryFolderDeleter.getInstance()
					.deleteFolder(inputFolderPathString, true, true);
			Assertions.assertTrue(deleteFolderSuccess);

			final boolean copyFolderSuccess = FactoryFolderCopier.getInstance()
					.copyFolder(originalInputFolderPathString, inputFolderPathString, true, true);
			Assertions.assertTrue(copyFolderSuccess);

			args = new String[] { "1920", inputFolderPathString, outputFolderPathString };

		} else {
			throw new RuntimeException();
		}

		AppStartImgResizer.main(args);
	}
}
