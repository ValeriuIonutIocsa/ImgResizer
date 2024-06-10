package com.personal.img_resizer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.folder_copiers.FactoryFolderCopier;
import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.test.TestInputUtils;

class AppStartImgResizerTest {

	@Test
	void testMain() {

		final String[] args;
		final int input = TestInputUtils.parseTestInputNumber("1");
		if (input == 1) {

			final String originalInputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\all\\_orig";
			final String inputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\all\\input";
			final String outputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\all\\output";

			final boolean deleteFolderSuccess = FactoryFolderDeleter.getInstance()
					.deleteFolder(inputFolderPathString, true, true);
			Assertions.assertTrue(deleteFolderSuccess);

			final boolean copyFolderSuccess = FactoryFolderCopier.getInstance()
					.copyFolder(originalInputFolderPathString, inputFolderPathString, true, true, true);
			Assertions.assertTrue(copyFolderSuccess);

			args = new String[] {
					"1920",
					inputFolderPathString,
					outputFolderPathString,
			};

		} else if (input == 2) {

			final String originalInputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\small\\_orig";
			final String inputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\small\\input";
			final String outputFolderPathString = "D:\\IVI_MISC\\Tmp\\ImageResizer\\small\\output";

			final boolean deleteFolderSuccess = FactoryFolderDeleter.getInstance()
					.deleteFolder(inputFolderPathString, true, true);
			Assertions.assertTrue(deleteFolderSuccess);

			final boolean copyFolderSuccess = FactoryFolderCopier.getInstance()
					.copyFolder(originalInputFolderPathString, inputFolderPathString, true, true, true);
			Assertions.assertTrue(copyFolderSuccess);

			args = new String[] {
					"1920",
					inputFolderPathString,
					outputFolderPathString
			};

		} else {
			throw new RuntimeException();
		}

		AppStartImgResizer.main(args);
	}
}
