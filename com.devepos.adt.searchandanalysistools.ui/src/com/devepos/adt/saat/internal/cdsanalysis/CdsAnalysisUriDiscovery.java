package com.devepos.adt.saat.internal.cdsanalysis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.base.util.UriDiscoveryBase;
import com.devepos.adt.saat.internal.util.IUriDiscoveryPaths;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for CDS Analysis
 *
 * @author stockbal
 */
public class CdsAnalysisUriDiscovery extends UriDiscoveryBase {
  private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/cds/analysis"; //$NON-NLS-1$
  private static final String DISCOVERY_RELATION_CDS_ANALYSIS = "http://www.devepos.com/adt/relations/saat/cds/analysis"; //$NON-NLS-1$
  private static final String DISCOVERY_TEMPLATE_TOP_DOWN = "/topDown"; //$NON-NLS-1$
  private static final String DISCOVERY_TEMPLATE_USED_ENTITES = "/usedEntities"; //$NON-NLS-1$
  private static final String DISCOVERY_TERM_CDS_ANALYSIS = "cdsanalysis"; //$NON-NLS-1$

  public CdsAnalysisUriDiscovery(final String destination) {
    super(destination, IUriDiscoveryPaths.OBJECT_SEARCH_DISOCOVERY_PATH, DISCOVERY_SCHEME);
  }

  /**
   * @return Retrieves Resource URI for the CDS Analysis Resource
   */
  public URI getCdsAnalysisUri() {
    return getUriFromCollectionMember(DISCOVERY_TERM_CDS_ANALYSIS);
  }

  /**
   * Returns <code>true</code> if the CDS Top-Down Analysis is available in the
   * current destination
   *
   * @return <code>true</code> if the CDS Top-Down Analysis is available in the
   *         current destination
   */
  public boolean isTopDownAnalysisAvailable() {
    return getCdsAnalysisTemplate(DISCOVERY_TEMPLATE_TOP_DOWN) != null;
  }

  /**
   * Returns <code>true</code> if the Used Entities Analysis is available in the
   * current destination
   *
   * @return Returns <code>true</code> if the Used Entities Analysis is available
   *         in the current destination
   */
  public boolean isUsedEntitiesAnalysisAvailable() {
    return getCdsAnalysisTemplate(DISCOVERY_TEMPLATE_USED_ENTITES) != null;
  }

  /**
   * @return ADT URI template for the CDS Analysis Resource
   */
  public IAdtUriTemplate getCdsAnalysisTemplate(final String templateUriPart) {
    return getTemplate(DISCOVERY_TERM_CDS_ANALYSIS, DISCOVERY_RELATION_CDS_ANALYSIS
        + templateUriPart);
  }

  /**
   * Creates Resource URI for a Top-Down analysis of the given CDS View
   *
   * @param cdsViewName  name of a CDS view
   * @param parameterMap map of parameters for URI template
   * @return REST resource URI
   */
  public URI createTopDownAnalysisResourceUri(final String cdsViewName,
      final Map<String, Object> parameterMap) {
    return createCdsAnalysisResourceUri(DISCOVERY_TEMPLATE_TOP_DOWN, cdsViewName, parameterMap);
  }

  /**
   * Creates Resource URI for Used Entities Analysis for the given CDS View
   *
   * @param cdsViewName  name of a CDS view
   * @param parameterMap map of parameters for URI template
   * @return REST resource URI
   */
  public URI createUsedEntitiesAnalysisResourceUri(final String cdsViewName,
      Map<String, Object> parameterMap) {
    if (parameterMap == null) {
      parameterMap = new HashMap<>();
    }
    parameterMap.put("usageAnalysis", "X"); //$NON-NLS-1$ //$NON-NLS-2$
    return createCdsAnalysisResourceUri(DISCOVERY_TEMPLATE_USED_ENTITES, cdsViewName, parameterMap);
  }

  /*
   * Creates a valid REST resource URI to perform a CDS analysis for the given CDS
   * View
   */
  private URI createCdsAnalysisResourceUri(final String templateUriPart, final String cdsViewName,
      final Map<String, Object> parameterMap) {
    final IAdtUriTemplate template = getCdsAnalysisTemplate(templateUriPart);
    URI uri = null;
    if (template != null) {
      if (template.containsVariable("cdsViewName")) { //$NON-NLS-1$
        template.set("cdsViewName", cdsViewName); //$NON-NLS-1$
      }
      fillTemplateWithParams(template, parameterMap);
      uri = URI.create(template.expand());
    }
    return uri;
  }
}
