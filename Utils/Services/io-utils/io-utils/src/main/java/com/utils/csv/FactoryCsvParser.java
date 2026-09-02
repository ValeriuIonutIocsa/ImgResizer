package com.utils.csv;

public final class FactoryCsvParser {

	private FactoryCsvParser() {
	}

	public static CsvParser newInstance(
			final String displayName,
			final String[] columnNameArray,
			final String defaultSeparator) {

		final CsvColumn[] csvColumnArray = createCsvColumnArray(columnNameArray);
		return new CsvParser(displayName, csvColumnArray, defaultSeparator);
	}

	private static CsvColumn[] createCsvColumnArray(
			final String[] columnNameArray) {

		final CsvColumn[] csvColumnArray = new CsvColumn[columnNameArray.length];
		for (int i = 0; i < columnNameArray.length; i++) {

			final String columnName = columnNameArray[i];
			csvColumnArray[i] = new CsvColumn(columnName);
		}
		return csvColumnArray;
	}
}
