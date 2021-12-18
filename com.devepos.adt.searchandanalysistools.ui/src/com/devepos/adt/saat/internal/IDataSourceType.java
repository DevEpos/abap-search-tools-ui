package com.devepos.adt.saat.internal;

import com.devepos.adt.saat.internal.util.IImages;

/**
 * Marks the type of a data source
 *
 * @author stockbal
 */
public interface IDataSourceType {

  /**
   * Returns the associated Image id for this source type
   *
   * @see IImages
   * @return
   */
  String getImageId();

  /**
   * Returns the internal ID of the source type
   *
   * @return
   */
  String getId();
}
