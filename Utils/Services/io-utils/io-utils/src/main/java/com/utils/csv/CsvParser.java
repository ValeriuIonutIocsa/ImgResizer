package com.utils.csv;

import java.io.BufferedReader;

import org.apache.commons.lang3.StringUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public class CsvParser {

	private final String displayName;
	private final CsvColumn[] csvColumnArray;

	private String separator;

	public CsvParser(
			final String displayName,
			final CsvColumn[] csvColumnArray,
			final String defaultSeparator) {

		this.displayName = displayName;
		this.csvColumnArray = csvColumnArray;

		separator = defaultSeparator;
	}

	@ApiMethod
	public boolean parseFirstLines(
			final BufferedReader bufferedReader) throws Exception {

		boolean success = false;
		String firstLine = bufferedReader.readLine();
		final String separatorLinePrefix = "sep=";
		if (StringUtils.isNotBlank(firstLine)) {

			final int indexOf = firstLine.indexOf(separatorLinePrefix);
			if (indexOf >= 0) {

				separator = firstLine.substring(indexOf + separatorLinePrefix.length());
				firstLine = bufferedReader.readLine();
			}
			if (StringUtils.isNotBlank(firstLine)) {

				if (firstLine.charAt(0) == '\uFEFF') {
					firstLine = firstLine.substring(1);
				}
				parseCsvColumnIndices(firstLine);
				success = true;
			}
		}
		return success;
	}

	private void parseCsvColumnIndices(
			final String firstLine) {

		final String[] firstLinePartArray = splitCsvLine(firstLine);
		for (final CsvColumn csvColumn : csvColumnArray) {

			final String columnName = csvColumn.getColumnName();

			int columnIndex = -1;
			for (int j = 0; j < firstLinePartArray.length; j++) {

				final String firstLinePart = firstLinePartArray[j];
				if (columnName.equals(firstLinePart)) {

					columnIndex = j;
					break;
				}
			}
			if (columnIndex < 0) {
				Logger.printError("failed to find column \"" + columnName + "\" " +
						"in the \"" + displayName + "\" CSV file");
			} else {
				csvColumn.setColumnIndex(columnIndex);
			}
		}
	}

	@ApiMethod
	public int computeMaxCsvColumnIndex() {

		int maxCsvColumnIndex = -1;
		for (final CsvColumn csvColumn : csvColumnArray) {

			final int columnIndex = csvColumn.getColumnIndex();
			if (columnIndex > maxCsvColumnIndex) {
				maxCsvColumnIndex = columnIndex;
			}
		}
		return maxCsvColumnIndex;
	}

	@ApiMethod
	public String[] splitCsvLine(
			final String csvLine) {

		return StringUtils.splitPreserveAllTokens(csvLine, separator);
	}

	@ApiMethod
	public String parseCsvColumnValue(
			final String[] linePartArray,
			final int csvColumnIndex) {

		String csvColumnValue = null;
		if (0 <= csvColumnIndex && csvColumnIndex < csvColumnArray.length) {

			final CsvColumn csvColumn = csvColumnArray[csvColumnIndex];
			final int columnIndex = csvColumn.getColumnIndex();
			if (0 <= columnIndex && columnIndex < linePartArray.length) {

				csvColumnValue = linePartArray[columnIndex];
			}
		}
		return csvColumnValue;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
