package com.devepos.adt.saat.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.devepos.adt.saat.internal.ObjectType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.dbbrowserintegration.DbBrowserIntegrationUriDiscovery;
import com.devepos.adt.saat.internal.ddicaccess.DdicRepositoryAccessFactory;
import com.devepos.adt.saat.internal.ddicaccess.IDdicRepositoryAccess;
import com.devepos.adt.saat.internal.navtargets.NavigationTargetsUriDiscovery;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.sap.adt.communication.message.HeadersFactory;
import com.sap.adt.communication.message.IHeaders;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.IProjectProvider;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;

/**
 * ADT convenience methods
 *
 * @author stockbal
 */
@SuppressWarnings("restriction")
public class AdtUtil {
	/**
	 * Overrides the title of an embedded SAP GUI Editor Part with the given
	 * <code>partName</code> and <code>imageId</code>
	 *
	 * @param project      the project of the SAP GUI editor
	 * @param partName     the new name for the part
	 * @param titleToolTip the new tooltip for the part
	 * @param imageId      the Id of the new image of the part
	 */
	public static void overrideSapGuiPartTitle(final WorkbenchPart part, final IProject project, final String partName,
		final String titleToolTip, final String imageId) {
		if (part == null) {
			return;
		}
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final String newPartName = String.format("[%s] %s", abapProject.getSystemId(), partName);
		final String newTitleTooltip = titleToolTip != null
			? String.format("%s [%s]", titleToolTip, abapProject.getDestinationDisplayText())
			: null;

		final Image newPartImage = imageId != null ? SearchAndAnalysisPlugin.getDefault().getImage(imageId) : null;
		final IPropertyListener listener = (object, id) -> {
			if (id == 1) {
				Reflection.forObject(part).invoke("setPartName", new Class[] { String.class }, new Object[] { newPartName });
				if (newTitleTooltip != null) {
					Reflection.forObject(part)
						.invoke("setTitleToolTip", new Class[] { String.class }, new Object[] { newTitleTooltip });
				}
				if (newPartImage != null) {
					Reflection.forObject(part)
						.invoke("setTitleImage", new Class[] { Image.class }, new Object[] { newPartImage });
				}
			}
		};
		part.addPropertyListener(listener);
	}

	/**
	 * Returns the correct image id for the given ADT object type
	 *
	 * @see         IImages
	 * @param  type the ADT object type
	 * @return      the found image id string or <code>null</code>
	 */
	public static String getImageForAdtType(final String type) {
		final ObjectType objectType = ObjectType.getFromAdtType(type);
		if (objectType != null) {
			switch (objectType) {
			case CDS_VIEW:
				return IImages.CDS_VIEW;
			case TABLE:
				return IImages.TABLE_DEFINITION;
			case VIEW:
				return IImages.VIEW_DEFINITION;
			case CLASS:
				return IImages.CLASS;
			case TABLE_SETTINGS:
				return IImages.TECHNICAL_SETTINGS;
			case METADATA_EXT:
				return IImages.METADATA_EXTENSION;
			case BUSINESS_OBJECT:
				return IImages.BUSINESS_OBJECT;
			case ACCESS_CONTROL:
				return IImages.ACCESS_CONTROL;
			case PACKAGE:
				return IImages.PACKAGE;
			default:
				return null;
			}
		}
		return null;
	}

	/**
	 * Retrieve headers for REST request
	 *
	 * @return
	 */
	public static IHeaders getHeaders() {
		final IHeaders headers = HeadersFactory.newHeaders();
		headers.addField(HeadersFactory.newField("Accept", "application/xml"));
		return headers;
	}

	/**
	 * Returns a simple DB Browser Tools compatible ADT object from the current
	 * selection
	 *
	 * @return
	 */
	public static IAdtObject getAdtObjectFromSelection(final boolean supportsDataPreview) {
		final List<IAdtObject> adtObjects = getAdtObjectsFromSelection(supportsDataPreview);
		return adtObjects != null && !adtObjects.isEmpty() ? adtObjects.get(0) : null;
	}

	/**
	 * Returns a List of simple DB Browser Tools compatible ADT objects from the
	 * current selection
	 *
	 * @return
	 */
	public static List<IAdtObject> getAdtObjectsFromSelection(final boolean supportsDataPreview) {
		List<IAdtObject> adtObjects = null;
		final ISelection selection = SelectionUtil.getSelection();

		if (selection != null) {
			if (selection instanceof ITreeSelection) {
				adtObjects = getObjectFromTreeSelection((ITreeSelection) selection);
			} else if (selection instanceof ITextSelection) {
				final IAdtObject adtObject = getObjectFromActiveEditor();
				if (adtObject != null) {
					adtObjects = Arrays.asList(adtObject);
				}
			}
		}
		if (adtObjects != null && supportsDataPreview) {
			adtObjects = adtObjects.stream()
				.filter(obj -> obj.getObjectType().supportsDataPreview())
				.collect(Collectors.toList());
		}
		return adtObjects;
	}

	public static IProject adaptAsProject(final Object object) {
		final IProjectProvider adaptedProjectProvider = Adapters.adapt(object, IProjectProvider.class);
		if (adaptedProjectProvider != null) {
			return adaptedProjectProvider.getProject();
		}
		final IProject adaptedProject = Adapters.adapt(object, IProject.class);
		if (adaptedProject != null) {
			return adaptedProject;
		}
		return null;
	}

	/**
	 * Read destination id from the given project. If the project is not of type
	 * {@link IAbapProject} <code>null</code> will be returned
	 *
	 * @param  project project instance which must be adaptable to type
	 *                 {@link IAbapProject}
	 * @return
	 */
	public static String getDestinationId(final IProject project) {
		if (project == null) {
			return null;
		}
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
		return abapProject != null ? abapProject.getDestinationId() : null;
	}

	/**
	 * Navigates to the given adt object reference
	 *
	 * @param objectReference the object reference for the navigation
	 * @param project         the project the object should be opened in
	 */
	public static void navigateWithObjectReference(final IAdtObjectReference objectReference, final IProject project) {
		if (objectReference == null || project == null) {
			return;
		}

		AdtNavigationServiceFactory.createNavigationService().navigate(project, objectReference, true);
	}

	/**
	 * Opens the given Object reference in an integrated SAP GUI editor in the
	 * current eclipse instance
	 *
	 * @param objectReference the ADT object reference to be opened
	 * @param project         the project where the object reference should be
	 *                        opened in
	 */
	public static void openAdtObjectRefInSapGui(final IAdtObjectReference objectReference, final IProject project) {
		AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
			.openEditorForObject(project, objectReference, true, WorkbenchAction.DISPLAY.toString(), null,
				Collections.<String, String>emptyMap());
	}

	/**
	 * Retrieve a list of all ABAP projects in the current workspace
	 *
	 * @return List of ABAP projects
	 */
	public static IProject[] getAbapProjects() {
		return AdtProjectServiceFactory.createProjectService().getAvailableAbapProjects();
	}

	/**
	 * Ensures that the users is logged on to given project
	 *
	 * @param  project the ABAP Project to ensure the logged on status
	 * @return         <code>true</code> if user is logged on to the given project
	 */
	public static boolean ensureLoggedOnToProject(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		return AdtLogonServiceUIFactory.createLogonServiceUI()
			.ensureLoggedOn(abapProject.getDestinationData(), PlatformUI.getWorkbench().getProgressService())
			.isOK();
	}

	/**
	 * Retrieve the currently active ABAP Project
	 *
	 * @return
	 */
	public static IProject getCurrentAbapProject() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		final ISelection selection = window.getSelectionService().getSelection();
		return ProjectUtil.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);
	}

	/**
	 * Checks if the object search is available in the given project
	 *
	 * @param  project ABAP Project
	 * @return         <code>true</code> if feature is available
	 */
	public static boolean isObjectSearchAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.getObjectSearchUri() != null;
	}

	/**
	 * Checks if navigation targets for an ADT object can be determined
	 *
	 * @param  project ABAP Project
	 * @return         <code>true</code> if feature is available
	 */
	public static boolean isNavigationTargetsFeatureAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
		final NavigationTargetsUriDiscovery uriDiscovery = new NavigationTargetsUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.getNavTargetsUri() != null;
	}

	/**
	 * Checks if the CDS Analysis feature is available for the given project
	 *
	 * @param  project ABAP Project
	 * @return         <code>true</code> if feature is available
	 */
	public static boolean isCdsAnalysisAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.getCdsAnalysisUri() != null;
	}

	/**
	 * Checks if the CDS Top Down analysis is available in the given project
	 *
	 * @param  project ABAP Project
	 * @return         <code>true</code> if feature is available
	 */
	public static boolean isCdsTopDownAnalysisAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.isTopDownAnalysisAvailable();
	}

	/**
	 * Checks if the CDS Used Entities Analysis feature is available for the given
	 * project
	 *
	 * @param  project ABAP Project
	 * @return         <code>true</code> if feature is available
	 */
	public static boolean isCdsUsedEntitiesAnalysisAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.isUsedEntitiesAnalysisAvailable();
	}

	/**
	 * Returns <code>true</code> if the DB Browser Application is available in the
	 * given project
	 *
	 * @param  project the project where the availability of the DB Browser should
	 *                 be checked
	 * @return         <code>true</code> if the DB Browser Application is available
	 *                 in the given project
	 */
	public static boolean isSapGuiDbBrowserAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

		final IUriDiscovery uriDiscovery = new DbBrowserIntegrationUriDiscovery(abapProject.getDestinationId());
		return uriDiscovery.isResourceDiscoverySuccessful();
	}

	/**
	 * Returns <code>true</code> if the DB Browser integration feature is availabe
	 * in the projects of the given ADT Objects
	 *
	 * @param  adtObjects a list of ADT Objects
	 * @return            <code>true</code> if the DB Browser integration feature is
	 *                    availabe in the projects of the given ADT Objects
	 */
	public static boolean isSapGuiDbBrowserAvailable(final List<IAdtObject> adtObjects) {
		if (adtObjects == null || adtObjects.isEmpty()) {
			return false;
		}
		if (adtObjects.size() == 1) {
			return isSapGuiDbBrowserAvailable(adtObjects.get(0).getProject());
		}
		final Set<IProject> uniqueProjects = new HashSet<>();
		for (final IAdtObject adtObject : adtObjects) {
			uniqueProjects.add(adtObject.getProject());
		}

		for (final IProject project : uniqueProjects) {
			if (!isSapGuiDbBrowserAvailable(project)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the ADT object from currently active editor or <code>null</code> if
	 * the editor content cannot be adapted to an instance of {@link IAdtObject}
	 *
	 * @return
	 */
	public static IAdtObject getObjectFromActiveEditor() {
		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		final IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor == null) {
			return null;
		}
		final IEditorInput input = activeEditor.getEditorInput();
		if (input == null) {
			return null;
		}
		return Adapters.adapt(input, IAdtObject.class);
	}

	/**
	 * Performs an ADT Link navigation to the given column in the given entity
	 *
	 * @param entityName    the name of an Database entity
	 * @param fieldName     the name of the field which should be selected after the
	 *                      editor opened
	 * @param destinationId the destination id of the ABAP project
	 */
	public static void navigateToEntityColumn(final String entityName, final String fieldName, final String destinationId) {
		final Job loadFieldUriJob = Job.create(NLS.bind("Load Field URI for ''{0}.{1}''", entityName, fieldName), (monitor) -> {
			final IDdicRepositoryAccess ddicRepoAccess = DdicRepositoryAccessFactory.createDdicAccess();
			final IAdtObjectReference adtObjectRef = ddicRepoAccess.getColumnUri(destinationId, entityName, fieldName);
			if (adtObjectRef != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
					AdtUtil.navigateWithObjectReference(adtObjectRef,
						AbapProjectProviderAccessor.getProviderForDestination(destinationId).getProject());
				});
			}
			monitor.done();
		});
		loadFieldUriJob.schedule();
	}

	private static List<IAdtObject> getObjectFromTreeSelection(final ITreeSelection selection) {
		List<IAdtObject> adtObjects = null;
		for (final Object selectionItem : selection.toList()) {
			final IAdtObject adtObject = Adapters.adapt(selectionItem, IAdtObject.class);
			if (adtObject == null) {
				continue;
			}
			if (adtObjects == null) {
				adtObjects = new ArrayList<>();
			}

			adtObjects.add(adtObject);

		}
		return adtObjects;
	}

}
