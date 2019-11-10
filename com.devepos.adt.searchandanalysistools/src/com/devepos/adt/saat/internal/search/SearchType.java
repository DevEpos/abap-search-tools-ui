package com.devepos.adt.saat.internal.search;

import org.eclipse.jface.resource.ImageDescriptor;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.util.IImages;

public enum SearchType {
	// TODO: not yet supported by any backend system
//	ALL("All", "all"),
	CDS_VIEW("CDS View", "cds", IImages.CDS_VIEW),
	DB_TABLE_VIEW("Database Table/View", "dbtabview", IImages.TABLE_DEFINITION);

	private final String name;
	private final String uriTerm;
	private final String imageId;

	private SearchType(final String description, final String uriTerm, final String imageId) {
		this.name = description;
		this.uriTerm = uriTerm;
		this.imageId = imageId;
	}

	/**
	 * Creates key/value pair array from enum name and description
	 *
	 * @return key/value pair array from enum name and description
	 */
	public static String[][] toNamesAndKeys() {
		final SearchType[] types = SearchType.values();
		final String[][] keyValue = new String[types.length][2];
		for (int i = 0; i < types.length; i++) {
			keyValue[i][0] = types[i].toString();
			keyValue[i][1] = types[i].name();
		}
		return keyValue;
	}

	public String getUriTerm() {
		return this.uriTerm;
	}

	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(this.imageId);
	}

	@Override
	public String toString() {
		return this.name;
	}

}
