package com.devepos.adt.saat.internal.util;

import java.util.Locale;

import com.sap.adt.communication.util.FileUtils;

/**
 * Utilities for File path operations
 *
 * @author stockbal
 */
public class FilePathUtil {
	/**
	 * Decodes the given name
	 *
	 * @param  name the name to be decoded
	 * @return      the decoded String
	 */
	public static String decodeName(final String name) {
		return FileUtils.decodeFilePathSegment(name);
	}

	/**
	 * Encodes the given name
	 *
	 * @param  name the String to be encoded
	 * @return      the encoded String
	 */
	public static String encodeName(final String name) {
		return FileUtils.encodeFilePathSegment(name);
	}

	/**
	 * Returns the file name without the extension suffix
	 * 
	 * @param  filePath the file path
	 * @return          the file name without the extension
	 */
	public static String getFileNameWithoutExtension(final String filePath) {
		final int index = filePath.lastIndexOf(".");
		if (index < 0) {
			return filePath;
		}
		return filePath.substring(0, index);
	}

	/**
	 * Decodes the name and translates it to all upper case letters
	 *
	 * @param  name the String to be decoded
	 * @return      the decoded uppercase String
	 */
	public static String decodeNameAndMakeUpperCase(final String name) {
		String result = decodeName(name);
		if (result != null) {
			result = result.toUpperCase(Locale.ENGLISH);
		}
		return result;
	}

	/**
	 * Retrieves the file extension for the given file path
	 *
	 * @param  filePath the String file path
	 * @return          the extracted String extension
	 */
	public static String getFileExtension(final String filePath) {
		final int index = filePath.lastIndexOf(".");
		if (index < 0) {
			return null;
		}
		return filePath.substring(index + 1);
	}

}
