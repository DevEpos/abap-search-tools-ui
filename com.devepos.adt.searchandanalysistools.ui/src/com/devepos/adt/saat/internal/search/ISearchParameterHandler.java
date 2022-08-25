package com.devepos.adt.saat.internal.search;

import java.util.List;

import com.devepos.adt.base.project.IAbapProjectProvider;

/**
 * Handles search parameters for e.g. DB Object search
 *
 * @author stockbal
 */
public interface ISearchParameterHandler {

  /**
   * Retrieves a list of all valid search parameters
   *
   * @return List of search parameters
   */
  List<ISearchParameter> getParameters();

  /**
   * Sets the search type for the current query
   *
   * @param searchType
   */
  void setSearchType(SearchType searchType);

  /**
   * Updates the project provider
   *
   * @param project the new project provider
   */
  void updateProjectProvider(IAbapProjectProvider projectProvider);
}
