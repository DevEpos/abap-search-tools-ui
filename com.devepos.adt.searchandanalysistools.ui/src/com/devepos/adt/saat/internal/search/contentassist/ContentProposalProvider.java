package com.devepos.adt.saat.internal.search.contentassist;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Abstract Content proposal provider implementation for DB Browser Tools
 *
 * @author stockbal
 */
public abstract class ContentProposalProvider implements IContentProposalProvider {
  private final KeyStroke triggeringKeyStroke;
  private static final KeyStroke DEFAULT_ACTIVATION_KEYSTROKE = KeyStroke.getInstance(isMacOS()
      ? SWT.MOD4
      : SWT.MOD1, SWT.SPACE);
  private final ContentProposalAdapter contentProposalAdapter;

  public ContentProposalProvider(final Control control) {
    triggeringKeyStroke = calculateActivationKeyStroke();
    contentProposalAdapter = addToControl(control);
    contentProposalAdapter.setProposalAcceptanceStyle(2);
  }

  private static boolean isMacOS() {
    return Platform.isRunning() && Platform.getOS().equals("macosx");
  }

  protected ContentProposalAdapter addToControl(final Control control) {
    final ControlDecoration dec = new ControlDecoration(control, SWT.TOP | SWT.LEFT);

    final String text = getContentAssistDecoratorText(triggeringKeyStroke);
    dec.setDescriptionText(text);
    if (Platform.isRunning()) {
      dec.setImage(FieldDecorationRegistry.getDefault()
          .getFieldDecoration("DEC_CONTENT_PROPOSAL")
          .getImage());
    }
    dec.setShowOnlyOnFocus(true);
    dec.setShowHover(true);
    IControlContentAdapter contentAdapter;

    if (!(control instanceof Text)) {
      throw new IllegalArgumentException("Can only attach to Text controls, not to " + control);
    }
    contentAdapter = new TextContentAdapter();
    return new ContentProposalAdapter(control, contentAdapter, this, triggeringKeyStroke,
        new char[0]);
  }

  /**
   * Retrieve the content proposal adapter
   *
   * @return
   */
  protected ContentProposalAdapter getContentProposalAdapter() {
    return contentProposalAdapter;
  }

  /**
   * Retrieve the keystroke for content assist activation
   *
   * @return
   */
  public static KeyStroke getActivationKeyStroke() {
    return calculateActivationKeyStroke();
  }

  /**
   * Calculates the default key stroke for content assist activation
   *
   * @return
   */
  private static KeyStroke calculateActivationKeyStroke() {
    final KeyStroke stroke = getActivationKeyStrokeFromPreferences();
    if (stroke != null) {
      return stroke;
    }
    return DEFAULT_ACTIVATION_KEYSTROKE;
  }

  protected String getContentAssistDecoratorText(final KeyStroke keyStroke) {
    return getContentAssistDescription(keyStroke);
  }

  public static String getContentAssistDescription(final KeyStroke keyStroke) {
    return NLS.bind(Messages.ContentProposalProvider_contentAssistTooltip_xtol, keyStroke.format());
  }

  /**
   * Retrieve and return activation key stroke for content assist from preferences
   *
   * @return
   */
  private static KeyStroke getActivationKeyStrokeFromPreferences() {
    try {
      if (Platform.isRunning()) {
        final IBindingService service = PlatformUI.getWorkbench().getService(IBindingService.class);
        if (service != null) {
          final TriggerSequence binding = service.getBestActiveBindingFor(
              "org.eclipse.ui.edit.text.contentAssist.proposals");
          if (binding instanceof KeySequence) {
            final KeyStroke[] keyStrokes = ((KeySequence) binding).getKeyStrokes();
            if (keyStrokes.length == 1) {
              return keyStrokes[0];
            }
          }
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
