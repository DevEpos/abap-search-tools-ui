package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import com.devepos.adt.saat.internal.help.HelpContexts;
import com.devepos.adt.saat.internal.help.HelpUtil;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.ViewPartInfo;

/**
 * Info text for CDS Analysis View
 *
 * @author stockbal
 */
public class CdsAnalysisWelcomeText extends ViewPartInfo {

  public CdsAnalysisWelcomeText(final Composite parent) {
    super(parent);
  }

  @Override
  protected void createContent() {
    final StyledText startingInfo = addSectionTitleStyledText(
        Messages.CdsAnalysis_NoOpenCdsAnalysis_xmsg);
    final StyledText option1 = addStyledText(Messages.CdsAnalysisWelcome_Option1_xmsg);
    final StyledText option2 = addStyledText(Messages.CdsAnalysisWelcome_Option2_xmsg);
    final StyledText option3 = addStyledText(Messages.CdsAnalysisWelcome_Option3_xmsg);
    final StyledText option4 = addStyledText(Messages.CdsAnalysisWelcome_Option4_xmsg);
    final StyledText option5 = addStyledText(Messages.CdsAnalysisWelcome_Option5_xmsg);

    setWrappingLayoutData(startingInfo, option1, option2, option3, option4, option5);
    setLineBulletToStyledTexts(option1, option2, option3, option4, option5);
  }

  @Override
  protected String getHelpLinkText() {
    return Messages.CdsAnalysisWelcome_HelpSuffix_xlnk;
  }

  @Override
  protected String getHelpContextId() {
    return HelpUtil.getFullyQualifiedContextId(HelpContexts.CDS_ANALYZER);
  }

}
