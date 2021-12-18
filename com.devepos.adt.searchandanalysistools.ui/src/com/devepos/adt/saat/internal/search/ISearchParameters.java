package com.devepos.adt.saat.internal.search;

/**
 * Holds values for all relevant search parameters for the DB Object search
 *
 * @author stockbal
 *
 */
public interface ISearchParameters {

  void setMaxRows(int maxRows);

  int getMaxRows();

}
