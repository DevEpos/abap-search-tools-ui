package com.devepos.adt.saat.search.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import com.devepos.adt.saat.internal.ui.IStatusUpdater;

public class QueryManager {
	private final List<IObjectSearchQueryListener> listeners;
	private final List<IStatusUpdater> statusUpdaters;

	public QueryManager() {
		this.listeners = new ArrayList<>();
		this.statusUpdaters = new ArrayList<>();
	}

	public void addStatusUpdater(final IStatusUpdater su) {
		synchronized (this.statusUpdaters) {
			this.statusUpdaters.add(su);
		}
	}

	public void removeStatusUpdate(final IStatusUpdater su) {
		synchronized (this.statusUpdaters) {
			this.statusUpdaters.remove(su);
		}
	}

	public void addQueryListener(final IObjectSearchQueryListener l) {
		synchronized (this.listeners) {
			this.listeners.add(l);
		}
	}

	public void removeQueryListener(final IObjectSearchQueryListener l) {
		synchronized (this.listeners) {
			this.listeners.remove(l);
		}
	}

	public void fireFinished(final IObjectSearchQueryResult queryResult) {
		final Set<IObjectSearchQueryListener> copiedListeners = new HashSet<>();
		synchronized (this.listeners) {
			copiedListeners.addAll(this.listeners);
		}
		final Iterator<IObjectSearchQueryListener> listeners = copiedListeners.iterator();
		while (listeners.hasNext()) {
			final IObjectSearchQueryListener l = listeners.next();
			l.queryFinished(queryResult);
		}
	}

	public void queryFinished(final IObjectSearchQueryResult queryResult) {
		fireFinished(queryResult);
	}

	private void fireStatusUpdate(final int statusCode, final String text) {
		final Set<IStatusUpdater> copiedStatusUpdaters = new HashSet<>();
		synchronized (this.statusUpdaters) {
			copiedStatusUpdaters.addAll(this.statusUpdaters);
		}
		for (final IStatusUpdater statusUpdate : copiedStatusUpdaters) {
			statusUpdate.updateStatus(statusCode, text);
		}

	}

	public void statusUpdated(final int statusCode, final String text) {
		statusUpdated(statusCode, text, false);
	}

	public void statusUpdated(final int statusCode, final String text, final boolean doOnUi) {
		if (doOnUi) {
			Display.getDefault().asyncExec(() -> {
				fireStatusUpdate(statusCode, text);
			});
		} else {
			fireStatusUpdate(statusCode, text);
		}
	}

}