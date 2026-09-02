package com.utils.csv;

import com.utils.string.StrUtils;

public class CsvColumn {

	private final String columnName;

	private int columnIndex;

	public CsvColumn(
			final String columnName) {

		this.columnName = columnName;

		columnIndex = -1;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnIndex(
			final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getColumnIndex() {
		return columnIndex;
	}
}
