package com.devepos.adt.saat.search.history;

import java.util.List;

import com.devepos.adt.saat.IModificationProvider;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Log which contains previously executed queries
 *
 * @author stockbal
 */
public interface IObjectSearchHistory extends IModificationProvider {

	/**
	 * Checks if <code>entry</code> is the active history entry
	 *
	 * @param  entry a history entry
	 * @return       <code>true</code> if the passed entry is the active history
	 *               entry
	 */
	boolean isActiveEntry(IObjectSearchHistoryEntry entry);

	/**
	 * Sets the given history entry as the currently active entry
	 *
	 * @param entry the history entry to be set as the active one
	 */
	void setActiveEntry(IObjectSearchHistoryEntry entry);

	/**
	 * Adds the given entry to the search history
	 *
	 * @param entry
	 */
	void addEntry(IObjectSearchHistoryEntry entry);

	/**
	 * Adds a new history entry for the given parameters
	 *
	 * @param query       the executed {@link IObjectSearchQuery}
	 * @param resultCount the number of results that were found by the query
	 * @see               IObjectSearchQuery
	 */
	void addEntry(IObjectSearchQuery query, int resultCount);

	/**
	 * Removes the given history entry from the history
	 *
	 * @param historyEntry the history entry to be removed
	 */
	void removeHistoryEntry(IObjectSearchHistoryEntry historyEntry);

	/**
	 * Returns the history entries of the object search
	 *
	 * @return
	 */
	List<IObjectSearchHistoryEntry> getEntries();

	/**
	 * Returns <code>true</code> if there is at least one history entry, otherwise
	 * <code>false</code>
	 *
	 * @return <code>true</code> if there are history entries
	 */
	boolean hasEntries();

	/**
	 * Clears the history
	 */
	void clear();

	/**
	 * Returns <code>true</code> if the history has an active entry
	 *
	 * @return <code>true</code> if the history has an active entry
	 */
	boolean hasActiveEntry();

	/**
	 * Returns the currently active history entry
	 *
	 * @return the active history entry
	 */
	IObjectSearchHistoryEntry getActiveEntry();

	/**
	 * Sets the maximum size of the object search history
	 *
	 * @param maxSize the maximum number of entries
	 */
	void setMaxHistorySize(int maxSize);
}
