package com.devepos.adt.saat.internal.search.history;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.devepos.adt.saat.IModificationListener;
import com.devepos.adt.saat.IModificationListener.ModificationKind;
import com.devepos.adt.saat.search.history.IObjectSearchHistory;
import com.devepos.adt.saat.search.history.IObjectSearchHistoryEntry;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Implementation of {@link IObjectSearchHistory}
 *
 * @author stockbal
 */
public class ObjectSearchHistory implements IObjectSearchHistory {
	private final LinkedList<IObjectSearchHistoryEntry> entries;
	private int maxSize;
	private List<IModificationListener> listeners;
	private IObjectSearchHistoryEntry activeEntry;

	public ObjectSearchHistory() {
		this.maxSize = 10;
		this.entries = new LinkedList<>();
	}

	@Override
	public List<IObjectSearchHistoryEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void addEntry(final IObjectSearchQuery query, final int resultCount) {
		this.addEntry(new ObjectSearchHistoryEntry(query, resultCount));
	}

	@Override
	public void addEntry(final IObjectSearchHistoryEntry entry) {
		if (!differsFromLastAddedEntry(entry)) {
			return;
		}
		// check if maximum size of history will be exceeded by the new entry
		if (this.entries.size() == this.maxSize) {
			this.entries.removeLast();
			notifyModificationListeners(ModificationKind.REMOVED);
		}
		this.entries.addFirst(entry);
		this.activeEntry = entry;
		notifyModificationListeners(ModificationKind.ADDED);
	}

	@Override
	public void removeHistoryEntry(final IObjectSearchHistoryEntry historyEntry) {
		if (this.entries != null) {
			this.entries.remove(historyEntry);
		}
	}

	@Override
	public boolean hasEntries() {
		return !this.entries.isEmpty();
	}

	@Override
	public boolean hasActiveEntry() {
		return this.activeEntry != null;
	}

	@Override
	public IObjectSearchHistoryEntry getActiveEntry() {
		return this.activeEntry;
	}

	@Override
	public void clear() {
		this.entries.clear();
		this.activeEntry = null;
		notifyModificationListeners(ModificationKind.CLEARED);
	}

	@Override
	public void setMaxHistorySize(final int maxSize) {
		this.maxSize = maxSize;
		while (this.entries.size() > maxSize) {
			this.entries.removeLast();
		}
	}

	@Override
	public void addModificationListener(final IModificationListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<>();
		}
		this.listeners.add(listener);
	}

	@Override
	public void removeModificationListener(final IModificationListener listener) {
		if (this.listeners == null) {
			return;
		}
		this.listeners.remove(listener);
	}

	@Override
	public boolean isActiveEntry(final IObjectSearchHistoryEntry entry) {
		return this.activeEntry == entry;
	}

	@Override
	public void setActiveEntry(final IObjectSearchHistoryEntry entry) {
		if (entry == null && hasEntries()) {
			this.activeEntry = this.entries.getFirst();
		} else {
			this.activeEntry = entry;
		}
	}

	private boolean differsFromLastAddedEntry(final IObjectSearchHistoryEntry entry) {
		if (this.entries.size() == 0) {
			return true;
		} else {
			final IObjectSearchHistoryEntry lastEntry = this.entries.getFirst();
			return !lastEntry.equals(entry);
		}
	}

	private void notifyModificationListeners(final ModificationKind kind) {
		if (this.listeners != null) {
			for (final IModificationListener listener : this.listeners) {
				listener.modified(kind);
			}
		}
	}

}
