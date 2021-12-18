package com.devepos.adt.saat.internal.ddicaccess;

/**
 * Factory for DDIC Repository Access
 *
 * @author stockbal
 */
public class DdicRepositoryAccessFactory {

  /**
   * Creates new instance for DDIC repository access
   *
   * @return
   */
  public static IDdicRepositoryAccess createDdicAccess() {
    return new DdicRepositoryAccess();
  }
}
