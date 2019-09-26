package com.devepos.adt.saat.internal.ui;

import java.util.List;

import org.eclipse.jface.action.Action;

import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Action for opening a list of ADT Objects
 *
 * @author stockbal
 */
public class OpenAdtObjectAction extends Action {
	private final List<IAdtObjectReference> adtObjectReferences;
	private final IAbapProjectProvider projectProvider;

	public OpenAdtObjectAction(final IAbapProjectProvider projectProvider, final List<IAdtObjectReference> adtObjectReferences) {
		super(Messages.ObjectSearch_OpenObject_xmit);
		this.adtObjectReferences = adtObjectReferences;
		this.projectProvider = projectProvider;
	}

	@Override
	public void run() {
		for (final IAdtObjectReference objectRef : this.adtObjectReferences) {
			this.projectProvider.openObjectReference(objectRef);
		}
	}

}