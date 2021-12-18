package com.devepos.adt.saat.internal.search;

/**
 * Simple Implementation of the named item interface {@link INamedItem}
 *
 * @author stockbal
 *
 */
public class NamedItem implements INamedItem {
  private String name;
  private String description;
  private String data;

  public NamedItem() {
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public String getData() {
    return data;
  }

  @Override
  public void setData(final String data) {
    this.data = data;
  }

}
