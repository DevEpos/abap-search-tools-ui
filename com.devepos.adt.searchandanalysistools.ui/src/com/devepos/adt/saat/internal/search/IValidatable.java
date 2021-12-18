package com.devepos.adt.saat.internal.search;

import org.eclipse.core.runtime.CoreException;

/**
 * An interface for a object that can validate certain values
 *
 * @author stockbal
 *
 */
@FunctionalInterface
public interface IValidatable {

  /**
   * Validates the given value. If it is invalid an exception will be thrown
   *
   * @param value the value to validate
   * @throws CoreException
   */
  void validate(Object value) throws CoreException;
}
