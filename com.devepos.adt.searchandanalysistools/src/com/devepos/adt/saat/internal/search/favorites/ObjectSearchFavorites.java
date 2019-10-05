package com.devepos.adt.saat.internal.search.favorites;

import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.IModificationListener;
import com.devepos.adt.saat.IModificationListener.ModificationKind;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorite;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorites;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Implementation of the {@link IObjectSearchFavorites} of the object search
 *
 * @author stockbal
 */
public class ObjectSearchFavorites implements IObjectSearchFavorites {
	private List<IObjectSearchFavorite> entries;
	private final List<IModificationListener> listeners;

	public ObjectSearchFavorites() {
		this.entries = new ArrayList<>();
		this.listeners = new ArrayList<>();
	}

	@Override
	public void addFavorite(final IObjectSearchQuery query, final String description, final boolean isProjectIndependent) {
		addFavorite(new ObjectSearchFavorite(query.getQuery(), description, query.getSearchType(), query.getDestinationId(),
			query.isAndSearchActive()));
	}

	@Override
	public void addFavorite(final IObjectSearchFavorite entry) {
		this.entries.add(entry);
		notifyModificationListeners(ModificationKind.ADDED);
	}

	@Override
	public void removeFavorite(final IObjectSearchFavorite favorite) {
		this.entries.remove(favorite);
		notifyModificationListeners(ModificationKind.REMOVED);
		if (this.entries.isEmpty()) {
			notifyModificationListeners(ModificationKind.CLEARED);
		}
	}

	@Override
	public List<IObjectSearchFavorite> getFavorites() {
		return this.entries;
	}

	@Override
	public boolean contains(final String destinationId, final SearchType searchType, final String description) {
		return this.entries.stream()
			.anyMatch(f -> description.equals(f.getDescription())
				&& (f.getDestinationId() == null && destinationId == null || destinationId.equals(f.getDestinationId()))
				&& searchType.equals(f.getSearchType()));
	}

	@Override
//	@JsonDeserialize(contentAs = ObjectSearchFavorite.class)
	public void setFavorites(final List<IObjectSearchFavorite> favorites) {
		this.entries = favorites;
	}

	@Override
	public boolean hasEntries() {
		return !this.entries.isEmpty();
	}

	@Override
	public void addModificationListener(final IModificationListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeModificationListener(final IModificationListener listener) {
		this.listeners.remove(listener);
	}

	private void notifyModificationListeners(final ModificationKind kind) {
		if (this.listeners != null) {
			for (final IModificationListener listener : this.listeners) {
				listener.modified(kind);
			}
		}
	}

}
