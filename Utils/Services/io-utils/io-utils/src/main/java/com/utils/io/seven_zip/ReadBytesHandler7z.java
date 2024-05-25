package com.utils.io.seven_zip;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.processes.AbstractReadBytesHandlerLines;
import com.utils.log.Logger;
import com.utils.log.progress.ProgressIndicators;
import com.utils.string.StrUtils;

public class ReadBytesHandler7z extends AbstractReadBytesHandlerLines {

	private int previousPercentage;

	public ReadBytesHandler7z() {
	}

	@Override
	protected void handleLine(
			final String line) {

		final String trimmedLine = line.trim();
		final int indexOf = trimmedLine.indexOf("%");
		if (indexOf > 0) {

			final String percentageString = trimmedLine.substring(0, indexOf);
			final int percentage = StrUtils.tryParsePositiveInt(percentageString);
			if (percentage > previousPercentage) {

				ProgressIndicators.getInstance().update(percentage, 100);
				previousPercentage = percentage;
			}

		} else {
			if (StringUtils.isNotBlank(trimmedLine)) {
				Logger.printLine(trimmedLine);
			}
		}
	}
}
