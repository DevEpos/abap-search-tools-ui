package com.devepos.adt.saat.internal.navtargets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;

import com.devepos.adt.tools.base.project.ProjectUtil;

/**
 * Factory for creating instances of {@link INavigationTargetService}
 *
 * @author stockbal
 */
public class NavigationTargetServiceFactory {

	public static INavigationTargetService createService(final IProject project) {
		Assert.isTrue(project != null);
		return new NavigationTargetService(ProjectUtil.getDestinationId(project));
	}
}
