package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;

/**
 * Registry which is used to find a specific page for the CDS Analyzer
 *
 * @author stockbal
 */
public class CdsAnalysisConfigRegistry {

	private final Map<CdsAnalysisType, CdsAnalysisPage<?>> pageMap;
	private final CdsAnalysisView viewPart;

	public CdsAnalysisConfigRegistry(final CdsAnalysisView viewPart) {
		this.pageMap = new HashMap<>();
		this.viewPart = viewPart;
	}

	/**
	 * Returns the analysis page for the given analysis type
	 * 
	 * @param  type the type for which the analysis page should be returned
	 * @return      the found page
	 */
	public CdsAnalysisPage<?> findPageForType(final CdsAnalysisType type) {
		CdsAnalysisPage<?> page = this.pageMap.get(type);
		if (page == null) {
			page = createPage(type);
			Assert.isNotNull(page);
			this.pageMap.put(type, page);
		}
		return page;
	}

	private CdsAnalysisPage<?> createPage(final CdsAnalysisType type) {
		switch (type) {
		case DEPENDENCY_TREE_USAGES:
			return new CdsUsageAnalysisView(this.viewPart);
		case FIELD_ANALYSIS:
			return new FieldAnalysisView(this.viewPart);
		case TOP_DOWN:
			return new CdsTopDownAnalysisView(this.viewPart);
		case WHERE_USED:
			return new WhereUsedInCdsAnalysisView(this.viewPart);
		default:
			return null;
		}
	}
}
