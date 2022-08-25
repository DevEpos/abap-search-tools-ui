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
import com.sap.adt.tools.core.AbapCore;
import com.sap.adt.tools.core.system.IAbapSystemInfo;
import com.sap.adt.tools.core.system.IUser;

/**
 * Search parameter for "owner"
 *
 * @author stockbal
 */
@SuppressWarnings("restriction")
public class UserSearchParameter implements ISearchParameter, ISearchProposalProvider {

  private final QueryParameterName parameterName;
  private final IAbapProjectProvider projectProvider;
  private final Image image;

  public UserSearchParameter(final IAbapProjectProvider projectProvider) {
    this.projectProvider = projectProvider;
    parameterName = QueryParameterName.OWNER;
    image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.USER_PARAM);
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
    return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionUserParameter_xmsg, new Object[] {
        parameterName.getLowerCaseKey(), "smith" });
  }

  @Override
  public List<IContentProposal> getProposalList(final String query) throws CoreException {
    final List<IContentProposal> proposals = new ArrayList<>();
    try {
      final List<IUser> users = getUsers(query);
      if (users != null) {
        for (final IUser user : users) {
          proposals.add(new SearchParameterProposal(user.getId(), parameterName, user.getText(),
              query));
          if (proposals.size() >= 50) {
            break;
          }
        }
      }
    } catch (final Exception e) {
      final IStatus status = new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, e
          .getMessage());
      throw new CoreException(status);
    }
    return proposals;
  }

  /**
   * Retrieve users from ABAP system
   *
   * @param query
   * @return
   * @throws CoreException
   */
  private List<IUser> getUsers(final String query) throws CoreException {
    List<IUser> users = null;
    try {
      final String destination = projectProvider.getDestinationId();
      if (destination != null && projectProvider.ensureLoggedOn()) {
        final IAbapSystemInfo systemInfo = AbapCore.getInstance().getAbapSystemInfo(destination);
        users = systemInfo.getUsers(new NullProgressMonitor(), String.valueOf(query) + "*", 50);
      }
    } catch (final OperationCanceledException ex) {
    }
    return users;
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
