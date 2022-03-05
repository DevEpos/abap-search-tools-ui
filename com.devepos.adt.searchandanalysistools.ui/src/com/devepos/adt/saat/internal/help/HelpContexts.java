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

  HelpContexts(final String contextId) {
    helpContextId = contextId;
  }

  public String getHelpContextId() {
    return helpContextId;
  }
}
