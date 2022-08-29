package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoProvider;
import com.devepos.adt.base.elementinfo.LazyLoadingElementInfo;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.ObjectContainer;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchQuery;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchRequest;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchResult;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Provider for reading usages of a given ADT object in Select/Association
 * clauses of CDS views
 *
 * @author stockbal
 */
public class WhereUsedInCdsElementInfoProvider implements IElementInfoProvider {
  private final String destinationId;
  private final String adtObjectName;
  private QueryParameterName searchParameter;
  private IWhereUsedInCdsSettings settings;

  /**
   * Creates a new Where Used in CDS view Element info provider
   *
   * @param destinationId      the id of the backend destination
   * @param adtObjectName      the name of the ADT object (can be Database table,
   *                           Database view or a CDS view)
   * @param searchSelectFrom   if <code>true</code> the query searches for uses of
   *                           <code>cdsViewName</code> in Select parts of CDS
   *                           views
   * @param searchAssociations if <code>true</code> the query searches for uses of
   *                           <code>cdsViewName</code> in Associations of CDS
   *                           views
   */
  public WhereUsedInCdsElementInfoProvider(final String destinationId, final String adtObjectName,
      IWhereUsedInCdsSettings settings) {
    this(destinationId, adtObjectName, settings, null);
  }

  /**
   * Creates a new Where Used in CDS view Element info provider
   *
   * @param destinationId      the id of the backend destination
   * @param adtObjectName      the name of the ADT object (can be Database table,
   *                           Database view or a CDS view)
   * @param searchSelectFrom   if <code>true</code> the query searches for uses of
   *                           <code>cdsViewName</code> in Select parts of CDS
   *                           views
   * @param searchAssociations if <code>true</code> the query searches for uses of
   *                           <code>cdsViewName</code> in Associations of CDS
   *                           views
   * @param searchParameter    the concrete parameter value for a where used
   *                           search
   */
  private WhereUsedInCdsElementInfoProvider(final String destinationId, final String adtObjectName,
      IWhereUsedInCdsSettings settings, final QueryParameterName searchParameter) {
    Assert.isNotNull(settings);
    this.destinationId = destinationId;
    this.adtObjectName = adtObjectName;
    this.settings = settings;
    updateSearchParameters(searchParameter);
  }

  @Override
  public List<IElementInfo> getElements() {
    final ObjectContainer<List<IElementInfo>> elementInfoWrapper = new ObjectContainer<>(
        new ArrayList<IElementInfo>());
    if (settings.isSearchAssociations() && settings.isSearchFromPart() && searchParameter == null) {
      return Arrays.asList(createLazyWhereUsedProviderElement(true),
          createLazyWhereUsedProviderElement(false));
    }
    final ObjectSearchRequest searchRequest = new ObjectSearchRequest();
    searchRequest.setProjectProvider(AbapProjectProviderAccessor.getProviderForDestination(
        destinationId));
    final Map<String, Object> parameters = new HashMap<>();
    parameters.put(searchParameter.toString(), adtObjectName);
    if (settings.isLocalAssociationsOnly()) {
      parameters.put(QueryParameterName.LOCAL_DECLARED_ASSOC_ONLY.toString(), "X"); //$NON-NLS-1$
    }
    if (settings.isReleasedUsagesOnly()) {
      parameters.put(QueryParameterName.RELEASE_STATE.toString(), "RELEASED");
    }
    searchRequest.setParameters(parameters, null);
    searchRequest.setReadAllEntries(true);
    searchRequest.setReadApiState(true);
    final ObjectSearchQuery searchQuery = new ObjectSearchQuery(searchRequest);
    final IStatus queryRunStatus = searchQuery.run(new NullProgressMonitor());
    if (queryRunStatus.isOK()) {
      final List<IAdtObjectReferenceElementInfo> result = ((ObjectSearchResult) searchQuery
          .getSearchResult()).getResult();
      for (final IAdtObjectReferenceElementInfo elementInfo : result) {
        final WhereUsedInCdsElementInfoProvider elemInfoProvider = new WhereUsedInCdsElementInfoProvider(
            destinationId, elementInfo.getName(), settings);
        elementInfo.setElementInfoProvider(elemInfoProvider);
        elementInfoWrapper.getObject().add(elementInfo);
      }
    }

    return elementInfoWrapper.getObject();
  }

  @Override
  public String getProviderDescription() {
    return NLS.bind(Messages.CdsAnalysis_WhereUsedProviderDescription_xmsg, adtObjectName);
  }

  /**
   * Updates the search parameters for the Where-Used provider
   */
  public void updateSearchParameters() {
    updateSearchParameters(null);
  }

  private IElementInfo createLazyWhereUsedProviderElement(final boolean searchFrom) {
    String imageId = null;
    String name = null;
    if (searchFrom) {
      imageId = IImages.DATA_SOURCE;
      name = Messages.CdsAnalysis_UsesInSelectTreeNode_xfld;
    } else {
      imageId = IImages.ASSOCIATION;
      name = Messages.CdsAnalysis_UsesInAssociationsTreeNode_xlfd;
    }
    final WhereUsedInCdsElementInfoProvider provider = new WhereUsedInCdsElementInfoProvider(
        destinationId, adtObjectName, settings, searchFrom ? QueryParameterName.SELECT_SOURCE_IN
            : QueryParameterName.ASSOCIATED_IN);
    return new LazyLoadingElementInfo(name, name, SearchAndAnalysisPlugin.getDefault()
        .getImage(imageId), provider);
  }

  /**
   * Updates the search parameters for the Where-Used provider
   *
   * @param searchParameter the concrete parameter value for a where used
   *                        search
   */
  private void updateSearchParameters(final QueryParameterName searchParameter) {
    Assert.isTrue(settings.isSearchAssociations() || settings.isSearchFromPart());
    if (searchParameter == null) {
      if (!settings.isSearchAssociations() || !settings.isSearchFromPart()) {
        if (settings.isSearchFromPart()) {
          this.searchParameter = QueryParameterName.SELECT_SOURCE_IN;
        } else {
          this.searchParameter = QueryParameterName.ASSOCIATED_IN;
        }
      } else {
        this.searchParameter = null;
      }
    } else {
      this.searchParameter = searchParameter;
    }
  }

}
