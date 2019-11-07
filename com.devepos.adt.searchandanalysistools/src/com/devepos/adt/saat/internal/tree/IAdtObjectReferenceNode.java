package com.devepos.adt.saat.internal.tree;

import org.eclipse.core.runtime.IAdaptable;

import com.devepos.adt.saat.internal.ObjectType;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Tree node which holds a reference to a {@link IAdtObjectReference}
 *
 * @author stockbal
 */
public interface IAdtObjectReferenceNode extends ICollectionTreeNode, IAdaptable {

	/**
	 * Returns <code>true</code> if this ADT object reference can be opened in the
	 * Data Preview
	 *
	 * @return <code>true</code> if Data Preview is supported
	 */
	boolean supportsDataPreview();

	/**
	 * Returns the object type of the underlying ADT object reference
	 *
	 * @return
	 */
	ObjectType getObjectType();

	/**
	 * @return the object reference of the node
	 */
	IAdtObjectReference getObjectReference();

	/**
	 * Sets the object reference of the node
	 *
	 * @param objectReference
	 */
	void setObjectReference(IAdtObjectReference objectReference);
}
