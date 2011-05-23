/*
 * PatternPanel.java
 *
 * Created on 24. juli 2007, 21:09
 *
 */

package budgetanalyzer.ui.panels;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import budgetanalyzer.model.patterns.Pattern;

/**
 * TODO: Class Description
 *
 * @author Rasmus
 * @version 1.0
 */
public interface PatternPanel {
  static public final String PROP_OK = "OK";
  Pattern getPattern();
  void addPropertyChangeListener(String propName, PropertyChangeListener listener);
  JComponent render();
}
