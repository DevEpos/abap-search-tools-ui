package com.devepos.adt.saat.internal.util;

public class ObjectContainer<T extends Object> {
	private T object;

	public ObjectContainer(final T object) {
		this.object = object;
	}

	/**
	 * Sets the given object in the container
	 *
	 * @param object
	 */
	public void setObject(final T object) {
		this.object = object;
	}

	/**
	 * Retrieves the given object out of the container
	 *
	 * @return
	 */
	public T getObject() {
		return this.object;
	}
}
