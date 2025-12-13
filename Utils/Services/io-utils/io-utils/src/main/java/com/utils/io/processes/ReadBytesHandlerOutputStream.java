package com.utils.io.processes;

import java.io.IOException;
import java.io.OutputStream;

import com.utils.log.Logger;
import com.utils.string.StrUtils;

public class ReadBytesHandlerOutputStream implements ReadBytesHandler {

	private final OutputStream outputStream;

	public ReadBytesHandlerOutputStream(
			final OutputStream outputStream) {

		this.outputStream = outputStream;
	}

	@Override
	public void handleReadByte(
			final int intByte) throws IOException {
		outputStream.write(intByte);
	}

	public void closeStream() {

		try {
			outputStream.close();

		} catch (final Throwable throwable) {
			Logger.printError("failed to close output stream");
			Logger.printThrowable(throwable);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
