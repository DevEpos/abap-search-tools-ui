package com.devepos.adt.saat.internal.search.contentassist;

import java.util.Locale;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

/**
 * Parameter proposal like <strong>owner</strong>
 *
 * @author stockbal
 *
 */
public class SearchFilterProposal implements IContentProposal {
  private final String name;
  private final Image image;
  private final String description;
  private final String wordToComplete;
  private final boolean hasProposalSupport;

  /**
   * Creates new search parameter proposal with the given name and description
   *
   * @param name               the name of parameter
   * @param image              the image to be displayed for the proposal
   * @param description        the description of the parameter
   * @param wordToComplete     the query String of the current proposal request
   * @param hasProposalSupport <code>true</code> if the filter parameter support
   *                           automatic proposals
   */
  public SearchFilterProposal(final String name, final Image image, final String description,
      final String wordToComplete, final boolean hasProposalSupport) {
    this.name = name;
    this.image = image;
    this.description = description;
    this.wordToComplete = wordToComplete;
    this.hasProposalSupport = hasProposalSupport;
  }

  /**
   * Creates new search parameter proposal with the given name and description
   *
   * @param name               the name of parameter
   * @param image              the image to be displayed for the proposal
   * @param description        the description of the parameter
   * @param hasProposalSupport <code>true</code> if the filter parameter supports
   *                           automatic proposals
   */
  public SearchFilterProposal(final String name, final Image image, final String description,
      final boolean hasProposalSupport) {
    this(name, image, description, null, hasProposalSupport);
  }

  public Image getImage() {
    return image;
  }

  @Override
  public String getContent() {
    String content = String.valueOf(name.toLowerCase(Locale.ENGLISH));
    if (wordToComplete != null && !wordToComplete.isEmpty()) {
      content = content.substring(wordToComplete.length());
    }
    final String proposalContent = content + ":";
    return proposalContent;
  }

  @Override
  public int getCursorPosition() {
    return getContent().length();
  }

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public boolean hasProposalSupport() {
    return hasProposalSupport;
  }

}
