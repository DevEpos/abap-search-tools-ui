package com.devepos.adt.saat.internal.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.IAdtRisQuickSearch;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Search parameter for "package"
 *
 * @author stockbal
 */
public class PackageSearchParameter implements ISearchParameter, ISearchProposalProvider {

  private final QueryParameterName parameterName;
  private final Image image;
  private String destinationId;
  private IAdtRisQuickSearch packageProvider;
  private final IAbapProjectProvider projectProvider;

  /**
   * @param projectProvider
   * @param parameterName
   * @param imageId
   */
  public PackageSearchParameter(final IAbapProjectProvider projectProvider) {
    this.projectProvider = projectProvider;
    parameterName = QueryParameterName.PACKAGE_NAME;
    image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.PACKAGE_PARAM);
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
    return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionPackageParameter_xmsg, new Object[] {
        parameterName.getLowerCaseKey(), "test" });
  }

  @Override
  public List<IContentProposal> getProposalList(final String query) throws CoreException {
    final List<IContentProposal> result = new ArrayList<>();
    try {
      getPackageProvider();
      if (packageProvider != null) {
        List<IAdtObjectReference> packageList = new ArrayList<>();
        packageList = packageProvider.execute(String.valueOf(query) + "*", 50, false, true,
            "DEVC/K");
        if (packageList != null) {
          for (final IAdtObjectReference objRef : packageList) {
            result.add(new SearchParameterProposal(objRef.getName(), parameterName, objRef
                .getDescription(), query));
          }
        }
      }
    } catch (final OperationCanceledException ex) {
    } catch (final Exception e) {
      final IStatus status = new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, e
          .getMessage());
      throw new CoreException(status);
    }

    return result;
  }

  /**
   * Retrieve provider for retrieving packages via QuickSearch service
   */
  private void getPackageProvider() {
    final String currentDestinationId = projectProvider.getDestinationId();
    if (destinationId == null || destinationId != currentDestinationId) {
      if (!projectProvider.ensureLoggedOn()) {
        destinationId = null;
        return;
      }
      destinationId = currentDestinationId;
      try {
        packageProvider = AdtRisQuickSearchFactory.createQuickSearch(destinationId,
            new NullProgressMonitor());
      } catch (final RisQuickSearchNotSupportedException ex) {
      }
    }
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
