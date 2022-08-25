package com.devepos.adt.saat.internal.search;

import java.text.MessageFormat;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.AdtBaseUIResources;
import com.devepos.adt.base.ui.IAdtBaseImages;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Factory for creating parameters for the Object Search
 *
 * @author stockbal
 */
public class SearchParameterFactory {

  /**
   * Creates description parameter
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createDescriptionParameter() {
    return new SearchParameter(QueryParameterName.DESCRIPTION, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionDescriptionParameter_xmsg,
        QueryParameterName.DESCRIPTION.getLowerCaseKey(), "*material*"), //$NON-NLS-1$
        SearchAndAnalysisPlugin.getDefault().getImage(IImages.DESCRIPTION_PARAM), true, true, true);
  }

  /**
   * Creates parameter for restricting CDS View search results to CDS View that
   * have (not) parameters
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createHasParameterParameter() {
    return new BooleanSearchParameter(QueryParameterName.HAS_PARAMS, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionParamsParameter_xmsg,
        QueryParameterName.HAS_PARAMS.getLowerCaseKey(), "true"), //$NON-NLS-1$
        SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM));
  }

  /**
   * Creates parameter for restricting CDS View search results to CDS Views with a
   * certain parameter
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createCdsParamParameter() {
    return new SearchParameter(QueryParameterName.CDS_PARAMETER, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionParamParameter_xmsg,
        QueryParameterName.CDS_PARAMETER.getLowerCaseKey(), "p_plant"), //$NON-NLS-1$
        SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM), true, true, true);
  }

  /**
   * Creates search parameter for CDS types
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createCdsTypeParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.TYPE, NamedItemType.CDS_TYPE, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionCdsTypeParameter_xmsg, QueryParameterName.TYPE
            .getLowerCaseKey(), "function")); //$NON-NLS-1$
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates search parameter for table types
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createTableTypeParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.TYPE, NamedItemType.TABLE_TYPE, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionTableTypeParameter_xmsg, QueryParameterName.TYPE
            .getLowerCaseKey(), "table")); //$NON-NLS-1$
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM));
//		parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates search parameter for Class Category
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createClassCategoryParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.CATEGORY, NamedItemType.CLASS_CATEGORY, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionClassCategoryParameter_xmsg,
        QueryParameterName.CATEGORY.getLowerCaseKey()));
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.FOLDER));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates search parameter for ABAP Language Version
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createAbapLanguageParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.ABAP_LANGUAGE, NamedItemType.ABAP_CLASS_LANGUAGE, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionAbapLangParameter_xmsg,
        QueryParameterName.ABAP_LANGUAGE.getLowerCaseKey()));
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.ABAP_VERSION));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates search parameter for Class types
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createClassTypeParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.TYPE, NamedItemType.CLASS_TYPE, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionClassTypeParameter_xmsg, QueryParameterName.TYPE
            .getLowerCaseKey()));
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates search parameter for Class Flags (e.g. "Is Abstract")
   *
   * @param projectProvider provider for ABAP Project
   * @return the created parameter instance
   */
  public static ISearchParameter createClassFlagParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.FLAG, NamedItemType.CLASS_FLAG, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionFlagParameter_xmsg, QueryParameterName.FLAG
            .getLowerCaseKey()));
    parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.ENABLED_CHECKBOX));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }

  /**
   * Creates parameter for restricting search to classes with a given "global
   * friend"
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createFriendParameter() {
    return new SearchParameter(QueryParameterName.FRIEND, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionFriendParameter_xmsg, QueryParameterName.FRIEND
            .getLowerCaseKey()), SearchAndAnalysisPlugin.getDefault().getImage(IImages.FRIEND),
        true, true, true);
  }

  /**
   * Creates parameter for restricting search to classes with a given "Super
   * Class"
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createSuperTypeParameter() {
    return new SearchParameter(QueryParameterName.SUPER_TYPE, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionSuperTypeParameter_xmsg,
        QueryParameterName.SUPER_TYPE.getLowerCaseKey()), SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.SUPER_TYPE), true, true, true);
  }

  /**
   * Creates parameter for restricting search to classes/interfaces that implement
   * certain interfaces
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createInterfaceParameter() {
    return new SearchParameter(QueryParameterName.INTERFACE, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionInterfaceParameter_xmsg,
        QueryParameterName.INTERFACE.getLowerCaseKey()), SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.INTERFACE), true, true, true);
  }

  /**
   * Creates parameter for restricting search to classes/interfaces that have
   * certain methods
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createMethodParameter() {
    return new SearchParameter(QueryParameterName.METHOD, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionMethodParameter_xmsg, QueryParameterName.METHOD
            .getLowerCaseKey()), SearchAndAnalysisPlugin.getDefault().getImage(IImages.METHOD),
        true, true, true);
  }

  /**
   * Creates parameter for restricting search to classes/interfaces that have
   * certain attributes
   *
   * @return the created parameter instance
   */
  public static ISearchParameter createAttributeParameter() {
    return new SearchParameter(QueryParameterName.ATTRIBUTE, MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionAttributeParameter_xmsg,
        QueryParameterName.ATTRIBUTE.getLowerCaseKey()), SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.ATTRIBUTE), true, true, true);
  }

  public static ISearchParameter createDeliveryClassParameter(
      final IAbapProjectProvider projectProvider) {
    final NamedItemParameter parameter = new NamedItemParameter(projectProvider,
        QueryParameterName.DELIVERY_CLASS, NamedItemType.TABLE_DELIVERY_CLASS, true, ""); //$NON-NLS-1$
    parameter.setDescription(MessageFormat.format(
        Messages.SearchPatternAnalyzer_DescriptionDeliveryClassParameter_xmsg,
        QueryParameterName.DELIVERY_CLASS.getLowerCaseKey()));
    parameter.setImage(AdtBaseUIResources.getImage(IAdtBaseImages.TRANSPORT));
    parameter.setSupportsNegatedValues(true);
    return parameter;
  }
}
