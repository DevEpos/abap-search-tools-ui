package com.devepos.adt.saat;

import org.eclipse.jface.action.IAction;

/**
 * An action represents an action that can be executed on an element
 * information.
 *
 * @author stockbal
 */
public interface IExecutable {

	/**
	 * Executes the logic of the action
	 */
	void execute();

	/**
	 * Creates UI action from executable
	 *
	 * @param  name    the name for the action (tooltip or text)
	 * @param  imageId the image id for the action or <code>null</code>
	 * @return
	 */
	IAction createAction(String name, String imageId);
}
