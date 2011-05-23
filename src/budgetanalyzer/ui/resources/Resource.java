/*
 * Resource.java
 *
 * Created on 10. juli 2007, 22:07
 *
 */

package budgetanalyzer.ui.resources;

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * ResourceBundle convenience class.
 * Design Pattern: Adaptor
 * @author Rasmus
 * @version 1.0
 */
public class Resource extends dk.lockfuglsang.rasmus.util.resources.Resource {
  static final protected dk.lockfuglsang.rasmus.util.resources.Resource INSTANCE = 
      new dk.lockfuglsang.rasmus.util.resources.Resource("BudgetAnalyzer");
  protected String baseName;
  /**
   * Creates a new instance of Resource
   */
  public Resource(String name) {
    super("BudgetAnalyzer");
    this.baseName = name;
  }
  public String getString(String key, String defaultValue) {
    return INSTANCE.getString(baseName + "." + key, defaultValue);
  }
  public String getString(String key) {
    return INSTANCE.getString(baseName + "." + key);
  }
  public String getString(String key, Object[] args) {
    return INSTANCE.getString(baseName + "." + key, args);
  }
  public int getInt(String key, int defaultValue) {
    return INSTANCE.getInt(baseName + "." + key, defaultValue);
  }
  public Color createColor(String key) {
    return INSTANCE.createColor(baseName + "." + key);
  }
  public JMenuItem createJMenuItem(String key) {
    return INSTANCE.createJMenuItem(baseName + "." + key);
  }
  public JMenu createJMenu(String key) {
    return INSTANCE.createJMenu(baseName + "." + key);
  }
  public JButton createJButton(String key) {
    return INSTANCE.createJButton(baseName + "." + key);
  }
  protected void prepareAbstractButton(AbstractButton btn, String key) {
    super.prepareAbstractButton(btn, key);
  }
  public void prepareAction(Action action, String key) {
    INSTANCE.prepareAction(action, baseName + "." + key);
  }
}
