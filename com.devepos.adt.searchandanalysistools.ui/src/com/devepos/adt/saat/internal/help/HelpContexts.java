package com.devepos.adt.saat.internal.help;

/**
 * Holds Help Contexts
 * 
 * @author stockbal
 */
public enum HelpContexts {
	OBJECT_SEARCH("object_search"),
	CDS_ANALYZER("cds_analyzer");

	private String helpContextId;

	private HelpContexts(final String contextId) {
		this.helpContextId = contextId;
	}

	public String getHelpContextId() {
		return this.helpContextId;
	}
}
