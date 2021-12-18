package com.devepos.adt.saat.internal.search.contentassist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.base.util.Reflection;
import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Content Assist for ADT Repository Information System search pattern
 *
 * @author stockbal
 */
public class SearchPatternContentAssist extends ContentProposalProvider {
  private static final int POPUP_DEFAULT_WIDTH = 300;
  private static final int FILTER_PROPOSAL_POPUP_MIN_HEIGHT = 200;
  private static final int POPUP_DEFAULT_HEIGHT = 300;
  private final Text text;
  private final List<IContentProposal> queryResults;
  private final SearchPatternContentProvider contentProvider;
  private final IContentProposalListener proposalListener;
  private final IContentChangeListener contentChangeListener;

  public SearchPatternContentAssist(final Text control,
      final SearchPatternAnalyzer patternAnalyzer) {
    super(control);

    text = control;
    queryResults = new ArrayList<>();
    contentProvider = new SearchPatternContentProvider(patternAnalyzer);
    getContentProposalAdapter().setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
    getContentProposalAdapter().setAutoActivationCharacters(new char[] { ':', ',' });
    getContentProposalAdapter().setAutoActivationDelay(500);

    contentChangeListener = resultList -> {
      showProposals(resultList);
    };

    proposalListener = new IContentProposalListener() {

      @Override
      public void proposalAccepted(final IContentProposal proposal) {
        if (proposal instanceof SearchFilterProposal && ((SearchFilterProposal) proposal)
            .hasProposalSupport()) {
          // this call is needed, otherwise a new proposal pop up will not open,
          // regardless of the inserted ":" character
          Reflection.forObject(getContentProposalAdapter()).invoke("autoActivate");
        } else if (proposal instanceof SearchParameterProposal) {
          adjustMatch((SearchParameterProposal) proposal);
        }
      }

      /**
       * Adjusts the text input control depending on the inserted proposal.
       *
       * @param proposal
       */
      private void adjustMatch(final SearchParameterProposal proposal) {
        final String insertedText = proposal.getContent();
        if (proposal.getKey().equalsIgnoreCase(insertedText)) {
          final String queryAfterInsert = text.getText();
          final int caretPosition = text.getCaretPosition();
          final int caretBeforeLexem = caretPosition - insertedText.length() - proposal.getLexeme()
              .length();
          final String adjustedProposal = queryAfterInsert.substring(0, caretBeforeLexem)
              + insertedText;
          final String query = adjustedProposal + queryAfterInsert.substring(caretPosition);
          text.setText(query);
          text.setSelection(adjustedProposal.length());
        }
      }
    };

    getContentProposalAdapter().addContentProposalListener(proposalListener);
    contentProvider.addContentChangeListener(contentChangeListener);
    text.addVerifyListener(e -> {
      final boolean popupActive = isProposalPopupActive();
      if (popupActive) {
        final KeyStroke keyStroke = getActivationKeyStroke();

        if (e.keyCode == keyStroke.getNaturalKey() && e.stateMask == keyStroke.getModifierKeys()) {
          e.doit = false;
        }
      }
    });
  }

  /**
   * Disposes of allocated resources
   */
  public void dispose() {
    final ContentProposalAdapter adapter = getContentProposalAdapter();
    if (adapter == null) {
      return;
    }

    final ILabelProvider labelProvider = adapter.getLabelProvider();
    if (labelProvider != null) {
      adapter.setLabelProvider(null);
      labelProvider.dispose();
    }
    if (contentChangeListener != null && contentProvider != null) {
      contentProvider.removeContentChangeListener(contentChangeListener);
    }

    if (proposalListener != null) {
      adapter.removeContentProposalListener(proposalListener);
    }
  }

  @Override
  public IContentProposal[] getProposals(final String contents, final int position) {
    final List<IContentProposal> result = new ArrayList<>();

    if (!isTextFieldEnabled() || !isTextFieldEditable()) {
      return result.toArray(new IContentProposal[result.size()]);
    }

    final String query = contents.substring(0, position);

    result.addAll(contentProvider.computeProposals(query));

    queryResults.clear();
    queryResults.addAll(result);

    return result.toArray(new IContentProposal[result.size()]);
  }

  /**
   * Sets the label provider for the content proposal adapter
   *
   * @param labelProvider the ILabelProvider to be used to display the labels of
   *                      the content proposals
   */
  public void setLabelProvider(final ILabelProvider labelProvider) {
    getContentProposalAdapter().setLabelProvider(labelProvider);
  }

  @Override
  protected String getContentAssistDecoratorText(final KeyStroke keyStroke) {
    return NLS.bind(Messages.ObjectSearch_ContentAssistDecorator_xtol, keyStroke.format());
  }

  protected boolean isProposalPopupActive() {
    if (getContentProposalAdapter() != null) {
      return getContentProposalAdapter().isProposalPopupOpen();
    }
    return false;
  }

  private Point calculatePopupSize(final PopupDialog popup) {
    int height = POPUP_DEFAULT_HEIGHT;
    if (queryResults.get(0) instanceof SearchParameterProposal) {
      return new Point(calculatePopupWidth(popup), height);
    }
    if (queryResults.get(0) instanceof SearchFilterProposal) {
      height = text.getSize().y * queryResults.size() * 3;
      if (height > FILTER_PROPOSAL_POPUP_MIN_HEIGHT) {
        height = Math.min(POPUP_DEFAULT_HEIGHT, height);
      } else {
        height = FILTER_PROPOSAL_POPUP_MIN_HEIGHT;
      }
      return new Point(POPUP_DEFAULT_WIDTH, height);
    }
    return new Point(POPUP_DEFAULT_WIDTH, POPUP_DEFAULT_HEIGHT);
  }

  private int calculatePopupWidth(final PopupDialog popup) {
    int width = (int) Math.max(300L, Math.round(text.getSize().x * 0.5));
    final GC gc = new GC(popup.getShell().getParent());
    final Font font = popup.getShell().getChildren()[0].getFont();
    gc.setFont(font);
    final ILabelProvider labelProvider = getContentProposalAdapter().getLabelProvider();
    for (final IContentProposal proposal : queryResults) {
      int proposalWidth = gc.stringExtent(labelProvider.getText(proposal)).x + 26;
      final Image image = labelProvider.getImage(proposal);
      if (image != null) {
        proposalWidth += image.getImageData().width + 16;
      }
      if (proposalWidth > width) {
        width = proposalWidth;
      }
    }
    gc.dispose();
    return width;
  }

  /**
   * Retrieves the pop up dialog of the content proposal adapter
   *
   * @return the PopupDialog instance
   */
  private PopupDialog getProposalsPopup() {
    final ContentProposalAdapter proposalAdapter = getContentProposalAdapter();
    return (PopupDialog) Reflection.forObject(proposalAdapter)
        .getFieldValue("popup", proposalAdapter.getClass());
  }

  private boolean isTextFieldEditable() {
    boolean result = false;
    if (text != null && !text.isDisposed()) {
      result = text.getEditable();
    }
    return result;
  }

  private boolean isTextFieldEnabled() {
    boolean result = false;
    if (text != null && !text.isDisposed()) {
      result = text.isEnabled();
    }
    return result;
  }

  private void resizePopupDeferred(final PopupDialog popup) throws NoSuchMethodException,
      IllegalAccessException, InvocationTargetException {
    if (queryResults == null || queryResults.size() == 0) {
      popup.close();
      return;
    }
    final Point popupSize = calculatePopupSize(popup);
    getContentProposalAdapter().setPopupSize(popupSize);
    Reflection.forObject(popup).invoke("adjustBounds");
  }

  /**
   * Shows the given list of content proposals in the proposal popup
   *
   * @param newProposals list of new content proposals to be displayed
   */
  private void showProposals(final List<IContentProposal> newProposals) {
    queryResults.clear();
    queryResults.addAll(newProposals);

    final IContentProposal[] proposals = newProposals.toArray(new IContentProposal[newProposals
        .size()]);

    final PopupDialog popup = getProposalsPopup();
    if (popup != null) {
      if (proposals.length == 0) {
        popup.close();
      } else {
        Reflection.forObject(popup)
            .invoke("setProposals", new Class[] { IContentProposal[].class }, new Object[] {
                proposals });
        try {
          resizePopupDeferred(popup);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
