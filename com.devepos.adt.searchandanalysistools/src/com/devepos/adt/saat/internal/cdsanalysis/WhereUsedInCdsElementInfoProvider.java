package com.devepos.adt.saat.internal.cdsanalysis;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.LazyLoadingElementInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchQuery;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchRequest;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchResult;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.ObjectContainer;

/**
 * Provider for reading usages of a given ADT object in Select/Association
 * clauses of CDS views
 *
 * @author stockbal
 */
public class WhereUsedInCdsElementInfoProvider implements IElementInfoProvider {
	private final String destinationId;
	private final String adtObjectName;
	private boolean searchSelectFrom;
	private boolean searchAssocications;
	private QueryParameterName searchParameter;

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
		final boolean searchSelectFrom, final boolean searchAssociations, final QueryParameterName searchParameter) {
		this.destinationId = destinationId;
		this.adtObjectName = adtObjectName;
		updateSearchParameters(searchSelectFrom, searchAssociations, searchParameter);
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
	 */
	public WhereUsedInCdsElementInfoProvider(final String destinationId, final String adtObjectName,
		final boolean searchSelectFrom, final boolean searchAssociations) {
		this(destinationId, adtObjectName, searchSelectFrom, searchAssociations, null);
	}

	/**
	 * Updates the search parameters for the Where-Used provider
	 *
	 * @param searchSelectFrom   if <code>true</code> the query searches for uses of
	 *                           <code>cdsViewName</code> in Select parts of CDS
	 *                           views
	 * @param searchAssociations if <code>true</code> the query searches for uses of
	 *                           <code>cdsViewName</code> in Associations of CDS
	 *                           views
	 */
	public void updateSearchParameters(final boolean searchSelectFrom, final boolean searchAssociations) {
		updateSearchParameters(searchSelectFrom, searchAssociations, null);
	}

	@Override
	public List<IElementInfo> getElements() {
		final ObjectContainer<List<IElementInfo>> elementInfoWrapper = new ObjectContainer<>(new ArrayList<IElementInfo>());
		if (this.searchAssocications && this.searchSelectFrom) {
			if (this.searchParameter == null) {
				return Arrays.asList(createLazyWhereUsedProviderElement(true), createLazyWhereUsedProviderElement(false));
			}
		}
		final ObjectSearchRequest searchRequest = new ObjectSearchRequest();
		searchRequest.setProjectProvider(AbapProjectProviderAccessor.getProviderForDestination(this.destinationId));
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put(this.searchParameter.toString(), this.adtObjectName);
		searchRequest.setParameters(parameters, null);
		searchRequest.setReadAllEntries(true);
		searchRequest.setReadApiState(true);
		final ObjectSearchQuery searchQuery = new ObjectSearchQuery(searchRequest);
		final IStatus queryRunStatus = searchQuery.run(new NullProgressMonitor());
		if (queryRunStatus.isOK()) {
			final List<IAdtObjectReferenceElementInfo> result = ((ObjectSearchResult) searchQuery.getSearchResult()).getResult();
			for (final IAdtObjectReferenceElementInfo elementInfo : result) {
				elementInfo.setElementInfoProvider(new WhereUsedInCdsElementInfoProvider(this.destinationId,
					elementInfo.getName(), this.searchSelectFrom, this.searchAssocications));
				elementInfoWrapper.getObject().add(elementInfo);
			}
		}

		return elementInfoWrapper.getObject();
	}

	@Override
	public String getProviderDescription() {
		return NLS.bind(Messages.CdsAnalysis_WhereUsedProviderDescription_xmsg, this.adtObjectName);
	}

	/**
	 * Updates the search parameters for the Where-Used provider
	 *
	 * @param searchSelectFrom   if <code>true</code> the query searches for uses of
	 *                           <code>cdsViewName</code> in Select parts of CDS
	 *                           views
	 * @param searchAssociations if <code>true</code> the query searches for uses of
	 *                           <code>cdsViewName</code> in Associations of CDS
	 *                           views
	 * @param searchParameter    the concrete parameter value for a where used
	 *                           search
	 */
	private void updateSearchParameters(final boolean searchSelectFrom, final boolean searchAssociations,
		final QueryParameterName searchParameter) {
		assertTrue(searchSelectFrom || searchAssociations);
		this.searchSelectFrom = searchSelectFrom;
		this.searchAssocications = searchAssociations;
		if (searchParameter == null) {
			if (!(searchSelectFrom && searchAssociations)) {
				if (searchSelectFrom) {
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
		return new LazyLoadingElementInfo(name, name, imageId,
			new WhereUsedInCdsElementInfoProvider(this.destinationId, this.adtObjectName, this.searchSelectFrom,
				this.searchAssocications, searchFrom ? QueryParameterName.SELECT_SOURCE_IN : QueryParameterName.ASSOCIATED_IN));
	}

}
