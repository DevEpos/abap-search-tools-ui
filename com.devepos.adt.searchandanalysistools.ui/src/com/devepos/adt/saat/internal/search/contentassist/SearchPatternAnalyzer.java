package com.devepos.adt.saat.internal.search.contentassist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;

import com.devepos.adt.base.util.StringUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ISearchParameter;
import com.devepos.adt.saat.internal.search.ISearchParameterHandler;
import com.devepos.adt.saat.internal.search.ISearchProposalProvider;
import com.devepos.adt.saat.internal.search.IValidatable;

/**
 * Analyzer for search pattern which retrieves
 *
 * @author stockbal
 */
public class SearchPatternAnalyzer {
  private static final String PARAM_KEY_END = ":"; //$NON-NLS-1$
  private static final String KEY_VALUE_SEP = "="; //$NON-NLS-1$
  private static final String VALUE_LIST_SEP = ","; //$NON-NLS-1$
  private static final String ANY_WHITESPACE = "\\s*"; //$NON-NLS-1$
  private static final String SPACE = " "; //$NON-NLS-1$
  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String ANY_VALUE_CHAR = "*"; //$NON-NLS-1$
  private static final String SOME_VALUE_CHAR = "+"; //$NON-NLS-1$
  private final ISearchParameterHandler parameterHandler;
  private List<ISearchParameter> parameters;
  private boolean isSearchTermAllowed = true;

  /**
   * Creates new search pattern analyzer instance
   *
   * @param parameterHandler handler for retrieving search parameters
   */
  public SearchPatternAnalyzer(final ISearchParameterHandler parameterHandler) {
    this.parameterHandler = parameterHandler;
  }

  /**
   * Enables/Disables the occurrence of a none parameter inside the search pattern
   *
   * @param allowSearchTermInPattern
   */
  public void setIsSearchTermAllowed(final boolean allowSearchTermInPattern) {
    isSearchTermAllowed = allowSearchTermInPattern;
  }

  /**
   * Check the parameters together with their values in the supplied
   * <code>searchPattern</code>
   *
   * @param searchPattern the pattern to analyze
   * @throws CoreException
   */
  public void checkParameters(final String searchPattern) throws CoreException {
    updateSearchParameters();
    final String condensedPattern = condense(searchPattern);
    final String searchTerm = getSearchTerm(searchPattern);

    if (!isSearchTermAllowed && !searchTerm.isEmpty()) {
      final String errorMessage = NLS.bind(
          Messages.SearchPatternAnalyzer_ErrorInvalidSearchParameter_xmsg, searchTerm);
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
          errorMessage));
    }

    // find the first invalid part in the search pattern
    for (final String part : condensedPattern.split(SPACE)) {
      if (!part.isEmpty() && !isParameter(part) && !part.equalsIgnoreCase(searchTerm)) {
        final String errorMessage = NLS.bind(
            Messages.SearchPatternAnalyzer_ErrorInvalidSearchParameter_xmsg, part);
        throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
            errorMessage));
      }
    }
  }

  /**
   * Get content (list of values) of a certain parameter out of the given search
   * pattern
   *
   * @param searchPattern the search pattern to analyze
   * @param parameterKey  parameter key which should be looked for in the
   *                      <code>searchPattern</code>
   * @return a list of parameter values
   */
  public List<String> getContent(final String searchPattern, final String parameterKey) {
    final List<String> result = new ArrayList<>();
    updateSearchParameters();
    final String pattern = condense(searchPattern);
    final String parameterKeyLower = parameterKey.toLowerCase(Locale.ENGLISH);
    final List<String> paramSections = Stream.of(pattern.split(SPACE))
        .filter(p -> p.startsWith(parameterKeyLower + PARAM_KEY_END))
        .collect(Collectors.toList());
    if (paramSections == null || paramSections.isEmpty()) {
      return result;
    }
    for (final String paramSection : paramSections) {
      result.addAll(Stream.of(paramSection.substring(parameterKeyLower.length() + 1)
          .split(VALUE_LIST_SEP)).filter(value -> !value.isEmpty()).collect(Collectors.toList()));
    }
    return result;
  }

  /**
   * Get proposal list of all relevant query filters. A filter is relevant for the
   * proposal list if the query part is empty or the filter parameter starts with
   * the same string as the current content assist query
   *
   * @param query
   * @return
   */
  public List<IContentProposal> getFilters(final String query) {
    final List<IContentProposal> filters = new ArrayList<>();
    String lastPart = getStringToAnalyse(query);
    lastPart = lastPart.substring(0, lastPart.length() - 1);
    for (final ISearchParameter searchParam : parameters) {
      // no query part so every parameter gets added to the proposal list
      if (lastPart.isEmpty()) {
        filters.add(new SearchFilterProposal(searchParam.getLabel(), searchParam.getImage(),
            searchParam.getDescription(), searchParam instanceof ISearchProposalProvider));
      } else {
        if (searchParam.getLabel().startsWith(lastPart)) {
          filters.add(new SearchFilterProposal(searchParam.getLabel(), searchParam.getImage(),
              searchParam.getDescription(), lastPart,
              searchParam instanceof ISearchProposalProvider));
        }

      }
    }
    return filters;
  }

  /**
   * Get a list of parameter proposals from the given query string
   *
   * @param query the query String to analyze
   * @return List of parameter proposals
   * @throws CoreException
   */
  public List<IContentProposal> getParameterProposals(final String query) throws CoreException {
    List<IContentProposal> proposals = new ArrayList<>();
    final String lastPart = getStringToAnalyse(query);
    for (final ISearchParameter searchParam : parameters) {
      if (!(searchParam instanceof ISearchProposalProvider)) {
        continue;
      }
      final String paramStart = searchParam.getLabel() + PARAM_KEY_END;
      if (!lastPart.toLowerCase().startsWith(paramStart)) {
        continue;
      }
      final String queryToParse = lastPart.substring(paramStart.length());
      final String[] queryParts = queryToParse.split(VALUE_LIST_SEP);
      String currentQuery = queryParts[queryParts.length - 1];
      currentQuery = currentQuery.substring(0, currentQuery.length() - 1);
      // if the parameter supports negated values the the negation char should be
      // excluded during the determination of relevant proposals
      currentQuery = removeNegation(searchParam, currentQuery);
      proposals = ((ISearchProposalProvider) searchParam).getProposalList(currentQuery);
    }
    return proposals;
  }

  /**
   * Retrieve proposals for the given query
   *
   * @param query the query String to be analyzed
   * @return the determined proposals
   * @throws CoreException
   */
  public List<IContentProposal> getProposals(final String query) throws CoreException {
    List<IContentProposal> proposals = null;
    updateSearchParameters();
    // determine if the query to analyze is a parameter
    if (isParameterProposal(query)) {
      proposals = getParameterProposals(query);
    } else {
      proposals = getFilters(query);
    }
    return proposals;
  }

  /**
   * Retrieve the search term out of the current pattern
   *
   * @param searchPattern the current search pattern to analyze
   * @return
   */
  public String getSearchTerm(final String searchPattern) {
    updateSearchParameters();
    /*
     * exclude empty parts and parts that contain ":" characters as those will be
     * parameters
     */
    return Stream.of(condense(searchPattern).split(SPACE))
        .filter(p -> !p.isEmpty() && !p.contains(PARAM_KEY_END))
        .findFirst()
        .orElse(EMPTY);
  }

  private boolean checkParameterValuesProvided(final String part, final ISearchParameter param)
      throws CoreException {
    // Error -> only parameter key + ":" supplied
    if (part.length() <= (param.getLabel() + PARAM_KEY_END).length()) {
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.SearchPatternAnalyzer_ErrorIncompleteSearchParameter_xmsg, part)));
    }

    final String paramValuesString = part.substring(param.getLabel().length() + 1);
    // Error -> no parameter values supplied
    if (paramValuesString.isEmpty()) {
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.SearchPatternAnalyzer_ErrorIncompleteSearchParameter_xmsg, part)));
    }

    final String[] paramValues = paramValuesString.split(VALUE_LIST_SEP);

    // perform checks on the given parameter values
    // 1) check if multiple values are supported
    // 2) check if parameter has custom validation implemented for values
    // 3) check if value is one of the buffered values
    // 4) check if wildcard values are allowed
    // 5) check if negation is allowed
    if (!param.supportsMultipleValues() && paramValues.length > 1) {
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.SearchPatternAnalyzer_ErrorParamAllowsOnlySingleValues_xmsg, param.getLabel())));
    }
    if (param instanceof IValidatable) {
      for (final String value : paramValues) {
        ((IValidatable) param).validate(value);
      }
    }
    if (param.isBuffered() && param instanceof ISearchProposalProvider) {
      for (String value : paramValues) {
        value = removeNegation(param, value);
        final List<IContentProposal> proposalList = ((ISearchProposalProvider) param)
            .getProposalList(value);
        if (!isValueInProposalList(proposalList, value)) {
          throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS
              .bind(Messages.SearchPatternAnalyzer_ErrorUnsupportedParamValue_xmsg, param
                  .getLabel(), value)));
        }
      }
    }
    if (!param.supportsPatternValues() && Stream.of(paramValues)
        .anyMatch(value -> value.contains(ANY_VALUE_CHAR) || value.contains(SOME_VALUE_CHAR))) {
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.SearchPatternAnalyzer_ErrorWildcardsNotSupportedInParam_xmsg, param
              .getLabel())));
    }
    if (!param.supportsNegatedValues() && Stream.of(paramValues)
        .anyMatch(StringUtil::startsWithNegationCharacter)) {
      throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
          MessageFormat.format("Parameter ''{0}'' does not permit negation", param.getLabel())));
    }

    return true;
  }

  private boolean isParameter(final String part) throws CoreException {
    if (parameters != null) {
      for (final ISearchParameter param : parameters) {
        if (part.startsWith(param.getLabel() + PARAM_KEY_END) && checkParameterValuesProvided(part,
            param)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isParameterProposal(final String query) {
    final String lastPart = getStringToAnalyse(query);
    for (final ISearchParameter parameter : parameters) {
      if (lastPart.toLowerCase().startsWith(parameter.getLabel() + PARAM_KEY_END)) {
        return true;
      }
    }
    return false;
  }

  private boolean isValueInProposalList(final List<IContentProposal> proposalList,
      final String value) {
    return proposalList.stream().anyMatch(proposal -> proposal.getLabel().equalsIgnoreCase(value));
  }

  private void updateSearchParameters() {
    if (parameterHandler == null) {
      parameters = new ArrayList<>();
      return;
    }
    parameters = parameterHandler.getParameters();
  }

  /*
   * Remove unnecessary spaces around parameter end ":" and parameter list
   * separator "," and parameter key/value separator "="
   */
  private static String condense(final String content) {
    return condense(condense(condense(content, PARAM_KEY_END), VALUE_LIST_SEP), KEY_VALUE_SEP);
  }

  private static String condense(String content, final String by) {
    final String regex = ANY_WHITESPACE + by + "{1}" + ANY_WHITESPACE; //$NON-NLS-1$
    return content = content.replaceAll(regex, by);
  }

  /*
   * Retrieves the actual part of the query that should be used to determine the
   * proposals
   */
  private static String getStringToAnalyse(final String contents) {
    final String term = condense(contents) + "%"; //$NON-NLS-1$
    final String[] splits = term.split(SPACE);
    String lastPart = splits[splits.length - 1];
    lastPart = lastPart.trim();
    return lastPart;
  }

  /*
   * Removes any negation characters if the parameter supports this
   */
  private static String removeNegation(final ISearchParameter param, final String value) {
    if (param.supportsNegatedValues()) {
      return StringUtil.removeNegationCharacter(value);
    }
    return value;
  }

}
