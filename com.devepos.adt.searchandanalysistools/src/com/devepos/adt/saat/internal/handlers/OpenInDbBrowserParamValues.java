package com.devepos.adt.saat.internal.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

import com.devepos.adt.saat.internal.messages.Messages;

public class OpenInDbBrowserParamValues implements IParameterValues {
	private final Map<String, String> values = new HashMap<>();

	public OpenInDbBrowserParamValues() {
		this.values.put(Messages.CommandParameterValuesSkipSelectionScreen, "true");
		this.values.put(Messages.CommandParameterValuesShowSelectionScreen, "false");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getParameterValues() {
		return this.values;
	}

}
