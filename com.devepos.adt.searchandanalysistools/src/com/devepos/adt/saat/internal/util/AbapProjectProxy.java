package com.devepos.adt.saat.internal.util;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.communication.session.AdtSystemSessionFactory;
import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;

/**
 * Proxy with convencience methods on {@link IProject} and {@link IAbapProject}
 *
 * @author stockbal
 */
public class AbapProjectProxy implements IAbapProjectProvider {

	private Optional<IProject> project;

	/**
	 * @param project
	 */
	public AbapProjectProxy(final IProject project) {
		this.project = Optional.ofNullable(project);
	}

	/**
	 * Updates the project reference in the proxy
	 *
	 * @param project
	 */
	@Override
	public void setProject(final IProject project) {
		this.project = Optional.ofNullable(project);
	}

	/**
	 * Retrieves the ABAP nature from the project
	 *
	 * @return
	 */
	@Override
	public IAbapProject getAbapProject() {
		return this.project.get().getAdapter(IAbapProject.class);
	}

	/**
	 * Ensures that the current user is logged on to the proxy project
	 *
	 * @return
	 */
	@Override
	public boolean ensureLoggedOn() {
		if (!hasProject()) {
			return false;
		}
		final ObjectContainer<Boolean> isLoggedOncontainer = new ObjectContainer<>(Boolean.FALSE);
		Display.getDefault().syncExec(() -> {
			isLoggedOncontainer.setObject(AdtLogonServiceUIFactory.createLogonServiceUI()
				.ensureLoggedOn(getAbapProject().getDestinationData(), PlatformUI.getWorkbench().getProgressService())
				.isOK());
		});
		return isLoggedOncontainer.getObject();
	}

	/**
	 * Retrieves the destination id of the project
	 *
	 * @return
	 */
	@Override
	public String getDestinationId() {
		return getAbapProject().getDestinationId();
	}

	/**
	 * Retrieves the name of the project
	 *
	 * @return
	 */
	@Override
	public String getProjectName() {
		return this.project.get().getName();
	}

	@Override
	public boolean hasProject() {
		return this.project.isPresent();
	}

	@Override
	public IProject getProject() {
		return hasProject() ? this.project.get() : null;
	}

	@Override
	public void openObjectReference(final IAdtObjectReference objectReference) {
		AdtUtil.navigateWithObjectReference(objectReference, this.project.get());
	}

	@Override
	public void openObjectReferenceInSapGui(final IAdtObjectReference objectReference) {
		AdtUtil.openAdtObjectRefInSapGui(objectReference, this.project.get());
	}

	@Override
	public ISystemSession createStatelessSession() {
		return AdtSystemSessionFactory.createSystemSessionFactory().createStatelessSession(getDestinationId());
	}

	@Override
	public IAbapProjectProvider copy() {
		return new AbapProjectProxy(this.project.orElse(null));
	}

	@Override
	public IDestinationData getDestinationData() {
		if (!hasProject()) {
			return null;
		}
		return getAbapProject().getDestinationData();
	}
}
