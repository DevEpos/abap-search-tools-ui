package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.preferences.IPreferences;

/**
 * Tracks all performed CDS analyses in the current workspace
 *
 * @author Ludwig Stockbauer-Muhr
 */
public class CdsAnalysisManager {

  private static CdsAnalysisManager instance;
  private List<CdsAnalysis> analyses;
  private Map<CdsAnalysisKey, CdsAnalysis> analysesMap;
  private final List<ICdsAnalysisListener> listeners;

  /**
   * Creates new instance of the CDS Analysis manager
   */
  private CdsAnalysisManager() {
    analyses = new LinkedList<>();
    listeners = new ArrayList<>();
    analysesMap = new HashMap<>();
  }

  /**
   * Returns instance of the CDS analyses manager
   *
   * @return instance of the CDS analyses manager
   */
  public static CdsAnalysisManager getInstance() {
    if (instance == null) {
      instance = new CdsAnalysisManager();
    }
    return instance;
  }

  /**
   * Moves the given analysis to the top of the list
   *
   * @param analysis the CDS analysis that was LRU
   */
  public void activated(final CdsAnalysis analysis) {
    synchronized (this) {
      if (analyses.contains(analysis)) {
        analyses.remove(analysis);
        analyses.add(0, analysis);
      }
    }
  }

  /**
   * Adds the given CDS analysis result at the top of the list
   *
   * @param result the result to be added
   */
  public void addAnalysis(final CdsAnalysis result) {
    establishHistoryLimit();
    synchronized (this) {
      analyses.add(0, result);
    }
  }

  /**
   * Adds the given listener if not already included
   *
   * @param l the listener to be added
   */
  public void addCdsAnalysisListener(final ICdsAnalysisListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  public void fireRemoved(final CdsAnalysis analysis) {
    final Set<ICdsAnalysisListener> copiedListeners = new HashSet<>();
    synchronized (listeners) {
      copiedListeners.addAll(listeners);
    }
    final Iterator<ICdsAnalysisListener> listeners = copiedListeners.iterator();
    while (listeners.hasNext()) {
      final ICdsAnalysisListener l = listeners.next();
      l.analysisRemoved(analysis);
    }
  }

  /**
   * Returns a LRU list of CDS analyses
   *
   * @return
   */
  public CdsAnalysis[] getAnalyses() {
    synchronized (this) {
      return analyses.toArray(new CdsAnalysis[analyses.size()]);
    }
  }

  /**
   * Returns an existing analysis for the given key
   *
   * @param key the key for a CDS analysis
   * @return the found CDS analysis or <code>null</code> if none could be found
   */
  public CdsAnalysis getExistingAnalysis(final CdsAnalysisKey key) {
    synchronized (this) {
      return analysesMap.get(key);
    }
  }

  public int getSize() {
    synchronized (this) {
      return analyses.size();
    }
  }

  /**
   * Returns <code>true</code> if there are analyses in the history
   *
   * @return <code>true</code> if there are analyses in the history
   */
  public boolean hasAnalyses() {
    return !analyses.isEmpty();
  }

  /**
   * Registers the given CDS analysis with the given analysis key
   *
   * @param analysisKey the key object for the analysis
   * @param newAnalysis the new analysis object to be registered
   */
  public void registerAnalysis(final CdsAnalysisKey analysisKey, final CdsAnalysis newAnalysis) {
    analysesMap.put(analysisKey, newAnalysis);
  }

  public void removeAll() {
    synchronized (this) {
      final List<CdsAnalysis> old = analyses;
      analyses = new LinkedList<>();
      analysesMap = new HashMap<>();
      final Iterator<CdsAnalysis> iter = old.iterator();
      while (iter.hasNext()) {
        final CdsAnalysis element = iter.next();
        fireRemoved(element);
      }
    }
  }

  /**
   * Removes the given analysis result from the list
   *
   * @param analysis the analysis object to be removed
   */
  public void removeAnalysis(final CdsAnalysis analysis) {
    synchronized (this) {
      analyses.remove(analysis);
      analysesMap.values().remove(analysis);
    }
    fireRemoved(analysis);
  }

  /**
   * Removes the given listener if it was registered
   *
   * @param l the listener to be removed
   */
  public void removeCdsAnalysisListener(final ICdsAnalysisListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  /**
   * Shows the given analysis in the CDS Analyzer view
   *
   * @param analysis  the analysis to be shown
   * @param openInNew if {@code true} the analysis will be opened in a new CDS Analyzer view
   */
  public void showAnalysis(final CdsAnalysis analysis, final boolean openInNew) {
    /*
     * retrieve CDS analyzer view Note: At this time only one active view is
     * possible.
     */
    final CdsAnalysisView cdsAnalyzerView = CdsAnalysisViewManager.getInstance()
        .activateCdsAnalysisView(openInNew);
    if (cdsAnalyzerView != null) {
      cdsAnalyzerView.setFocus();
      activated(analysis);
      cdsAnalyzerView.showCdsAnalysis(analysis);
    }
  }

  private void establishHistoryLimit() {
    final int historyLimit = SearchAndAnalysisPlugin.getDefault()
        .getPreferenceStore()
        .getInt(IPreferences.MAX_CDS_ANALYZER_HISTORY);
    if (historyLimit >= getSize()) {
      return;
    }
    int numberQueriesNotShown = 0;
    final CdsAnalysisViewManager viewManager = CdsAnalysisViewManager.getInstance();
    final CdsAnalysis[] analyses = getAnalyses();
    for (final CdsAnalysis analysis : analyses) {
      if (!viewManager.isAnalysisShown(analysis) && ++numberQueriesNotShown >= historyLimit) {
        removeAnalysis(analysis);
      }
    }
  }

}
