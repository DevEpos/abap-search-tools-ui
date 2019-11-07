package com.devepos.adt.saat.internal.search.contentassist;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.ObjectType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Label provider for the DB Object Search content assist
 *
 * @author stockbal
 *
 */
public class LabelProvider extends org.eclipse.jface.viewers.LabelProvider {

	@Override
	public Image getImage(final Object element) {
		if (element instanceof SearchParameterProposal) {
			final SearchParameterProposal parameter = (SearchParameterProposal) element;
			switch (parameter.getParameterName()) {
			case OWNER:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.USER);
			case PACKAGE_NAME:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.PACKAGE_PARAM);
			case RELEASE_STATE:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.API_PARAM);
			case ASSOCIATED_IN:
			case SELECT_SOURCE_IN:
				return getImageForType(parameter.getType());
			case EXTENDED_BY:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.EXTENSION_VIEW);
			case FIELD_NAME:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.FIELD_PARAM);
			case ANNOTATION:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.ANNOTATION_PARAM);
			case TYPE:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.ABAP_TYPE);
			default:
				return null;
			}
		} else if (element instanceof SearchFilterProposal) {
			return ((SearchFilterProposal) element).getImage();
		}
		return null;
	}

	@Override
	public String getText(final Object element) {
		if (element instanceof SearchFilterProposal) {
			final SearchFilterProposal proposal = (SearchFilterProposal) element;
			return proposal.getLabel();
		}
		if (element instanceof SearchParameterProposal) {
			final SearchParameterProposal proposal = (SearchParameterProposal) element;
			String result = proposal.getLabel();
			final String shortText = proposal.getShortText();
			if (shortText != null && !shortText.isEmpty()) {
				result = String.valueOf(result) + " (" + shortText + ")";
			}
			return result;
		}
		if (element instanceof IContentProposal) {
			final IContentProposal proposal = (IContentProposal) element;
			return proposal.getLabel();
		}
		return null;
	}

	/**
	 * Retrieves the image for the given type
	 *
	 * @param type
	 * @return
	 */
	private Image getImageForType(final ObjectType objectType) {
		if (objectType == null) {
			return null;
		} else {
			switch (objectType) {
			case CDS_VIEW:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.CDS_VIEW);
			case TABLE:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.TABLE_DEFINITION);
			case VIEW:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.VIEW_DEFINITION);
			default:
				return null;
			}
		}
	}

}
