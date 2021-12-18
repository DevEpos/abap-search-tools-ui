package com.devepos.adt.saat.internal.search;

/**
 * Factory to create instances of the {@link INamedItemService}
 *
 * @author stockbal
 *
 */
public class NamedItemServiceFactory {

  /**
   * Creates instance of the {@link INamedItemService}
   *
   * @param destination ABAP project destination
   * @return
   */
  public static INamedItemService createService(final String destination) {
    return new NamedItemService(destination);
  }
}
