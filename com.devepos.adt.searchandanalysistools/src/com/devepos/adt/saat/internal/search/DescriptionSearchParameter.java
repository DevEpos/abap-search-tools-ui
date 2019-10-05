package com.devepos.adt.saat.internal.search;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Search parameter to restrict query by description of objects
 *
 * @author stockbal
 */
public class DescriptionSearchParameter implements ISearchParameter {

	private final QueryParameterName parameterName;
	private final Image image;

	public DescriptionSearchParameter() {
		this.parameterName = QueryParameterName.DESCRIPTION;
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.DESCRIPTION_PARAM);
	}

	@Override
	public QueryParameterName getParameterName() {
		return this.parameterName;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public String getLabel() {
		return this.parameterName.getLowerCaseKey();
	}

	@Override
	public String getDescription() {
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionDescriptionParameter_xmsg,
			new Object[] { this.parameterName.getLowerCaseKey(), "*material*" });
	}

	@Override
	public boolean supportsPatternValues() {
		return true;
	}

	@Override
	public boolean isBuffered() {
		return false;
	}

	@Override
	public boolean supportsMultipleValues() {
		return true;
	}

	@Override
	public boolean supportsNegatedValues() {
		return true;
	}
}
