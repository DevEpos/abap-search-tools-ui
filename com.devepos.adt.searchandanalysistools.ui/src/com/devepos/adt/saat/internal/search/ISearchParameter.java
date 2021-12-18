package com.devepos.adt.saat.internal.search;

import org.eclipse.swt.graphics.Image;

/**
 * Search Parameter for Db Object Search
 *
 * @author stockbal
 */
public interface ISearchParameter {
  String WILDCARD = "*";

  QueryParameterName getParameterName();

  /**
   * Retrieve the image for this parameter
   *
   * @return reference to Image
   */
  Image getImage();

  /**
   * Returns the Label for this parameter to be shown in proposals
   *
   * @return the String label of the parameter
   */
  String getLabel();

  /**
   * Returns the Description for this parameter to be shown in proposals
   *
   * @return the String description of the parameter
   */
  String getDescription();

  /**
   * Retrieves a flag which signals if the parameter allows values with wildcard
   * pattern
   *
   * @return <code>true</code> if the parameter supports pattern values
   */
  boolean supportsPatternValues();

  /**
   * Retrieves a flag which signals if the parameter is buffered. That means the
   * possible values of this parameter are only retrieved once from the backend
   *
   * @return <code>true</code> if the parameter's values are buffered
   */
  boolean isBuffered();

  /**
   * Retrieves a flag which signals if the parameter allows multiple values or not
   *
   * @return <code>true</code> if the parameter supports multiple values
   */
  boolean supportsMultipleValues();

  /**
   * Returns <code>true</code> if this parameter supports the negation of its
   * values
   *
   * @return <code>true</code> if parameter supports negation of values
   */
  boolean supportsNegatedValues();
}
