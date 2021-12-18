package com.devepos.adt.saat.internal.navtargets;

/**
 * Navigation Target for a single ADT object
 *
 * @author stockbal
 */
public class NavigationTarget implements INavigationTarget {

  private final String name;
  private final String displayName;
  private final String imageId;

  public NavigationTarget(final String name, final String imageId) {
    this(name, name, imageId);
  }

  public NavigationTarget(final String name, final String displayName, final String imageId) {
    this.name = name;
    this.displayName = displayName;
    this.imageId = imageId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getImageId() {
    return imageId;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

}
