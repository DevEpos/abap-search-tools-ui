package com.devepos.adt.saat.internal.search.contentassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.search.AnnotationSearchParameter;
import com.devepos.adt.saat.internal.search.AssociationSearchParameter;
import com.devepos.adt.saat.internal.search.CdsExtendedBySearchParameter;
import com.devepos.adt.saat.internal.search.FieldSearchParameter;
import com.devepos.adt.saat.internal.search.ISearchParameter;
import com.devepos.adt.saat.internal.search.ISearchParameterHandler;
import com.devepos.adt.saat.internal.search.NamedItemType;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.PackageSearchParameter;
import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.devepos.adt.saat.internal.search.ReleaseStateSearchParameter;
import com.devepos.adt.saat.internal.search.SearchParameterFactory;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.search.SelectFromSearchParameter;
import com.devepos.adt.saat.internal.search.UserSearchParameter;

/**
 * Provider for Object Search pattern
 *
 * @author stockbal
 */
public class SearchPatternProvider implements ISearchParameterHandler {
  private SearchPatternContentAssist contentAssist;
  private final SearchPatternAnalyzer patternAnalyzer;
  private IAbapProjectProvider projectProvider;
  private Map<SearchType, List<ISearchParameter>> parameterMap;
  private SearchType searchType;

  public SearchPatternProvider() {
    this((IAbapProjectProvider) null, null);
  }

  public SearchPatternProvider(final IAbapProjectProvider projectProvider,
      final SearchType searchType) {
    patternAnalyzer = new SearchPatternAnalyzer(this);
    patternAnalyzer.setIsSearchTermAllowed(true);
    this.searchType = searchType;
    this.projectProvider = projectProvider;
  }

  public void enableSearchTermInput(final boolean enable) {
    patternAnalyzer.setIsSearchTermAllowed(enable);
  }

  /**
   * Adds content assistance to the given field. This provider currently supports
   * only one field
   *
   * @param input
   */
  public void addContentAssist(final Text input) {
    // TODO: decouple content assist completely from the provider
    if (contentAssist != null) {
      contentAssist.dispose();
      contentAssist = null;
    }
    contentAssist = new SearchPatternContentAssist(input, patternAnalyzer);
    contentAssist.setLabelProvider(new LabelProvider());
  }

  /**
   * Checks if the current search pattern is complete
   *
   * @param searchPattern the pattern to analyze
   * @throws CoreException
   */
  public void checkSearchParametersComplete(final String searchPattern) throws CoreException {
    if (patternAnalyzer != null) {
      patternAnalyzer.checkParameters(searchPattern);
    }
  }

  @Override
  public List<ISearchParameter> getParameters() {
    if (parameterMap == null) {
      parameterMap = new HashMap<>();
    }
    List<ISearchParameter> parameters = parameterMap.get(searchType);
    if (parameters == null) {
      parameters = new ArrayList<>();
      parameters.add(new UserSearchParameter(projectProvider));
      parameters.add(new PackageSearchParameter(projectProvider));
      parameters.add(SearchParameterFactory.createDescriptionParameter());
      if (searchType == SearchType.CDS_VIEW) {
        parameters.add(new ReleaseStateSearchParameter(projectProvider));
        parameters.add(new SelectFromSearchParameter(projectProvider));
        parameters.add(new AssociationSearchParameter(projectProvider));
        parameters.add(new FieldSearchParameter(projectProvider, NamedItemType.CDS_FIELD));
        parameters.add(new CdsExtendedBySearchParameter(projectProvider));
        parameters.add(new AnnotationSearchParameter(projectProvider));
        parameters.add(SearchParameterFactory.createCdsParamParameter());
        parameters.add(SearchParameterFactory.createHasParameterParameter());
        parameters.add(SearchParameterFactory.createCdsTypeParameter(projectProvider));
      } else if (searchType == SearchType.DB_TABLE_VIEW) {
        parameters.add(new FieldSearchParameter(projectProvider, NamedItemType.TABLE_FIELD));
        parameters.add(SearchParameterFactory.createTableTypeParameter(projectProvider));
        parameters.add(SearchParameterFactory.createDeliveryClassParameter(projectProvider));
      } else if (searchType == SearchType.CLASS_INTERFACE) {
        parameters.add(new ReleaseStateSearchParameter(projectProvider));
        parameters.add(SearchParameterFactory.createClassTypeParameter(projectProvider));
        parameters.add(SearchParameterFactory.createClassCategoryParameter(projectProvider));
        parameters.add(SearchParameterFactory.createClassFlagParameter(projectProvider));
        parameters.add(SearchParameterFactory.createAbapLanguageParameter(projectProvider));
        parameters.add(SearchParameterFactory.createFriendParameter());
        parameters.add(SearchParameterFactory.createSuperTypeParameter());
        parameters.add(SearchParameterFactory.createInterfaceParameter());
        parameters.add(SearchParameterFactory.createMethodParameter());
        parameters.add(SearchParameterFactory.createAttributeParameter());
      }
      parameterMap.put(searchType, parameters);
    }

    // filter parameters according to the URI search template
    if (!projectProvider.ensureLoggedOn()) {
      return new ArrayList<>();
    }
    final List<QueryParameterName> supportedParameters = new ObjectSearchUriDiscovery(
        projectProvider.getDestinationId()).getSupportedSearchParameters(searchType);

    return parameters.stream()
        .filter(param -> supportedParameters.contains(param.getParameterName()))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a map of all valid parameters in the given search pattern and their
   * entered values
   *
   * @param pattern
   * @return
   */
  public Map<String, Object> getSearchParameters(final String pattern) {
    final Map<String, Object> parameterMap = new HashMap<>();
    final List<ISearchParameter> parameters = getParameters();
    // special case for max rows params to consider search preference values
    parameterMap.put(QueryParameterName.MAX_ROWS.toString(), getMaxRows(pattern, parameters));
    // collect values of the rest of the parameters
    for (final ISearchParameter param : parameters) {
      if (param.getParameterName() == QueryParameterName.MAX_ROWS) {
        continue;
      }
      parameterMap.put(param.getParameterName().toString(), getParameterValues(pattern, param
          .getLabel()));

    }
    return parameterMap;
  }

  /**
   * Retrieves the search term from the the given <code>searchPattern</code>
   *
   * @param searchPattern the pattern to analyze
   * @return
   */
  public String getSearchTerm(final String searchPattern) {
    if (patternAnalyzer != null) {
      return patternAnalyzer.getSearchTerm(searchPattern);
    }
    return searchPattern;
  }

  @Override
  public void setSearchType(final SearchType searchType) {
    this.searchType = searchType;
  }

  @Override
  public void updateProjectProvider(final IAbapProjectProvider projectProvider) {
    if (this.projectProvider == null) {
      this.projectProvider = projectProvider;
    } else {
      this.projectProvider.setProject(projectProvider.getProject());
    }
  }

  private int getMaxRows(final String searchPattern, final List<ISearchParameter> parameters) {
    final int maxRows = SearchAndAnalysisPlugin.getDefault()
        .getPreferenceStore()
        .getInt(IPreferences.MAX_SEARCH_RESULTS);
    final List<String> values = getParameterValues(searchPattern, QueryParameterName.MAX_ROWS
        .toString());
    if (values == null || values.isEmpty()) {
      return maxRows;
    }
    return Integer.parseInt(values.get(0));
  }

  private List<String> getParameterValues(final String pattern, final String paramName) {
    if (patternAnalyzer != null) {
      return patternAnalyzer.getContent(pattern, paramName);
    }
    return new ArrayList<>();
  }

  /**
   * Dispose of all allocated resources
   */
  public void dispose() {
    if (contentAssist != null) {
      contentAssist.dispose();
    }
  }
}
