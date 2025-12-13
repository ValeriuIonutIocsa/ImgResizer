package com.utils.io.folder_movers;

import com.utils.annotations.ApiMethod;

public interface FolderMover {

	@ApiMethod
	boolean moveFolder(
			String srcFolderPathString,
			String dstFolderPathString,
			boolean deleteDstDirectoryBeforeMoving,
			boolean verboseProgress,
			boolean verboseError);
}
