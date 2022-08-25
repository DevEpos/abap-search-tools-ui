package com.devepos.adt.saat.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.util.StringUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;

/**
 * Proposal provider which used the Named item service for data retrieval. The
 * type of named item can be controlled by passing the requested
 * {@link NamedItemType} to the constructor
 *
 * @author stockbal
 */
public class NamedItemProposalProvider {
  protected final IAbapProjectProvider projectProvider;
  private INamedItemService namedItemService;
  protected String destinationId;
  protected final QueryParameterName parameterName;
  protected final NamedItemType namedItemType;
  protected final boolean isCachingActive;
  private Map<String, INamedItem[]> cache;

  /**
   * Create instance of the named item proposal provider
   *
   * @param projectProvider provides project/destination for named item queries
   * @param parameterName   the parameter name to be used for the found named
   *                        items
   * @param namedItemType   the type of named item to look for
   */
  public NamedItemProposalProvider(final IAbapProjectProvider projectProvider,
      final QueryParameterName parameterName, final NamedItemType namedItemType) {
    this(projectProvider, parameterName, namedItemType, false);
  }

  /**
   * Create instance of the named item proposal provider
   *
   * @param projectProvider provides project/destination for named item queries
   * @param parameterName   the parameter name to be used for the found named
   *                        items
   * @param namedItemType   the type of named item to look for
   * @param cacheResults    use <code>true</code> if you want the results to be
   *                        cached. Only sensible if the result set is expected to
   *                        be very small
   */
  public NamedItemProposalProvider(final IAbapProjectProvider projectProvider,
      final QueryParameterName parameterName, final NamedItemType namedItemType,
      final boolean cacheResults) {
    this.projectProvider = projectProvider;
    this.parameterName = parameterName;
    this.namedItemType = namedItemType;
    isCachingActive = cacheResults;
  }

  /**
   * Gets a list of named items which match the given query string. If the query
   * string is empty a maximum of 50 named items will be retrieved
   *
   * @param query optional query to restrict results
   * @return
   * @throws CoreException
   */
  protected List<IContentProposal> getProposals(final String query) throws CoreException {
    return getProposals(query, query, null);
  }

  /**
   * Gets a list of named items which match the given query string. If the query
   * string is empty a maximum of 50 named items will be retrieved. If caching was
   * enabled via the <code>cacheResults</code> in the Constructor of the Provider,
   * the parameter <code>cacheQuery</code> can be used to restrict the result list
   * further. <br>
   * <strong>Note</strong>: This is only sensible if <code>query</code> parameter
   * was supplied with a different value.
   *
   * @param query      optional query to restrict results
   * @param cacheQuery query to further restrict the result returned from the
   *                   cache
   * @return
   * @throws CoreException
   */
  protected List<IContentProposal> getProposals(final String query, final String cacheQuery)
      throws CoreException {
    return getProposals(query, cacheQuery, null);
  }

  /**
   * Gets a list of named items which match the given query string. If the query
   * string is empty a maximum of 50 named items will be retrieved. If caching was
   * enabled via the <code>cacheResults</code> in the Constructor of the Provider,
   * the parameter <code>cacheQuery</code> can be used to restrict the result list
   * further. <br>
   * <strong>Note</strong>: This is only sensible if <code>query</code> parameter
   * was supplied with a different value.
   *
   * @param query      optional query to restrict results
   * @param cacheQuery query to further restrict the result returned from the
   *                   cache
   * @param dataFilter optional filter to restrict results to NamedItems which a
   *                   certain <code>data</code> value
   * @return
   * @throws CoreException
   */
  protected List<IContentProposal> getProposals(final String query, final String cacheQuery,
      final String dataFilter) throws CoreException {
    final List<IContentProposal> result = new ArrayList<>();

    try {
      if (getNamedItemService()) {
        final INamedItem[] namedItems = getNamedItemsFromCache(query, cacheQuery, dataFilter);
        if (namedItems != null) {
          for (final INamedItem item : namedItems) {
            result.add(createProposalFromNamedItem(item, cacheQuery));
          }
        }
      }
    } catch (final OperationCanceledException ex) {
    } catch (final Exception e) {
      final IStatus status = new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, e
          .getMessage());
      throw new CoreException(status);
    }

    return result;
  }

  /**
   * Creates new content proposal from the given named item
   *
   * @param item
   * @param query
   * @return
   */
  protected IContentProposal createProposalFromNamedItem(final INamedItem item,
      final String query) {
    return new SearchParameterProposal(item.getName(), parameterName, item.getDescription(), query);
  }

  /**
   * Filters the given named item. If it should be included in the final result
   * the value <code>true</code> should be returned. The default implementation
   * considers only the <em>Name</em> attribute in the comparison. Clients should
   * override this method, if a more comprehensive filtering is needed
   *
   * @param namedItem the named item to be included/excluded
   * @return <code>true</code> if the named item should be included in the result
   */
  protected boolean filterResult(final INamedItem namedItem, final String query) {
    if (query == null || query.isEmpty()) {
      return true;
    }
    return StringUtil.getPatternForQuery(query).matcher(namedItem.getName()).matches();
  }

  /**
   * Retrieve named item service for current project destination
   */
  private boolean getNamedItemService() {
    final String currentDestinationId = projectProvider.getDestinationId();
    if (destinationId == null || destinationId != currentDestinationId) {
      if (!projectProvider.ensureLoggedOn()) {
        destinationId = null;
        namedItemService = null;
        return false;
      }
      destinationId = currentDestinationId;
      namedItemService = NamedItemServiceFactory.createService(destinationId);
      // cache is only relevant for one condition at the moment
      if (cache != null) {
        cache.clear();
      }
      cache = null;
    }
    return namedItemService != null;
  }

  /**
   * Reads a list of named items for the given query from cache. If there are not
   * yet in the cache, retrieval from server will be attempted
   *
   * @param query         query to be used for data retrieval
   * @param cacheQuery    the query string to be used to further filter the found
   *                      results from the cache
   * @param namedItemType named item type to be used for data retrieval
   * @return
   */
  private INamedItem[] getNamedItemsFromCache(final String query, final String cacheQuery,
      final String dataFilter) {
    if (!isCachingActive) {
      return namedItemService.getNamedItems(namedItemType, 50, query + "*", null, dataFilter);
    }
    INamedItem[] namedItems = null;
    if (cache == null) {
      cache = new HashMap<>();
    }
    if (cache.containsKey(query)) {
      namedItems = cache.get(query);
    } else {
      namedItems = namedItemService.getNamedItems(namedItemType, 50, query + "*", null, dataFilter);
      cache.put(query, namedItems);
    }
    // filter results from cache before returning them
    return namedItems != null ? Stream.of(namedItems)
        .filter(namedItem -> filterResult(namedItem, cacheQuery))
        .toArray(INamedItem[]::new) : null;
  }

}
