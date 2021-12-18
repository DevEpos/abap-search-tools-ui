package com.devepos.adt.saat.internal.elementinfo;

public class ElementInfoRetrievalServiceFactory {

  /**
   * Creates new Element Information Retrieval service
   *
   * @return instance of an element information retrieval service
   */
  public static IElementInfoRetrievalService createService() {
    return new ElementInfoRetrievalService();
  }
}
