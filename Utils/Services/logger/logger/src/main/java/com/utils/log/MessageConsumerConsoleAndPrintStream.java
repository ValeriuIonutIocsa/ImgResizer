package com.utils.log;

import java.io.PrintStream;

public class MessageConsumerConsoleAndPrintStream extends MessageConsumerDefault {

	private final PrintStream outPrintStream;
	private final PrintStream errPrintStream;

	public MessageConsumerConsoleAndPrintStream(
			final PrintStream outPrintStream,
			final PrintStream errPrintStream) {

		this.outPrintStream = outPrintStream;
		this.errPrintStream = errPrintStream;
	}

	@Override
	public void printMessageSpecific(
			final MessageLevel messageLevel,
			final String message) {

		super.printMessageSpecific(messageLevel, message);

		if (messageLevel == MessageLevel.INFO ||
				messageLevel == MessageLevel.PROGRESS ||
				messageLevel == MessageLevel.STATUS) {
			outPrintStream.println(message);

		} else if (messageLevel == MessageLevel.WARNING ||
				messageLevel == MessageLevel.ERROR ||
				messageLevel == MessageLevel.THROWABLE) {
			errPrintStream.println(message);
		}
	}
}
