package com.devepos.adt.saat.internal.search;

/**
 * Named item resource. It always consists of a concrete name and can have an
 * optional description and some generic descriptive data
 *
 * @author stockbal
 *
 */
public interface INamedItem {
  /**
   * Returns the name of the item
   *
   * @return
   */
  String getName();

  /**
   * Sets the name of the item
   *
   * @param name
   */
  void setName(final String name);

  /**
   * Returns the description of the item
   *
   * @return
   */
  String getDescription();

  /**
   * Sets the description of the item
   *
   * @param description
   */
  void setDescription(final String description);

  /**
   * Retrieves the custom data of the item
   *
   * @return
   */
  String getData();

  /**
   * Sets the custom data of the item
   *
   * @param data
   */
  void setData(final String data);
}
