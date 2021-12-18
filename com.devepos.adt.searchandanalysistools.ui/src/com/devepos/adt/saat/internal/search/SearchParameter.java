package com.devepos.adt.saat.internal.search;

import org.eclipse.swt.graphics.Image;

/**
 * Describes a simple search parameter for the Object search which does not have
 * a proposal provider
 *
 * @author stockbal
 */
public class SearchParameter implements ISearchParameter {

  private final QueryParameterName parameterName;
  private final String description;
  private final Image image;
  private final boolean supportsPatternValues;
  private final boolean supportsMultipleValues;
  private final boolean supportsNegatedValues;

  /**
   * Creates a simple search parameter that supports multiple values and patterns.
   * It does not allow buffering and negated values
   *
   * @param parameterName the name of the parameter
   * @param description   a long description for the parameter
   * @param image         the image to be displayed for the parameter
   */
  public SearchParameter(final QueryParameterName parameterName, final String description,
      final Image image) {
    this(parameterName, description, image, true, true, false);
  }

  /**
   * Creates a simple search parameter with the given options. Negated values are
   * not allowed.
   *
   * @param parameterName          the name of the parameter
   * @param description            a long description for the parameter
   * @param image                  the image to be displayed for the parameter
   * @param supportsPatternValues  if <code>true</code> the parameter supports
   *                               pattern values
   * @param supportsMultipleValues if <code>true</code> the parmeter supports
   *                               multiple values
   */
  public SearchParameter(final QueryParameterName parameterName, final String description,
      final Image image, final boolean supportsPatternValues,
      final boolean supportsMultipleValues) {
    this(parameterName, description, image, supportsMultipleValues, supportsMultipleValues, false);
  }

  /**
   * Creates a simple search parameter with the given options
   *
   * @param parameterName          the name of the parameter
   * @param description            a long description for the parameter
   * @param image                  the image to be displayed for the parameter
   * @param supportsPatternValues  if <code>true</code> the parameter supports
   *                               pattern values
   * @param supportsMultipleValues if <code>true</code> the parmeter supports
   *                               multiple values
   * @param supportsNegatedValues  if <code>true</code> the parmeter supports the
   *                               negation of values
   */
  public SearchParameter(final QueryParameterName parameterName, final String description,
      final Image image, final boolean supportsPatternValues, final boolean supportsMultipleValues,
      final boolean supportsNegatedValues) {
    this.parameterName = parameterName;
    this.description = description;
    this.image = image;
    this.supportsPatternValues = supportsPatternValues;
    this.supportsMultipleValues = supportsMultipleValues;
    this.supportsNegatedValues = supportsNegatedValues;
  }

  @Override
  public QueryParameterName getParameterName() {
    return parameterName;
  }

  @Override
  public Image getImage() {
    return image;
  }

  @Override
  public String getLabel() {
    return getParameterName().getLowerCaseKey();
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean supportsPatternValues() {
    return supportsPatternValues;
  }

  @Override
  public boolean isBuffered() {
    return false;
  }

  @Override
  public boolean supportsMultipleValues() {
    return supportsMultipleValues;
  }

  @Override
  public boolean supportsNegatedValues() {
    return supportsNegatedValues;
  }

}
