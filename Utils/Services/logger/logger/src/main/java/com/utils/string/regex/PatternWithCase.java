package com.utils.string.regex;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.utils.string.StrUtils;

public class PatternWithCase implements Serializable {

	@Serial
	private static final long serialVersionUID = -7028678646055852912L;

	private final String patternString;
	private final boolean caseSensitive;

	private transient Pattern pattern;

	PatternWithCase(
			final String patternString,
			final boolean caseSensitive) {

		this.patternString = patternString;
		this.caseSensitive = caseSensitive;
	}

	public boolean checkMatches(
			final String string) {

		final Pattern pattern = createPattern();
		return string != null && pattern.matcher(string).matches();
	}

	public void writeToXml(
			final Element element,
			final String attributeName) {

		element.setAttribute(attributeName, patternString);
		element.setAttribute(attributeName + "CaseSensitive", String.valueOf(caseSensitive));
	}

	public Pattern createPattern() {

		if (pattern == null) {

			pattern = RegexUtils.tryCompile(patternString, caseSensitive);
			if (pattern == null) {
				pattern = Pattern.compile("");
			}
		}
		return pattern;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public String getPatternString() {
		return patternString;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}
}
