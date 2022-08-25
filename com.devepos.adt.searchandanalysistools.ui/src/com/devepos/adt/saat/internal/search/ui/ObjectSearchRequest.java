package com.devepos.adt.saat.internal.search.ui;

import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.search.SearchType;
import com.sap.adt.destinations.model.IDestinationData;

/**
 * Search request for an Object Search. <br>
 * Holds all the data used in the object search query
 *
 * @author stockbal
 */
public class ObjectSearchRequest {
  private final String query;
  private String searchTerm;
  private String destinationId;
  private boolean andSearchActive;
  private SearchType searchType;
  private boolean readApiState;
  private boolean readAllEntries;
  private String parametersString;
  private Map<String, Object> parameters;
  private int maxResults;
  private IAbapProjectProvider projectProvider;
  private boolean readPackageHierarchy;

  public ObjectSearchRequest() {
    query = null;
    searchType = SearchType.CDS_VIEW;
    destinationId = null;
    readApiState = false;
    readAllEntries = false;
  }

  @Override
  public boolean equals(final Object object) {
    if (!(object instanceof ObjectSearchRequest)) {
      return super.equals(object);
    }
    final ObjectSearchRequest otherEntry = (ObjectSearchRequest) object;
    return query.equalsIgnoreCase(otherEntry.getQuery()) && destinationId.equalsIgnoreCase(
        otherEntry.getDestinationId()) && andSearchActive == otherEntry.isAndSearchActive();
  }

  @Override
  public String toString() {
    final String destinationInfo = getDestinationInfo();
    if (destinationInfo.isEmpty()) {
      return String.format("'%s' (%s)", getQuery(), getSearchType());
    }
    return String.format("'%s' (%s) [%s]", getQuery(), getSearchType(), destinationInfo);
  }

  public String getQuery() {
    String query = searchTerm == null || searchTerm.isEmpty() ? "" : searchTerm + " ";
    query += parametersString != null && !parametersString.isEmpty() ? parametersString : "";
    return query;
  }

  public String getDestinationId() {
    return projectProvider != null ? projectProvider.getDestinationId()
        : destinationId != null ? destinationId : "";
  }

  public boolean isAndSearchActive() {
    return andSearchActive;
  }

  public SearchType getSearchType() {
    return searchType;
  }

  public boolean shouldReadApiState() {
    return readApiState;
  }

  public boolean shouldReadAllEntries() {
    return readAllEntries;
  }

  public void setSearchType(final SearchType searchType) {
    this.searchType = searchType;
  }

  public void setDestinationId(final String destinationId) {
    this.destinationId = destinationId;
  }

  public void setAndSearchActive(final boolean andSearchActive) {
    this.andSearchActive = andSearchActive;
  }

  public void setReadApiState(final boolean readApiState) {
    this.readApiState = readApiState;
  }

  public void setReadAllEntries(final boolean readAllEntries) {
    this.readAllEntries = readAllEntries;
  }

  /**
   * @return the readPackageHierarchy
   */
  public boolean isReadPackageHierarchy() {
    return readPackageHierarchy;
  }

  /**
   * @param readPackageHierarchy the readPackageHierarchy to set
   */
  public void setReadPackageHierarchy(final boolean readPackageHierarchy) {
    this.readPackageHierarchy = readPackageHierarchy;
  }

  public Map<String, Object> getParameters() {
    return parameters != null ? parameters : new HashMap<>();
  }

  public void setParameters(final Map<String, Object> parameters, final String parametersString) {
    this.parameters = parameters;
    this.parametersString = parametersString;
  }

  public void setMaxResults(final int maxResults) {
    this.maxResults = maxResults;
  }

  public int getMaxResults() {
    return maxResults;
  }

  public String getSearchTerm() {
    return searchTerm != null ? searchTerm : "";
  }

  public void setSearchTerm(final String searchTerm) {
    this.searchTerm = searchTerm;
  }

  public void setProjectProvider(final IAbapProjectProvider projectProvider) {
    this.projectProvider = projectProvider;
  }

  public IAbapProjectProvider getProjectProvider() {
    return projectProvider;
  }

  public String getParametersString() {
    return parametersString != null ? parametersString : "";
  }

  private String getDestinationInfo() {
    if (projectProvider == null || !projectProvider.hasProject()) {
      return "";
    }
    final IDestinationData destData = projectProvider.getDestinationData();
    return String.format("%s-%s", destData.getSystemConfiguration().getSystemId(), destData
        .getClient());
  }
}
