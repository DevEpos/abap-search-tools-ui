package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.tools.base.elementinfo.IElementInfo;
import com.devepos.adt.tools.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.tools.base.elementinfo.IElementInfoProvider;

/**
 * Element info provider which reads a Where-Used list for a given database
 * entity field
 *
 * @author stockbal
 */
public class FieldWhereUsedInCdsElementInfoProvider implements IElementInfoProvider {

	private final String objectName;
	private final String field;
	private final String destinationId;
	private boolean searchCalcFields;
	private boolean searchDbViews;

	public FieldWhereUsedInCdsElementInfoProvider(final String destinationId, final String objectName, final String field) {
		this(destinationId, objectName, field, false, false);
	}

	public FieldWhereUsedInCdsElementInfoProvider(final String destinationId, final String objectName, final String field,
		final boolean searchCalcFields, final boolean searchDbViews) {
		this.objectName = objectName;
		this.field = field;
		this.destinationId = destinationId;
		this.searchCalcFields = searchCalcFields;
		this.searchDbViews = searchDbViews;
	}

	public void setSearchCalcFields(final boolean searchCalcFields) {
		this.searchCalcFields = searchCalcFields;
	}

	public void setSearchDbViews(final boolean searchDbViews) {
		this.searchDbViews = searchDbViews;
	}

	@Override
	public List<IElementInfo> getElements() {
		final IElementInfo whereUsedInCdsInfo = CdsAnalysisServiceFactory.createCdsAnalysisService()
			.loadWhereUsedFieldAnalysis(this.objectName, this.field, this.searchCalcFields, this.searchDbViews,
				this.destinationId);
		if (whereUsedInCdsInfo != null && whereUsedInCdsInfo instanceof IElementInfoCollection) {
			return ((IElementInfoCollection) whereUsedInCdsInfo).getChildren();
		}
		return null;
	}

	@Override
	public String getProviderDescription() {
		return NLS.bind(Messages.FieldWhereUsedInCdsElementInfoProvider_ProviderDescription_xmsg, this.objectName, this.field);
	}
}
