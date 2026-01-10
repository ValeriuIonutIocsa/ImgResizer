package com.utils.tmp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.utils.annotations.ApiMethod;
import com.utils.io.PathUtils;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.string.env.EnvProviderSystem;

public final class TmpFolderUtils {

	private TmpFolderUtils() {
	}

	@ApiMethod
	public static String createTmpFolderPathString() {

		String tmpFolderPathString = new EnvProviderSystem().getEnv("TMP");
		if (StringUtils.isBlank(tmpFolderPathString)) {

			tmpFolderPathString = PathUtils.computePath(SystemUtils.USER_HOME, ".java_tmp");
			FactoryFolderCreator.getInstance().createDirectories(tmpFolderPathString, false, true);
		}
		return tmpFolderPathString;
	}
}
