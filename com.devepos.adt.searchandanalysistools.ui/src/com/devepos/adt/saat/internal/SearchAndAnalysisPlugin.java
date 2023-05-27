package com.devepos.adt.saat.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;

import com.devepos.adt.base.ui.plugin.AbstractAdtUIPlugin;
import com.devepos.adt.saat.internal.search.favorites.IObjectSearchFavorites;
import com.devepos.adt.saat.internal.search.favorites.ObjectSearchFavoriteStorage;
import com.devepos.adt.saat.internal.search.favorites.ObjectSearchFavorites;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * The activator class controls the plug-in life cycle
 */
public class SearchAndAnalysisPlugin extends AbstractAdtUIPlugin {

  public static final String PLUGIN_ID = "com.devepos.adt.searchandanalysistools.ui"; //$NON-NLS-1$

  // The shared instance
  private static SearchAndAnalysisPlugin plugin;

  private IObjectSearchFavorites searchFavorites;

  /**
   * The constructor
   */
  public SearchAndAnalysisPlugin() {
    super(PLUGIN_ID);
  }

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
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
    if (searchFavorites == null) {
      searchFavorites = new ObjectSearchFavorites();
      // deserialize existing search favorites
      ObjectSearchFavoriteStorage.deserialize(searchFavorites);
    }
    return searchFavorites;
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static SearchAndAnalysisPlugin getDefault() {
    return plugin;
  }

  @Override
  protected void initializeImageRegistry(final ImageRegistry imageRegistry) {
    // register all kinds of images
    registerImage(imageRegistry, IImages.DB_BROWSER_DATA_PREVIEW, "icons/DBBrowser.png");
    registerImage(imageRegistry, IImages.ADT_DATA_DATA_PREVIEW, "icons/ADTDataPreview.png");
    registerImage(imageRegistry, IImages.CDS_VIEW, "icons/CDSEntity.png");
    registerImage(imageRegistry, IImages.TABLE_DEFINITION, "icons/Table.png");
    registerImage(imageRegistry, IImages.VIEW_DEFINITION, "icons/ViewDefinition.png");
    registerImage(imageRegistry, IImages.SEARCH_HISTORY, "icons/full/elcl16/search_history.png",
        "org.eclipse.search");
    registerImage(imageRegistry, IImages.HISTORY_LIST, "icons/HistoryList.png");
    registerImage(imageRegistry, IImages.USER, "icons/UserEdit.png");
    registerImage(imageRegistry, IImages.PROPERTIES, "icons/Properties.png");
    registerImage(imageRegistry, IImages.DATE, "icons/Date.png");
    registerImage(imageRegistry, IImages.CLASS, "icons/Class.png");
    registerImage(imageRegistry, IImages.INTERFACE, "icons/Interface.png");
    registerImage(imageRegistry, IImages.CLASS_INTERFACE, "icons/ClassInterface.png");
    registerImage(imageRegistry, IImages.RELEASED, "icons/Success.png");
    registerImage(imageRegistry, IImages.NOT_RELEASED, "icons/Error.png");
    registerImage(imageRegistry, IImages.FOLDER, "icons/Folder.png");
    registerImage(imageRegistry, IImages.VIRTUAL_FOLDER, "icons/VirtualFolder.png");
    registerImage(imageRegistry, IImages.TECHNICAL_SETTINGS, "icons/TechnicalSettings.png");
    registerImage(imageRegistry, IImages.ACCESS_CONTROL, "icons/AccessControl.png");
    registerImage(imageRegistry, IImages.BUSINESS_OBJECT, "icons/BusinessObject.png");
    registerImage(imageRegistry, IImages.METADATA_EXTENSION, "icons/MetadataExtension.png");
    registerImage(imageRegistry, IImages.EXCEL_APPLICATION, "icons/Excel.png");
    registerImage(imageRegistry, IImages.ANALYTICAL_QUERY, "icons/AnalyticalQuery.png");
    registerImage(imageRegistry, IImages.EXTERNAL_TOOLS, "icons/ExternalTools.png");
    registerImage(imageRegistry, IImages.SAP_GUI_LOGO, "icons/SapGuiLogo.png");
    registerImage(imageRegistry, IImages.INNER_JOIN, "icons/InnerJoin.png");
    registerImage(imageRegistry, IImages.OBJECT_SEARCH, "icons/ABAPSearch.png");
    registerImage(imageRegistry, IImages.FAVORITES, "icons/Favorites.png");
    registerImage(imageRegistry, IImages.CDS_ANALYZER, "icons/CdsAnalyzerView.png");
    registerImage(imageRegistry, IImages.USED_ENTITES, "icons/UsedEntities.png");
    registerImage(imageRegistry, IImages.USED_IN_FROM, "icons/UsedInFrom.png");
    registerImage(imageRegistry, IImages.TOP_DOWN, "icons/TopDown.png");
    registerImage(imageRegistry, IImages.FIELD_ANALYSIS, "icons/CdsFieldAnalysis.png");
    registerImage(imageRegistry, IImages.WHERE_USED_IN, "icons/WhereUsedInCds.png");
    registerImage(imageRegistry, IImages.USAGE_ANALYZER, "icons/CdsUsageAnalyzer.png");
    registerImage(imageRegistry, IImages.UNION, "icons/Union.png");
    registerImage(imageRegistry, IImages.JOIN_RESULT_SOURCE, "icons/JoinedDataSource.png");
    registerImage(imageRegistry, IImages.KEY_COLUMN, "icons/KeyColumn.png");
    registerImage(imageRegistry, IImages.FIELD_TOP_DOWN, "icons/FieldTopDown.png");
    registerImage(imageRegistry, IImages.FIELD_WHERE_USED, "icons/FieldWhereUsed.png");
    registerImage(imageRegistry, IImages.CALCULATOR, "icons/Calculator.png");
    registerImage(imageRegistry, IImages.FUNCTION, "icons/Function.png");
    registerImage(imageRegistry, IImages.ABAP_VERSION, "icons/Version.gif");
    registerImage(imageRegistry, IImages.SUPER_TYPE, "icons/SuperType.png");
    registerImage(imageRegistry, IImages.FRIEND, "icons/Friend.png");
    registerImage(imageRegistry, IImages.ATTRIBUTE, "icons/Attribute.png");
    registerImage(imageRegistry, IImages.METHOD, "icons/Method.png");
    registerImage(imageRegistry, IImages.ENABLED_CHECKBOX, "icons/EnabledCheckbox.png");

    // register overlay images
    registerImage(imageRegistry, IImages.RELEASED_API_OVR, "icons/ovr/Released.png");
    registerImage(imageRegistry, IImages.SOURCE_TYPE_FUNCTION_OVR, "icons/ovr/Function.png");
    registerImage(imageRegistry, IImages.SOURCE_TYPE_ABSTRACT_ENTITY_OVR,
        "icons/ovr/AbstractEntity.png");
    registerImage(imageRegistry, IImages.SOURCE_TYPE_PROJECTION_ENTITY_OVR,
        "icons/ovr/ProjectionEntity.png");
    registerImage(imageRegistry, IImages.SOURCE_TYPE_HIERARCHY_ENTITY_OVR,
        "icons/ovr/HierarchyEntity.png");
    registerImage(imageRegistry, IImages.SOURCE_TYPE_CUSTOM_ENTITY_OVR,
        "icons/ovr/CustomEntity.png");

    // Object Search parameter image registrations
    registerImage(imageRegistry, IImages.DESCRIPTION_PARAM, "icons/Documentation.png");
    registerImage(imageRegistry, IImages.PARAMETER_PARAM, "icons/ImportingParameter.png");
    registerImage(imageRegistry, IImages.PACKAGE_PARAM, "icons/obj/package_obj.png",
        "com.sap.adt.tools.core.ui");
    registerImage(imageRegistry, IImages.USER_PARAM, "icons/User.png");
    registerImage(imageRegistry, IImages.API_PARAM, "icons/APIState.png");
    registerImage(imageRegistry, IImages.SELECT_FROM_PARAM, "icons/SelectFromSource.png");
    registerImage(imageRegistry, IImages.FIELD_PARAM, "icons/Column.png");
    registerImage(imageRegistry, IImages.ANNOTATION_PARAM, "icons/Annotation.png");
    registerImage(imageRegistry, IImages.USED_AS_ASSOCICATION_PARAM,
        "icons/AssociationDeclaration.png");
    registerImage(imageRegistry, IImages.MAX_ROWS_PARAM, "icons/Count.png");
    registerImage(imageRegistry, IImages.TYPE_PARAM, "icons/TypeFolder.png");
    registerImage(imageRegistry, IImages.ABAP_TYPE, "icons/TypeGroup.png");
    registerImage(imageRegistry, IImages.EXTENSION, "icons/ExtensionPoint.png");
    registerImage(imageRegistry, IImages.RUN_NEW_ANALYSIS, "icons/RunCdsAnalysis.png");
  }
}
