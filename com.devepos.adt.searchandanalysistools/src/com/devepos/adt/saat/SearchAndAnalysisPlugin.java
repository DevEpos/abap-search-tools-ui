package com.devepos.adt.saat;

import java.net.URL;
import java.util.stream.Stream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.devepos.adt.saat.internal.search.favorites.ObjectSearchFavoriteStorage;
import com.devepos.adt.saat.internal.search.favorites.ObjectSearchFavorites;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorites;

/**
 * The activator class controls the plug-in life cycle
 */
public class SearchAndAnalysisPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.devepos.adt.searchandanalysistools"; //$NON-NLS-1$

	// The shared instance
	private static SearchAndAnalysisPlugin plugin;

	private IObjectSearchFavorites searchFavorites;

	/**
	 * The constructor
	 */
	public SearchAndAnalysisPlugin() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener((part, selection) -> {
//			Logging.getLogger(getClass()).log(new Status(IStatus.INFO, PLUGIN_ID, selection.toString() + "," + part.getTitle()));
//		});
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns reference to the favorites of the object search
	 *
	 * @return
	 */
	public IObjectSearchFavorites getFavoriteManager() {
		if (this.searchFavorites == null) {
			this.searchFavorites = new ObjectSearchFavorites();
			// deserialize existing search favorites
			ObjectSearchFavoriteStorage.deserialize(this.searchFavorites);
		}
		return this.searchFavorites;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SearchAndAnalysisPlugin getDefault() {
		return plugin;
	}

	/**
	 * Gets the image descriptor for the given key from the image registry
	 *
	 * @param  key the identifier of the image to get
	 * @return     the found image descriptor or <code>null</code>
	 */
	public ImageDescriptor getImageDescriptor(final String key) {
		return getImageRegistry().getDescriptor(key);
	}

	/**
	 * Retrieves Image from registry
	 *
	 * @param  key the identifier of the image to retrieve
	 * @return     the found image
	 */
	public Image getImage(final String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * Retrieves the dialog settings for the given name
	 *
	 * @param  name the name of the dialog settings to retrieve
	 * @return      the found dialog settings
	 */
	public IDialogSettings getDialogSettingsSection(final String name) {
		final IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null) {
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

	/**
	 * Returns a decorated {@link Image} with the <code>image</code> as the source
	 * image, the <code>decoratorKey</code> as the key for the overlay image and the
	 * <code>decorationPosition</code> as the
	 *
	 * @param  image           the image to be decorated with overlays
	 * @param  overlayImageIds an array of exactly 5 id of Images
	 * @see                    IImages
	 * @return
	 */
	public Image overlayImage(final Image image, final String[] overlayImageIds) {
		if (overlayImageIds == null || overlayImageIds.length > IDecoration.UNDERLAY) {
			return image;
		}
		if (!Stream.of(overlayImageIds).anyMatch(id -> id != null)) {
			return image;
		}
		final StringBuffer overlayImageKeyBuffer = new StringBuffer(image.toString());
		for (int i = 0; i < overlayImageIds.length; i++) {
			final String imageId = overlayImageIds[i];
			if (imageId == null) {
				continue;
			}
			overlayImageKeyBuffer.append(String.valueOf(i)).append("_").append(imageId);
		}
		final String overlayImageKey = overlayImageKeyBuffer.toString();

		Image decoratedImage = getImageRegistry().get(overlayImageKey.toString());
		if (decoratedImage == null) {
			final ImageDescriptor[] overlays = new ImageDescriptor[overlayImageIds.length];
			for (int i = 0; i < overlays.length; i++) {
				final String imageId = overlayImageIds[i];
				if (imageId != null) {
					overlays[i] = getImageDescriptor(imageId);
				} else {
					overlays[i] = null;
				}
			}
			final DecorationOverlayIcon doi = new DecorationOverlayIcon(image, overlays); // , new Point(18, 16));
			decoratedImage = doi.createImage();
			getImageRegistry().put(overlayImageKey, decoratedImage);
		}
		return decoratedImage;
	}

	/**
	 * Returns a decorated {@link Image} with the <code>image</code> as the source
	 * image, the <code>decoratorKey</code> as the key for the overlay image and the
	 * <code>decorationPosition</code> as the
	 *
	 * @param  image              the image to be decorated
	 * @param  overlayImageKey    the id for the overlay image
	 * @param  decorationPosition the position where the overlay image should be
	 *                            placed
	 * @return
	 */
	public Image overlayImage(final Image image, final String overlayImageKey, final int decorationPosition) {
		if (overlayImageKey == null) {
			return image;
		}
		final String decoratedImageKey = String.valueOf(image.toString()) + overlayImageKey;
		Image decoratedImage = getImageRegistry().get(decoratedImageKey);
		if (decoratedImage == null) {
			final ImageDescriptor[] overlays = new ImageDescriptor[4];
			final ImageDescriptor overlay = getImageDescriptor(overlayImageKey);
			if (overlay == null) {
				return image;
			}
			switch (decorationPosition) {
			case IDecoration.TOP_LEFT:
				overlays[0] = overlay;
				break;
			case IDecoration.TOP_RIGHT:
				overlays[1] = overlay;
				break;
			case IDecoration.BOTTOM_LEFT:
				overlays[2] = overlay;
				break;
			case IDecoration.BOTTOM_RIGHT:
				overlays[3] = overlay;
				break;
			}
			final DecorationOverlayIcon doi = new DecorationOverlayIcon(image, overlays, new Point(21, 16));
			decoratedImage = doi.createImage();
			getImageRegistry().put(decoratedImageKey, decoratedImage);
		}
		return decoratedImage;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry) {
		// register all kinds of images
		registerImage(imageRegistry, IImages.DB_BROWSER_DATA_PREVIEW, "icons/DBBrowser.png");
		registerImage(imageRegistry, IImages.ADT_DATA_DATA_PREVIEW, "icons/ADTDataPreview.png");
		registerImage(imageRegistry, IImages.REFRESH, "icons/full/elcl16/refresh.png", "org.eclipse.search");
		registerImage(imageRegistry, IImages.CDS_VIEW, "icons/CDSEntity.png");
		registerImage(imageRegistry, IImages.TABLE_DEFINITION, "icons/Table.png");
		registerImage(imageRegistry, IImages.VIEW_DEFINITION, "icons/ViewDefinition.png");
		registerImage(imageRegistry, IImages.SEARCH_HISTORY, "icons/full/elcl16/search_history.png", "org.eclipse.search");
		registerImage(imageRegistry, IImages.HISTORY_LIST, "icons/HistoryList.png");
		registerImage(imageRegistry, IImages.USER, "icons/UserEdit.png");
		registerImage(imageRegistry, IImages.WAITING, "icons/WaitingIndicator.png");
		registerImage(imageRegistry, IImages.PROPERTIES, "icons/Properties.png");
		registerImage(imageRegistry, IImages.DATE, "icons/Date.png");
		registerImage(imageRegistry, IImages.CLASS, "icons/Class.png");
		registerImage(imageRegistry, IImages.RELEASED, "icons/Success.png");
		registerImage(imageRegistry, IImages.NOT_RELEASED, "icons/Error.png");
		registerImage(imageRegistry, IImages.FOLDER, "icons/Folder.png");
		registerImage(imageRegistry, IImages.VIRTUAL_FOLDER, "icons/VirtualFolder.png");
		registerImage(imageRegistry, IImages.TECHNICAL_SETTINGS, "icons/TechnicalSettings.png");
		registerImage(imageRegistry, IImages.EXPAND_ALL, "icons/full/elcl16/expandall.png", "org.eclipse.search");
		registerImage(imageRegistry, IImages.COLLAPSE_ALL, "icons/full/elcl16/collapseall.png", "org.eclipse.search");
		registerImage(imageRegistry, IImages.ACCESS_CONTROL, "icons/AccessControl.png");
		registerImage(imageRegistry, IImages.BUSINESS_OBJECT, "icons/BusinessObject.png");
		registerImage(imageRegistry, IImages.METADATA_EXTENSION, "icons/MetadataExtension.png");
		registerImage(imageRegistry, IImages.EXCEL_APPLICATION, "icons/Excel.png");
		registerImage(imageRegistry, IImages.ANALYTICAL_QUERY, "icons/AnalyticalQuery.png");
		registerImage(imageRegistry, IImages.EXTERNAL_TOOLS, "icons/ExternalTools.png");
		registerImage(imageRegistry, IImages.SAP_GUI_LOGO, "icons/SapGuiLogo.png");
		registerImage(imageRegistry, IImages.INNER_JOIN, "icons/InnerJoin.png");
		registerImage(imageRegistry, IImages.SEARCH, "icons/full/etool16/search.png", "org.eclipse.search");
		registerImage(imageRegistry, IImages.OBJECT_SEARCH, "icons/ABAPSearch.png");
		registerImage(imageRegistry, IImages.FAVORITES, "icons/Favorites.png");
		registerImage(imageRegistry, IImages.CDS_ANALYZER, "icons/CdsAnalyzerView.png");
		registerImage(imageRegistry, IImages.USED_ENTITES, "icons/UsedEntities.png");
		registerImage(imageRegistry, IImages.USED_IN_FROM, "icons/UsedInFrom.png");
		registerImage(imageRegistry, IImages.TOP_DOWN, "icons/TopDown.png");
		registerImage(imageRegistry, IImages.FIELD_ANALYSIS, "icons/CdsFieldAnalysis.png");
		registerImage(imageRegistry, IImages.WHERE_USED_IN, "icons/WhereUsedInCds.png");
		registerImage(imageRegistry, IImages.WHERE_USED_LIST, "icons/etool/where_used.png", "com.sap.adt.ris.whereused.ui");
		registerImage(imageRegistry, IImages.USAGE_ANALYZER, "icons/CdsUsageAnalyzer.png");
		registerImage(imageRegistry, IImages.UNION, "icons/Union.png");
		registerImage(imageRegistry, IImages.JOIN_RESULT_SOURCE, "icons/JoinedDataSource.png");
		registerImage(imageRegistry, IImages.KEY_COLUMN, "icons/KeyColumn.png");
		registerImage(imageRegistry, IImages.FIELD_TOP_DOWN, "icons/FieldTopDown.png");
		registerImage(imageRegistry, IImages.FIELD_WHERE_USED, "icons/FieldWhereUsed.png");
		registerImage(imageRegistry, IImages.CALCULATOR, "icons/Calculator.png");
		registerImage(imageRegistry, IImages.FUNCTION, "icons/Function.png");
		registerImage(imageRegistry, IImages.IMPORT, "icons/full/etool16/import_wiz.png", "org.eclipse.ui");
		registerImage(imageRegistry, IImages.EXPORT, "icons/full/etool16/export_wiz.png", "org.eclipse.ui");

		// register overlay images
		registerImage(imageRegistry, IImages.RELEASED_API_OVR, "icons/ovr/Released.png");
		registerImage(imageRegistry, IImages.SOURCE_TYPE_FUNCTION_OVR, "icons/ovr/Function.png");
		registerImage(imageRegistry, IImages.SOURCE_TYPE_ABSTRACT_ENTITY_OVR, "icons/ovr/AbstractEntity.png");
		registerImage(imageRegistry, IImages.SOURCE_TYPE_CUSTOM_ENTITY_OVR, "icons/ovr/CustomEntity.png");

		// Object Search parameter image registrations
		registerImage(imageRegistry, IImages.DESCRIPTION_PARAM, "icons/Documentation.png");
		registerImage(imageRegistry, IImages.PARAMETER_PARAM, "icons/ImportingParameter.png");
		registerImage(imageRegistry, IImages.PACKAGE_PARAM, "/icons/Package.png");
		registerImage(imageRegistry, IImages.USER_PARAM, "icons/User.png");
		registerImage(imageRegistry, IImages.API_PARAM, "icons/APIState.png");
		registerImage(imageRegistry, IImages.SELECT_FROM_PARAM, "icons/SelectFromSource.png");
		registerImage(imageRegistry, IImages.FIELD_PARAM, "icons/Column.png");
		registerImage(imageRegistry, IImages.ANNOTATION_PARAM, "icons/Annotation.png");
		registerImage(imageRegistry, IImages.USED_AS_ASSOCICATION_PARAM, "icons/AssociationDeclaration.png");
		registerImage(imageRegistry, IImages.MAX_ROWS_PARAM, "icons/Count.png");
		registerImage(imageRegistry, IImages.TYPE_PARAM, "icons/TypeFolder.png");
		registerImage(imageRegistry, IImages.ABAP_TYPE, "icons/TypeGroup.png");
		registerImage(imageRegistry, IImages.EXTENSION, "icons/ExtensionPoint.png");
	}

	/**
	 * Registers the image with the given image
	 *
	 * @param imageId
	 * @param fileName
	 * @param bundleId
	 */
	private void registerImage(final ImageRegistry registry, final String imageId, final String fileName, final String bundleId) {
		final Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			return;
		}
		final IPath path = new Path(fileName);
		final URL url = FileLocator.find(bundle, path, null);
		if (url == null) {
			return;
		}
		final ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		if (registry.get(imageId) != null) {
			throw new IllegalStateException("duplicate imageId in image registry.");
		}
		registry.put(imageId, desc);
	}

	private void registerImage(final ImageRegistry registry, final String imageId, final String fileName) {
		registerImage(registry, imageId, fileName, PLUGIN_ID);
	}

}
