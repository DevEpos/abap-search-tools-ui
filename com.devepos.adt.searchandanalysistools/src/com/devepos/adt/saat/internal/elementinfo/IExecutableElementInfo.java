package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.IExecutable;

/**
 * Element information that holds an Action
 *
 * @author stockbal
 */
public interface IExecutableElementInfo extends IElementInfo {
	/**
	 * Returns the action of this element information
	 * 
	 * @return
	 */
	IExecutable getExecutable();
}
