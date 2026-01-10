package com.utils.net;

import java.nio.charset.Charset;

import org.apache.commons.lang3.SystemUtils;

import com.utils.annotations.ApiMethod;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandlerByteArray;

public final class HostNameUtils {

	private HostNameUtils() {
	}

	@ApiMethod
	public static String findHostName() {

		String hostName = "";
		try {
			final String[] commandPartArray;
			if (SystemUtils.IS_OS_WINDOWS) {
				commandPartArray = new String[] { "cmd", "/c", "hostname" };
			} else {
				commandPartArray = new String[] { "hostname" };
			}
			final Process process = new ProcessBuilder(commandPartArray).start();

			final ReadBytesHandlerByteArray readBytesHandlerByteArray = new ReadBytesHandlerByteArray();
			final InputStreamReaderThread inputStreamReaderThread = new InputStreamReaderThread(
					"host name finder", process.getInputStream(),
					Charset.defaultCharset(), readBytesHandlerByteArray);
			inputStreamReaderThread.start();

			process.waitFor();
			inputStreamReaderThread.join();

			hostName = readBytesHandlerByteArray.getString(Charset.defaultCharset());

		} catch (final Throwable ignored) {
		}
		return hostName;
	}
}
