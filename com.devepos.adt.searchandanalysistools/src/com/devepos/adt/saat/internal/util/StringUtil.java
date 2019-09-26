package com.devepos.adt.saat.internal.util;

import java.util.regex.Pattern;

/**
 * Utility for common String tasks
 *
 * @author stockbal
 */
public class StringUtil {
	private static final String NEGATION1 = "!"; //$NON-NLS-1$
	private static final String NEGATION2 = "<>"; //$NON-NLS-1$

	/**
	 * Compiles pattern for the given query string
	 *
	 * @param  query the query to compile into Pattern
	 * @return
	 */
	public static Pattern getPatternForQuery(final String query) {
		if (query == null || query.isEmpty()) {
			return Pattern.compile(".*");
		} else {
			final String pattern = query.replace(".", "\\.").replace("*", ".*").replace("+", ".") + ".*";
			return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}
	}

	/**
	 * Returns <code>true</code> if the passed String value starts with a negation
	 * character/string. A negation character is either "!" or "<>"
	 *
	 * @param  value the String to be analyzed
	 * @return       <code>true</code> if <code>value</code> starts with negation
	 *               character
	 */
	public static boolean startsWithNegationCharacter(final String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		return value.startsWith(NEGATION1) || value.startsWith(NEGATION2);
	}

	/**
	 * Removes any existing negation characters from the start of the given string
	 *
	 * @param  value the String to be adjusted
	 * @return       the passed String value without any leading negation characters
	 */
	public static String removeNegationCharacter(final String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		int negationCharLength;
		if (value.startsWith(NEGATION1)) {
			negationCharLength = 1;
		} else if (value.startsWith(NEGATION2)) {
			negationCharLength = 2;
		} else {
			return value;
		}

		if (value.length() > negationCharLength) {
			return value.substring(negationCharLength);
		} else {
			return "";
		}
	}

	/**
	 * Unescapes HTML escaped characters
	 *
	 * @param  escapedHtml the String where characters should be unescaped
	 * @return
	 */
	public static String unescapeHtmlChars(final String escapedHtml) {
		if (escapedHtml == null || escapedHtml.isEmpty()) {
			return "";
		}
		return escapedHtml.replace("&lt;", "<")
			.replace("&gt;", ">")
			.replace("&szlig;", "ß")
			.replace("&amp;", "&")
			.replace("&quot;", "\"")
			.replace("&#39;", "'")
			.replace("&agrave;", "à")
			.replace("&Agrave;", "À")
			.replace("&acirc;", "Â")
			.replace("&auml;", "ä")
			.replace("&Auml;", "Ä")
			.replace("&ouml;", "ö")
			.replace("&Ouml;", "Ö")
			.replace("&uuml;", "ü")
			.replace("&Uuml;", "Ü")
			.replace("&nbsp;", " ");
	}
}
