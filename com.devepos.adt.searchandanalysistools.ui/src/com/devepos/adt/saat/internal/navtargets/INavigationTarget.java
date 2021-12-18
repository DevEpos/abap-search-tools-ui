package com.devepos.adt.saat.internal.navtargets;

/**
 * Navigation Target for a single ADT object
 *
 * @author stockbal
 */
public interface INavigationTarget {
  /**
   * Returns the name of the navigation target
   *
   * @return
   */
  String getName();

  /**
   * Returns the display name of the navigation target
   *
   * @return
   */
  String getDisplayName();

  /**
   * Returns the id of the image for this navigation target
   *
   * @return
   */
  String getImageId();

}
