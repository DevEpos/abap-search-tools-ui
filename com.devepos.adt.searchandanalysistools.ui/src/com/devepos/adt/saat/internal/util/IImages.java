package com.devepos.adt.saat.internal.util;

/**
 * Shared Image identifiers for the plugin
 *
 * @author stockbal
 */
public interface IImages {
  /**
   * Identifies the Image for the ADT Data Preview
   */
  String ADT_DATA_DATA_PREVIEW = "IMG_ADT_DATA_PREVIEW";
  /**
   * Identifies the Image for the DB Browser Data Preview
   */
  String DB_BROWSER_DATA_PREVIEW = "IMG_DATA_PREVIEW";
  /**
   * Identifies the CDS View Entity Image
   */
  String CDS_VIEW = "IMG_CDS_VIEW";
  /**
   * Identifies the image for a Database table definition
   */
  String TABLE_DEFINITION = "IMG_TABLE";
  /**
   * Identifies the image for a Database view
   */
  String VIEW_DEFINITION = "IMG_SELECT";
  /**
   * Identifies image for class
   */
  String CLASS = "IMG_CLASS";
  /**
   * Identifies image for interface
   */
  String INTERFACE = "IMG_INTERFACE";
  /**
   * Identifies image for class/interface
   */
  String CLASS_INTERFACE = "IMG_CLASS_INTF";
  /**
   * Icon for a list of history entries
   */
  String HISTORY_LIST = "IMG_HISTORY_LIST";
  /**
   * Identifies the image for the Search history
   */
  String SEARCH_HISTORY = "IMG_SEARCH_HISTORY";
  /**
   * Identifies the image for the user parameter
   */
  String USER_PARAM = "IMG_USER";
  /**
   * Identifies the image for a SAP User Logon
   */
  String USER = "IMG_USER_EDIT";
  /**
   * Identifies the image for the API Release state parameter
   */
  String API_PARAM = "IMG_API";
  /**
   * Identifies the image for the <strong>Select From</strong> Parameter
   */
  String SELECT_FROM_PARAM = "IMG_SELECT_FROM";
  /**
   * Identifies the image for a SELECT data source
   */
  String DATA_SOURCE = SELECT_FROM_PARAM;
  /**
   * Identifies the image for the Package parameter
   */
  String PACKAGE_PARAM = "IMG_PACKAGE";
  /**
   * Identifies the image for the Field parameter
   */
  String FIELD_PARAM = "IMG_COLUMN";
  /**
   * Identifies the image for a column/field
   */
  String COLUMN = "IMG_COLUMN";
  /**
   * Identifies the image for a key column/field
   */
  String KEY_COLUMN = "IMG_KEY_COLUMN";
  /**
   * Identifies the image for the annotation parameter
   */
  String ANNOTATION_PARAM = "IMG_ANNOTATION";
  /**
   * Identifies the image for the <strong>Used as association</strong> parameter
   */
  String USED_AS_ASSOCICATION_PARAM = "IMG_ASSOCIATION";
  /**
   * Identifies an image for a CDS association
   */
  String ASSOCIATION = USED_AS_ASSOCICATION_PARAM;
  /**
   * Identifies the image for the Description parameter
   */
  String DESCRIPTION_PARAM = "IMG_DOCU";
  /**
   * Identifies the image for the <em>Parameter</em> parameter
   */
  String PARAMETER_PARAM = "IMG_PARAMETER";
  /**
   * Identifies the image for the <em>Max rows</em> parameter
   */
  String MAX_ROWS_PARAM = "IMG_NUMBER";
  /**
   * Identifies the iamge for the <em>type</em> parameter
   */
  String TYPE_PARAM = "IMG_TYPE";
  /**
   * Identifies an image for a type folder
   */
  String TYPE_GROUP = "IMG_TYPE";
  /**
   * Identifies the <em>type</em> image
   */
  String ABAP_TYPE = "IMG_ABAP_TYPE";
  /**
   * Identifies an image for the <em>Extended by</em> parameter
   */
  String EXTENSION = "IMG_EXTENSION";
  /**
   * Identifies an image for an extension view
   */
  String EXTENSION_VIEW = CDS_VIEW;
  /**
   * Identifies an image for properties
   */
  String PROPERTIES = "IMG_PROPERTIES";
  /**
   * Identifies an image for Date
   */
  String DATE = "IMG_DATE";
  /**
   * Identifies an image to signal that something is not released
   */
  String NOT_RELEASED = "IMG_ERROR";
  /**
   * Identifies an image to signal that something is released
   */
  String RELEASED = "IMG_SUCCESS";
  /**
   * Folder Image
   */
  String FOLDER = "IMG_FOLDER";
  /**
   * Virtual Folder Image
   */
  String VIRTUAL_FOLDER = "IMG_VIRTUAL_FOLDER";
  /**
   * Technical Table Settings image
   */
  String TECHNICAL_SETTINGS = "IMG_TECH_SETTINGS";
  /**
   * Image for an ABAP Access Control File
   */
  String ACCESS_CONTROL = "IMG_ACCESS_CONTROl";
  /**
   * Image for an ABAP DDLS Metadata Extension file
   */
  String METADATA_EXTENSION = "IMG_METADATA_EXT";
  /**
   * Image for an ABAP Business Object
   */
  String BUSINESS_OBJECT = "IMG_BOBF";
  /**
   * Image for Excel
   */
  String EXCEL_APPLICATION = "IMG_EXCEL";
  /**
   * Image for an analytical query
   */
  String ANALYTICAL_QUERY = "IMG_ANALYTICAL_QUERY";
  /**
   * Images for external tools
   */
  String EXTERNAL_TOOLS = "IMG_EXTERNAL_TOOLS";
  /**
   * Image for SAP GUI Logo
   */
  String SAP_GUI_LOGO = "IMG_SAP_GUI";
  /**
   * Image for SQL inner join condition
   */
  String INNER_JOIN = "IMG_INNER_JOIN";
  /**
   * Image for the object search
   */
  String OBJECT_SEARCH = "IMG_OBJECT_SEARCH";
  /**
   * Image for Favorites
   */
  String FAVORITES = "IMG_FAVORITES";
  /**
   * Image for Cds Hierarchy view
   */
  String CDS_ANALYZER = "IMG_HIERARCHY";
  /**
   * Image for Entities that were used in from clause of other CDS views
   */
  String USED_IN_FROM = "IMG_USED_IN_FROM";
  /**
   * Image for entities used in from clause of current CDS
   */
  String USED_ENTITES = "IMG_USED_ENTITIES";
  /**
   * Image for CDS usage analyzer
   */
  String USAGE_ANALYZER = "IMG_CDS_USAGE_ANALYZER";
  /**
   * Image for SQL Union
   */
  String UNION = "IMG_UNION";
  /**
   * Image for Select part of SQL
   */
  String SELECT_PART = "IMG_SELECT";
  /**
   * Image for a Join Result of a SQL Select
   */
  String JOIN_RESULT_SOURCE = "IMG_JOIN_RESULT";
  /**
   * Image for an overlay image which signals a released API
   */
  String RELEASED_API_OVR = "IMG_OVR_RELEASED";
  /**
   * Image for Where used in analysis
   */
  String WHERE_USED_IN = "IMG_WHERE_USED_IN";
  /**
   * Image for Top-Down analysis
   */
  String TOP_DOWN = "IMG_TOP_DOWN";
  /**
   * Image for Field Top-Down Analysis
   */
  String FIELD_TOP_DOWN = "IMG_FIELD_TOP_DOWN";
  /**
   * Image for Field Where-Used Analysis
   */
  String FIELD_WHERE_USED = "IMG_FIELD_WHERE_USED";
  /**
   * Image id for a CDS View which has the source type "Table Function"
   */
  String SOURCE_TYPE_FUNCTION_OVR = "IMG_SOURCE_TYPE_FUNCTION";
  /**
   * Image id for a CDS view which has the source type "Abstract Entity"
   */
  String SOURCE_TYPE_ABSTRACT_ENTITY_OVR = "IMG_SOURCE_TYPE_ABSTRACT_ENTITY";
  /**
   * Image id for a CDS view which has the source type "Custom Entity"
   */
  String SOURCE_TYPE_CUSTOM_ENTITY_OVR = "IMG_SOURCE_TYPE_CUSTOM_ENTITY";
  /**
   * Image id for a projection CDS view
   */
  String SOURCE_TYPE_PROJECTION_ENTITY_OVR = "IMG_SOURCE_TYPE_PROJECTION_ENTITY";
  /**
   * Image id for a hierarchy CDS view
   */
  String SOURCE_TYPE_HIERARCHY_ENTITY_OVR = "IMG_SOURCE_TYPE_HIERARCHY_ENTITY";
  /**
   * Image which identifies the Field Analysis page in the CDS Analyzer
   */
  String FIELD_ANALYSIS = "IMG_FIELD_ANALYSIS";
  /**
   * Image to mark elements which were calculated
   */
  String CALCULATOR = "IMG_CALCULATOR";
  /**
   * Image to mark elements which are functional
   */
  String FUNCTION = "IMG_FUNCTION";
  /**
   * Image id for "Package"
   */
  String PACKAGE = PACKAGE_PARAM;
  /**
   * Image id for "Version"
   */
  String ABAP_VERSION = "IMG_VERSION";
  /**
   * Images id for "Global Friend"
   */
  String FRIEND = "IMG_FRIEND";
  /**
   * Image id for "Super type"
   */
  String SUPER_TYPE = "IMG_SUPER_TYPE";
  /**
   * Image id for "Method"
   */
  String METHOD = "IMG_METHOD";
  /**
   * Image id for "Attribute"
   */
  String ATTRIBUTE = "IMG_ATTRIBUTE";
  /**
   * Image id for "Enabled Checkbox"
   */
  String ENABLED_CHECKBOX = "IMG_ENABLED_CHECKBOX";
  /**
   * Image id for running a new CDS analysis
   */
  String RUN_NEW_ANALYSIS = "IMG_RUN_NEW_ANALYSIS";
}
