package com.devepos.adt.saat.internal.search;

/**
 * Possible types for {@link INamedItemService}
 *
 * @author stockbal
 */
public enum NamedItemType {
	ANNOTATION("annotation"),
	ANNOTATION_VALUE("annotationvalue"),
	DB_ENTITY("dbentity"),
	CDS_FIELD("cdsfield"),
	TABLE_FIELD("tablefield"),
	TABLE_TYPE("tabletype"),
	API_STATE("releasestate"),
	CDS_TYPE("cdstype"),
	CDS_EXTENSION("cdsextension");

	private String discoveryTerm;

	private NamedItemType(final String discoveryTerm) {
		this.discoveryTerm = discoveryTerm;
	}

	/**
	 * Returns the discovery term of this named item type
	 * 
	 * @return the discovery term of this named item type
	 */
	public String getDiscoveryTerm() {
		return this.discoveryTerm;
	}
}