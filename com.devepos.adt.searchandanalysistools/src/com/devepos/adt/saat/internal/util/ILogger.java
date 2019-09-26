package com.devepos.adt.saat.internal.util;

import org.eclipse.core.runtime.IStatus;

/**
 * Logs messages
 * 
 * @author stockbal
 *
 */
public interface ILogger {

	/**
	 * Create error log entry from throwable
	 * 
	 * @param throwable
	 */
	void error(Throwable throwable);

	/**
	 * Create error log entry from throwable
	 * 
	 * @param throwable
	 * @param messageText
	 * @param args
	 */
	void error(Throwable throwable, String messageText, Object... args);

	/**
	 * Create warning log entry from throwable
	 * 
	 * @param throwable
	 * @param messageText
	 * @param args
	 */
	void warning(Throwable throwable, String messageText, Object... args);

	/**
	 * Create log entry from the given status
	 * 
	 * @param status
	 */
	void log(IStatus status);
}
