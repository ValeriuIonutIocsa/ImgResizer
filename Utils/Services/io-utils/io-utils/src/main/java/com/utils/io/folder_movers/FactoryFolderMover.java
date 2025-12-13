package com.utils.io.folder_movers;

import com.utils.annotations.ApiMethod;

public final class FactoryFolderMover {

	private static FolderMover instance = new FolderMoverImpl();

	private FactoryFolderMover() {
	}

	@ApiMethod
	public static FolderMover getInstance() {
		return instance;
	}

	@ApiMethod
	public static void setInstance(
			final FolderMover instance) {
		FactoryFolderMover.instance = instance;
	}
}
