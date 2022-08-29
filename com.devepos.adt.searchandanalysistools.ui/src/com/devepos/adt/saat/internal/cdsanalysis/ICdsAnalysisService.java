package com.devepos.adt.saat.internal.cdsanalysis;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;

/**
 * Service for analyzing CDS Views
 *
 * @author stockbal
 */
public interface ICdsAnalysisService {

  /**
   * Loads the complete hierarchy of the given CDS View field
   *
   * @param cdsView       name of the owning CDS view of <code>field</code>
   * @param field         name of a field in the given CDS view
   * @param destinationId the destination id of an ABAP project
   * @return the found field hierarchy information
   */
  IElementInfo loadTopDownFieldAnalysis(String cdsView, String field, String destinationId);

  /**
   * Loads the first level of the Where-Used list of the given field
   *
   * @param objectName       name of the owning Database object of
   *                         <code>field</code>
   * @param field            name of a field in the given CDS view
   * @param searchCalcFields if <code>true</code> calculation fields are also
   *                         searched for usages of the given field
   * @param searchDbViews    if <code>true</code> Database Views are also searched
   *                         for usages of the given field
   * @param destinationId    the destination id of an ABAP project
   * @return the found field hierarchy information
   */
  IElementInfo loadWhereUsedFieldAnalysis(final String objectName, final String field,
      ICdsFieldAnalysisSettings settings, final String destinationId);

  /**
   * Loads SELECT Part of a single CDS View
   *
   * @param cdsView       the name of the CDS view for which the SELECT part
   *                      should be loaded
   * @param settings      settings object to configure the top down analysis
   * @param destinationId the destination of ID of the ABAP project
   * @return
   */
  IAdtObjectReferenceElementInfo loadTopDownAnalysis(String cdsView, ICdsTopDownSettings settings,
      String destinationId);

  /**
   * Loads Used entities in the dependency tree of the given CDS view
   *
   * @param cdsView       the name of the CDS view for which the Used Entities
   *                      should be analyzed
   * @param destinationId the destination of ID of the ABAP project
   * @return
   */
  IAdtObjectReferenceElementInfo loadUsedEntitiesAnalysis(String cdsView, String destinationId);

}