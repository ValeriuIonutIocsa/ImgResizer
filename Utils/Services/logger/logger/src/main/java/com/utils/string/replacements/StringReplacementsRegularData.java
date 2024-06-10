package com.utils.string.replacements;

import com.utils.string.StrUtils;

record StringReplacementsRegularData(
		String searchString,
		String replacementString) {

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
