package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.tools.base.project.IAbapProjectProvider;

/**
 * Simple implementation of a Search Parameter which retrieves its proposals via
 * the {@link NamedItemService}
 *
 * @author stockbal
 */
public class NamedItemParameter extends NamedItemProposalProvider implements ISearchParameter, ISearchProposalProvider {
	private String description;
	private Image image;
	private boolean supportsPatternValues = true;
	private boolean supportsMultipleValues = true;
	private boolean supportsNegatedValues;
	private final String initialQuery;

	/**
	 * Creates a simple search parameter with the given options
	 *
	 * @param projectProvider provider which supplies ABAP Project
	 * @param parameterName   the name of the parameter
	 * @param namedItemType
	 * @param isBuffered      if <code>true</code> the parameter supports buffering
	 *                        of it' proposals
	 * @param initialQuery    initial query String, used to get proposals. <br>
	 *                        Has no effect if <code>isBuffered</code> is
	 *                        <code>false</code>
	 */
	public NamedItemParameter(final IAbapProjectProvider projectProvider, final QueryParameterName parameterName,
		final NamedItemType namedItemType, final boolean isBuffered, final String initialQuery) {
		super(projectProvider, parameterName, namedItemType, isBuffered);
		this.initialQuery = initialQuery;
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		if (this.isCachingActive && this.initialQuery != null) {
			return getProposals(this.initialQuery, query);
		} else {
			return getProposals(query);
		}
	}

	@Override
	public QueryParameterName getParameterName() {
		return this.parameterName;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	/**
	 * Sets the image for the parameter
	 *
	 * @param image the image to set
	 */
	public void setImage(final Image image) {
		this.image = image;
	}

	@Override
	public String getLabel() {
		return this.parameterName.getLowerCaseKey();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description for the parameter
	 *
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public boolean supportsPatternValues() {
		return this.supportsPatternValues;
	}

	/**
	 * Sets the <code>supportPatternValues</code> property
	 *
	 * @param supportsPatternValues if <code>true</code> the parameter supports
	 *                              patterns inside the values
	 */
	public void setSupportsPatternValues(final boolean supportsPatternValues) {
		this.supportsPatternValues = supportsPatternValues;
	}

	@Override
	public boolean isBuffered() {
		return this.isCachingActive;
	}

	@Override
	public boolean supportsMultipleValues() {
		return this.supportsMultipleValues;
	}

	/**
	 * Sets the <code>supportMultipleValues</code> property
	 *
	 * @param supportsMultipleValues if <code>true</code> the parameter supports
	 *                               multiple input values
	 */
	public void setSupportsMultipleValues(final boolean supportsMultipleValues) {
		this.supportsMultipleValues = supportsMultipleValues;
	}

	@Override
	public boolean supportsNegatedValues() {
		return this.supportsNegatedValues;
	}

	/**
	 * Sets the <code>supportNegatedValues</code> property
	 *
	 * @param supportsNegatedValues if <code>true</code> the parameter supports
	 *                              negated values
	 */
	public void setSupportsNegatedValues(final boolean supportsNegatedValues) {
		this.supportsNegatedValues = supportsNegatedValues;
	}
}
