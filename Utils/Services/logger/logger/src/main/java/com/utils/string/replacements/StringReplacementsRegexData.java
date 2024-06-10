package com.utils.string.replacements;

import java.util.regex.Pattern;

import com.utils.string.StrUtils;

record StringReplacementsRegexData(
		Pattern searchPattern,
		String replacementString) {

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
