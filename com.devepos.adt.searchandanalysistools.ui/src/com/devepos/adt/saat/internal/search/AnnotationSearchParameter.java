package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.util.StringUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

public class AnnotationSearchParameter extends NamedItemProposalProvider implements
    ISearchParameter, ISearchProposalProvider, IValidatable {

  public static final String KEY_VAL_SEPARATOR = "="; //$NON-NLS-1$
  private final NamedItemProposalProvider annotationValueProposalProvider;
  private final QueryParameterName parameterName;
  private final Image image;

  /**
   */
  public AnnotationSearchParameter(final IAbapProjectProvider projectProvider) {
    super(projectProvider, QueryParameterName.ANNOTATION, NamedItemType.ANNOTATION);
    parameterName = QueryParameterName.ANNOTATION;
    image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.ANNOTATION_PARAM);
    annotationValueProposalProvider = new NamedItemProposalProvider(projectProvider,
        QueryParameterName.ANNOTATION, NamedItemType.ANNOTATION_VALUE);
  }

  @Override
  public QueryParameterName getParameterName() {
    return parameterName;
  }

  @Override
  public Image getImage() {
    return image;
  }

  @Override
  public String getLabel() {
    return parameterName.getLowerCaseKey();
  }

  @Override
  public String getDescription() {
    return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionAnnotationParameter_xmsg,
        new Object[] { getLabel(), "ObjectModel*foreignkey*", "VDM.viewType=#basic" }); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public boolean supportsPatternValues() {
    return true;
  }

  @Override
  public List<IContentProposal> getProposalList(final String query) throws CoreException {
    // check if key/value separator is included in query
    if (query != null && query.contains(KEY_VAL_SEPARATOR)) {
      final String queryToAnalyze = query + "%"; //$NON-NLS-1$
      final String[] queryParts = queryToAnalyze.split(KEY_VAL_SEPARATOR);
      if (queryParts.length != 2) {
        return null;
      }
      final String annotationValueQuery = StringUtil.removeNegationCharacter(queryParts[1]
          .replaceAll("%", "")); //$NON-NLS-1$ //$NON-NLS-2$
      return annotationValueProposalProvider.getProposals(annotationValueQuery,
          annotationValueQuery, StringUtil.removeNegationCharacter(queryParts[0]));
    }
    return getProposals(query);
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

  @Override
  public void validate(final Object value) throws CoreException {
    final String annoFilterValue = (String) value;
    if (annoFilterValue.contains(KEY_VAL_SEPARATOR)) {
      final String[] queryParts = annoFilterValue.split(KEY_VAL_SEPARATOR);
      if (queryParts.length != 2) {
        return;
      }

      final String annoKey = queryParts[0];

      if (StringUtil.startsWithNegationCharacter(annoKey)) {
        throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS
            .bind(Messages.SearchPatternAnalyzer_NoValuePartPossibleIfKeyIsNegated_xmsg,
                getLabel())));
      }

    }
  }

}
