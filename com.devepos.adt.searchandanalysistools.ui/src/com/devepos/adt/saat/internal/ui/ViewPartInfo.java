package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;

/**
 * Composite that can be used to display some information about e.g. Views
 *
 * @author stockbal
 */
public abstract class ViewPartInfo extends ScrolledComposite {
  private static final String HELP_LINK = "showHelp";
  protected static final int BINDING_TEXT_LEFT_MARGIN = 10;
  protected static final int BULLET_LINE_INDENT = 10;
  private Composite content;
  private final String title;

  /**
   * Creates new info text composite
   *
   * @param parent the parent composite
   */
  public ViewPartInfo(final Composite parent) {
    this(parent, null);
  }

  /**
   * Creates new info text composite with the given title
   *
   * @param parent the parent composite
   * @param title  the title to be used
   */
  public ViewPartInfo(final Composite parent, final String title) {
    super(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    this.title = title;
    /*
     * hack to fix background problem of StyledText in Dark mode (see
     * com.sap.adt.util.ui.controls.AdtViewTextPage)
     */
    setData("org.eclipse.e4.ui.css.id", "AdtViewTextPage");
    init();
    createInformationText();
  }

  /**
   * Some initialization before the information content is created. <br>
   * Subclasses may override
   */
  protected void init() {

  }

  protected void createInformationText() {
    content = new Composite(this, SWT.NONE);
    content.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
    content.setLayout(new GridLayout());
    addTitle();
    createContent();
    addHelpLink();
    setExpandHorizontal(true);
    setExpandVertical(true);
    this.setMinSize(50, 50);
    setContent(content);
    addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(final ControlEvent e) {
        final Rectangle r = ViewPartInfo.this.getClientArea();
        ViewPartInfo.this.setMinSize(content.computeSize(r.width, -1));
      }
    });
  }

  /**
   * Subclasses must override to create the actual content.
   */
  protected abstract void createContent();

  /**
   * Creates the contents of the information text
   */

  /**
   * Creates a simple Label and adds it the information text with the given text.
   *
   * @param text the text for the label
   * @return
   */
  protected Label addLabel(final String text) {
    final Label label = new Label(content, SWT.LEFT | SWT.TOP | SWT.WRAP);
    label.setText(text);
    label.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
    return label;
  }

  /**
   * Creates a section title and adds it to the information text with the given
   * text.
   *
   * @param text the created styled section title
   * @return
   */
  protected StyledText addSectionTitleStyledText(final String text) {
    final StyledText styledText = addStyledText(text);
    styledText.setFont(JFaceResources.getFont("org.eclipse.jface.bannerfont")); //$NON-NLS-1$
    return styledText;
  }

  /**
   * Creates a styled title with the given text and adds it to the information
   * text.
   *
   * @param text the title text to be used
   * @return
   */
  protected StyledText addTitleStyledText(final String text) {
    final StyledText styledText = addStyledText(text);
    styledText.setFont(JFaceResources.getFont("org.eclipse.jface.headerfont")); //$NON-NLS-1$
    return styledText;
  }

  /**
   * Creates a read only styled text with the given text and adds it to the
   * information text.
   *
   * @param text the string to be used in the styled text
   * @return
   */
  protected StyledText addStyledText(final String text) {
    final StyledText styledText = new StyledText(content, SWT.WRAP);
    styledText.setText(text);
    styledText.setEditable(false);
    return styledText;
  }

  /**
   * The text to be used for the help link. <br>
   * <strong>Note</strong>: Subclasses must override if a help link is to be shown
   *
   * @return the text for the help link or <code>null</code> if no help link
   *         should be shown
   */
  protected String getHelpLinkText() {
    return null;
  }

  /**
   * The help context id to be used during calling the help. If it is not supplied
   * the workbench will call the help dynamically from the current UI context
   *
   * @return the help context id or <code>null</code>
   */
  protected String getHelpContextId() {
    return null;
  }

  /**
   * Sets bullet points to the list of styled texts
   *
   * @param texts an array of styled texts for which bullet points should be added
   */
  protected void setLineBulletToStyledTexts(final StyledText... texts) {
    if (texts == null) {
      return;
    }
    for (final StyledText text : texts) {
      final StyleRange styleRange = new StyleRange();
      styleRange.metrics = new GlyphMetrics(0, 0, 10);
      final Bullet bullet = new Bullet(styleRange);
      text.setLeftMargin(10);
      text.setLineBullet(0, 1, bullet);
      text.setLineWrapIndent(0, 1, 10);
    }
  }

  /**
   * Sets the layout data to "wrapping" for the given list of composites
   *
   * @param composites an array of composites
   */
  protected void setWrappingLayoutData(final Composite... composites) {
    if (composites == null) {
      return;
    }
    for (final Composite composite : composites) {
      composite.setLayoutData(new GridData(SWT.H_SCROLL | SWT.V_SCROLL));
    }
  }

  /*
   * Adds a styled title to the info text if one was supplied
   */
  private void addTitle() {
    if (title == null) {
      return;
    }
    final StyledText title = addTitleStyledText(this.title);
    setWrappingLayoutData(title);
  }

  /*
   * Adds help link
   */
  private void addHelpLink() {
    final String helpLinkText = getHelpLinkText();
    if (helpLinkText == null) {
      return;
    }
    final Link link = new Link(content, SWT.NONE);
    link.setBackground(JFaceColors.getBannerBackground(Display.getDefault()));
    link.setText(String.format("<a href=\"%s\">%s %s</a>", ViewPartInfo.HELP_LINK,
        "Learn more about", helpLinkText));
    link.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (ViewPartInfo.HELP_LINK.equals(e.text)) {
          ViewPartInfo.this.displayHelp();
        }
      }
    });
    link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
  }

  private void displayHelp() {
    final String helpContextId = getHelpContextId();
    if (helpContextId == null) {
      displayDynamicHelp();
    } else {
      this.displayHelp(helpContextId);
    }
  }

  private void displayDynamicHelp() {
    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
  }

  private void displayHelp(final String helpContextId) {
    PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextId);
  }
}
