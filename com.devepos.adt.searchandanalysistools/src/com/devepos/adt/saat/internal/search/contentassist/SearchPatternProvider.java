package com.devepos.adt.saat.internal.search.contentassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
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
import com.devepos.adt.saat.internal.search.TypeSearchParameter;
import com.devepos.adt.saat.internal.search.UserSearchParameter;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;

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

	public SearchPatternProvider(final IAbapProjectProvider projectProvider, final SearchType searchType) {
		this.patternAnalyzer = new SearchPatternAnalyzer(this);
		this.patternAnalyzer.setIsSearchTermAllowed(true);
		this.searchType = searchType;
		this.projectProvider = projectProvider;
	}

	public void enableSearchTermInput(final boolean enable) {
		this.patternAnalyzer.setIsSearchTermAllowed(enable);
	}

	/**
	 * Adds content assistance to the given field. This provider currently supports
	 * only one field
	 *
	 * @param input
	 */
	public void addContentAssist(final Text input) {
		// TODO: decouple content assist completely from the provider
		if (this.contentAssist != null) {
			this.contentAssist.dispose();
			this.contentAssist = null;
		}
		this.contentAssist = new SearchPatternContentAssist(input, this.patternAnalyzer);
		this.contentAssist.setLabelProvider(new LabelProvider());
	}

	/**
	 * Checks if the current search pattern is complete
	 *
	 * @param  searchPattern the pattern to analyze
	 * @throws CoreException
	 */
	public void checkSearchParametersComplete(final String searchPattern) throws CoreException {
		if (this.patternAnalyzer != null) {
			this.patternAnalyzer.checkParameters(searchPattern);
		}
	}

	@Override
	public List<ISearchParameter> getParameters() {
		if (this.parameterMap == null) {
			this.parameterMap = new HashMap<>();
		}
		List<ISearchParameter> parameters = this.parameterMap.get(this.searchType);
		if (parameters == null) {
			parameters = new ArrayList<>();
			parameters.add(new UserSearchParameter(this.projectProvider));
			parameters.add(new PackageSearchParameter(this.projectProvider));
			parameters.add(SearchParameterFactory.createDescriptionParameter());
			if (this.searchType == SearchType.CDS_VIEW) {
				parameters.add(new ReleaseStateSearchParameter(this.projectProvider));
				parameters.add(new SelectFromSearchParameter(this.projectProvider));
				parameters.add(new AssociationSearchParameter(this.projectProvider));
				parameters.add(new FieldSearchParameter(this.projectProvider, NamedItemType.CDS_FIELD));
				parameters.add(new CdsExtendedBySearchParameter(this.projectProvider));
				parameters.add(new AnnotationSearchParameter(this.projectProvider));
				parameters.add(SearchParameterFactory.createCdsParamParameter());
				parameters.add(SearchParameterFactory.createHasParameterParameter());
				parameters.add(new TypeSearchParameter(this.projectProvider));
			} else if (this.searchType == SearchType.DB_TABLE_VIEW) {
				parameters.add(new FieldSearchParameter(this.projectProvider, NamedItemType.TABLE_FIELD));
			}
			this.parameterMap.put(this.searchType, parameters);
		}

		// filter parameters according to the URI search template
		if (!this.projectProvider.ensureLoggedOn()) {
			return new ArrayList<>();
		}
		final List<QueryParameterName> supportedParameters = new ObjectSearchUriDiscovery(this.projectProvider.getDestinationId())
			.getSupportedSearchParameters();

		return parameters.stream()
			.filter(param -> supportedParameters.contains(param.getParameterName()))
			.collect(Collectors.toList());
	}

	/**
	 * Retrieves a map of all valid parameters in the given search pattern and their
	 * entered values
	 *
	 * @param  pattern
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
			parameterMap.put(param.getParameterName().toString(), getParameterValues(pattern, param.getLabel()));

		}
		return parameterMap;
	}

	/**
	 * Retrieves the search term from the the given <code>searchPattern</code>
	 *
	 * @param  searchPattern the pattern to analyze
	 * @return
	 */
	public String getSearchTerm(final String searchPattern) {
		if (this.patternAnalyzer != null) {
			return this.patternAnalyzer.getSearchTerm(searchPattern);
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
		final int maxRows = SearchAndAnalysisPlugin.getDefault().getPreferenceStore().getInt(IPreferences.MAX_SEARCH_RESULTS);
		final List<String> values = getParameterValues(searchPattern, QueryParameterName.MAX_ROWS.toString());
		if (values == null || values.isEmpty()) {
			return maxRows;
		} else {
			return Integer.parseInt(values.get(0));
		}
	}

	private List<String> getParameterValues(final String pattern, final String paramName) {
		if (this.patternAnalyzer != null) {
			return this.patternAnalyzer.getContent(pattern, paramName);
		}
		return new ArrayList<>();
	}

	/**
	 * Dispose of all allocated resources
	 */
	public void dispose() {
		if (this.contentAssist != null) {
			this.contentAssist.dispose();
		}
	}
}
