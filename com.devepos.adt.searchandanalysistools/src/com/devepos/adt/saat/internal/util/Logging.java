package com.devepos.adt.saat.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;

public class Logging implements ILogger {
	static final Level DEFAULT_LOGGING_LEVEL = Level.INFO;
	private static final Map<Integer, Level> severityToLevel = new HashMap<>(6);
	private final Logger delegate;

	static {
		severityToLevel.put(Integer.valueOf(0), Level.INFO);
		severityToLevel.put(Integer.valueOf(4), Level.SEVERE);
		severityToLevel.put(Integer.valueOf(2), Level.WARNING);
		severityToLevel.put(Integer.valueOf(1), Level.INFO);
		severityToLevel.put(Integer.valueOf(8), Level.INFO);
	}

	private Logging(final String location) {
		this.delegate = Logger.getLogger(location);
	}

	public static ILogger getLogger(final Class<?> location) {
		final String locationString = location.getName();
		return new Logging(locationString);
	}

	@Override
	public void error(final Throwable throwable) {
		error(throwable, "");
	}

	@Override
	public void error(final Throwable throwable, final String messageText, final Object... args) {
		this.delegate.log(toRecord(Level.SEVERE, throwable, messageText, args));
	}

	@Override
	public void warning(final Throwable throwable, final String messageText, final Object... args) {
		this.delegate.log(toRecord(Level.WARNING, throwable, messageText, args));
	}

	@Override
	public void log(final IStatus status) {
		final Level level = severityToLevel.get(Integer.valueOf(status.getSeverity()));
		this.delegate.log(toRecord(level, status.getException(), status.getMessage()));
	}

	/**
	 * Create Log record with the given parameters
	 *
	 * @param level
	 *          the level of the log entry
	 * @param throwable
	 *          the exception like object
	 * @param message
	 *          message string for the log entry
	 * @param args
	 *          additional arguments for the message string
	 * @return created Log Entry
	 */
	private LogRecord toRecord(final Level level, final Throwable throwable, final String message, final Object... args) {
		final LogRecord record = new LogRecord(level, message);
		record.setParameters(args);
		record.setThrown(throwable);
		return record;
	}

}
