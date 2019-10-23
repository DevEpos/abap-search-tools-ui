package com.devepos.adt.saat.internal.menu;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.analytics.OpenWithAnalysisForOfficeExecutable;
import com.devepos.adt.saat.internal.analytics.OpenWithQueryMonitorExecutable;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.navtargets.INavigationTarget;
import com.devepos.adt.saat.internal.navtargets.INavigationTargetService;
import com.devepos.adt.saat.internal.navtargets.NavigationTargetServiceFactory;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAdtObject;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.ObjectContainer;

/**
 * Utility class to create a Menu for some DB Browser actions for a given object
 * (CDS View, Database table or Database view)
 *
 * @author stockbal
 */
public class DynamicOpenInMenuUtility {

	/**
	 * Creates analysis tools menu for the list of ADT Objects
	 *
	 * @param  adtObjects a list of ADT objects
	 * @return            the created menu or <code>null</code>
	 */
	public static IMenuManager buildAnalysisToolsSubMenu(final List<IAdtObject> adtObjects) {
		if (adtObjects == null || adtObjects.isEmpty()) {
			return null;
		}
		final IProject project = adtObjects.get(0).getProject();
		if (project == null) {
			return null;
		}
		if (!AdtUtil.isObjectSearchAvailable(project)) {
			return null;
		}
		return new DynamicOpenInMenuManager(adtObjects, project);
	}

	private static class DynamicOpenInMenuManager extends MenuManager implements IMenuListener {
		private static final String ID = SearchAndAnalysisPlugin.PLUGIN_ID + ".actionsMenu"; //$NON-NLS-1$
		private final List<IAdtObject> adtObjects;
		private final IProject project;
		private final boolean cdsAnalysisAvailable;

		public DynamicOpenInMenuManager(final List<IAdtObject> adtObjects, final IProject project) {
			super(Messages.AdtObjectMenu_MainMenuEntry,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.CDS_ANALYZER), ID);
			this.adtObjects = adtObjects;
			this.project = project;
			this.cdsAnalysisAvailable = this.adtObjects.size() == 1 && AdtUtil.isCdsAnalysisAvailable(project);
			setRemoveAllWhenShown(true);
			addMenuListener(this);
		}

		@Override
		public void menuAboutToShow(final IMenuManager manager) {
			final boolean isDbBrowserAvailable = AdtUtil.isSapGuiDbBrowserAvailable(this.adtObjects);
			// Add command "Open In DB Browser"
			if (isDbBrowserAvailable) {
				MenuItemFactory.addOpenInDbBrowserCommand(this, false);
				MenuItemFactory.addOpenInDbBrowserCommand(this, true);
			}

			if (this.cdsAnalysisAvailable) {
				if (isDbBrowserAvailable) {
					add(new Separator());
				}
				final boolean isCdsView = this.adtObjects.get(0).getObjectType() == ObjectType.CDS_VIEW;
				if (isCdsView) {
					MenuItemFactory.addCdsAnalyzerCommandItem(this, null, ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
				}
				MenuItemFactory.addCdsAnalyzerCommandItem(this, null, ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
				if (isCdsView) {
					MenuItemFactory.addCdsAnalyzerCommandItem(this, null, ICommandConstants.USED_ENTITIES_ANALYSIS);
				}
				MenuItemFactory.addCdsAnalyzerCommandItem(this, null, ICommandConstants.FIELD_ANALYSIS);
				// Additional actions only exist for CDS view at the moment
				if (isCdsView && AdtUtil.isNavigationTargetsFeatureAvailable(DynamicOpenInMenuManager.this.project)) {
					add(new Separator());
					add(new ExternalNavigationTargetsMenu(this.adtObjects.get(0), this.project));
				}
			}

		}

	}

	private static class ExternalNavigationTargetsMenu extends MenuManager implements IMenuListener {
		private static final String ID = SearchAndAnalysisPlugin.PLUGIN_ID + ".externalNavTargetsMenu"; //$NON-NLS-1$
		private final IAdtObject adtObject;
		private final IProject project;

		public ExternalNavigationTargetsMenu(final IAdtObject adtObject, final IProject project) {
			super(Messages.AdtObjectMenu_ExtnernalNavigationTargets_xmit,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.EXTERNAL_TOOLS), ID);
			this.adtObject = adtObject;
			this.project = project;
			setRemoveAllWhenShown(true);
			addMenuListener(this);
		}

		@Override
		public void menuAboutToShow(final IMenuManager manager) {
			createLazyMenuItem();
		}

		private void createLazyMenuItem() {
			final IContributionItem loadingIndicator = addTextContribution(Messages.AdtObjectMenu_LoadingText_xmit);
			final String objectName = this.adtObject.getName();
			final ObjectType objectType = this.adtObject.getObjectType();

			final Job job = new Job(NLS.bind(Messages.AdtObjectMenu_DynamicMenuItemsLoadingJob_xmsg, objectName)) {
				@Override
				protected IStatus run(final IProgressMonitor monitor) {

					final INavigationTargetService service = NavigationTargetServiceFactory
						.createService(ExternalNavigationTargetsMenu.this.project);
					final ObjectContainer<INavigationTarget[]> targetsWrapper = new ObjectContainer<>(null);
					if (service != null) {
						final INavigationTarget[] targets = service.getTargets(objectName, objectType);
						targetsWrapper.setObject(targets);
					}
					monitor.done();
					// update menu after targets are loaded
					PlatformUI.getWorkbench().getDisplay().asyncExec((Runnable) () -> {
						ExternalNavigationTargetsMenu.this.remove(loadingIndicator);
						final INavigationTarget[] targets = targetsWrapper.getObject();
						if (targets != null) {
							addNavigationTargetActions(targets);
						} else {
							addTextContribution(Messages.AdtObjectMenu_NoTargetsFound_xmit);
						}
						ExternalNavigationTargetsMenu.this.update(true);
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule(100);

		}

		private void addNavigationTargetActions(final INavigationTarget[] targets) {
			for (final INavigationTarget target : targets) {
				switch (target.getName()) {
				case "EXCEL": //$NON-NLS-1$
					add(new OpenWithAnalysisForOfficeExecutable(AdtUtil.getDestinationId(this.project), this.adtObject.getName())
						.createAction(target.getDisplayName(), target.getImageId()));
					break;
				case "QUERY_MONITOR": //$NON-NLS-1$
					add(new OpenWithQueryMonitorExecutable(AdtUtil.getDestinationId(this.project), this.adtObject.getName())
						.createAction(target.getDisplayName(), target.getImageId()));
					break;
				}
			}
		}

		private IContributionItem addTextContribution(final String text) {
			final IContributionItem item = new ActionContributionItem(new Action(text) {
				@Override
				public boolean isEnabled() {
					return false;
				}
			});
			add(item);
			return item;
		}
	}
}
