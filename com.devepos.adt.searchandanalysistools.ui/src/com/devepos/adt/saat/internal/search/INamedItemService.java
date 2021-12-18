package com.devepos.adt.saat.internal.search;

/**
 * Service for retrieving named items from the backend
 *
 * @author stockbal
 *
 */
public interface INamedItemService {

  /**
   * Retrieves a list of named items for the given type.
   *
   * @param type       the type of {@link INamedItem} to retrieve
   * @param maxResults number of results to be retrieved from the service
   * @return an array of the found named items
   */
  INamedItem[] getNamedItems(final NamedItemType type, final int maxResults);

  /**
   * Retrieves a list of named items for the given type. Results can be restricted
   * by supplying a concrete name (wildcards are also possible).
   *
   * @param type       the type of {@link INamedItem} to retrieve
   * @param maxResults number of results to be retrieved from the service
   * @param name       optional filter for a specific named item
   * @return an array of the found named items
   */
  INamedItem[] getNamedItems(final NamedItemType type, final int maxResults, final String name);

  /**
   * Retrieves a list of named items for the given type. Results can be restricted
   * by supplying a concrete name and description (wildcards are also possible).
   *
   * @param type        the type of {@link INamedItem} to retrieve
   * @param maxResults  number of results to be retrieved from the service
   * @param name        optional filter for a specific named item
   * @param description optional filter for a named item with a certain
   *                    description
   * @return an array of the found named items
   */
  INamedItem[] getNamedItems(final NamedItemType type, final int maxResults, final String name,
      final String description);

  /**
   * Retrieves a list of named items for the given type. Results can be restricted
   * by supplying a concrete name, description and custmo data string (wildcards
   * are also possible).
   *
   * @param type        the type of {@link INamedItem} to retrieve
   * @param maxResults  number of results to be retrieved from the service
   * @param name        optional filter for a specific named item
   * @param description optional filter for a named item with a certain
   *                    description
   * @param data        optional filter for a named item with a certain custom
   *                    data string
   * @return
   */
  INamedItem[] getNamedItems(final NamedItemType type, final int maxResults, final String name,
      final String description, final String data);

}