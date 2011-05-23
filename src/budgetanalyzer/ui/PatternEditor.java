/*
 * PatternEditor.java
 *
 * Created on 24. juli 2007, 21:05
 *
 */

package budgetanalyzer.ui;

import budgetanalyzer.model.patterns.AbstractFieldPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.panels.PatternPanel;
import budgetanalyzer.ui.panels.PatternPanelImpl;
import budgetanalyzer.ui.resources.Resource;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class PatternEditor {
  protected PatternPanel patternPanel;
  protected JPanel centerPanel;
  protected JDialog dialog;
  protected int option;
  /** Creates a new instance of PatternEditor */
  public PatternEditor(JFrame owner) {
    Resource res = new Resource("PatternEditor");
    dialog = new JDialog(owner, res.getString("Title"), true);
    dialog.setLayout(new BorderLayout());
    centerPanel = new JPanel(new BorderLayout());
    dialog.add(centerPanel, BorderLayout.CENTER);
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btn = res.createJButton("Button.Cancel");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        option = JOptionPane.CANCEL_OPTION;
        dialog.setVisible(false);
      }
    });
    panel.add(btn);
    btn = res.createJButton("Button.Ok");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        option = JOptionPane.OK_OPTION;
        dialog.setVisible(false);
      }
    });
    panel.add(btn);
    dialog.add(panel, BorderLayout.SOUTH);
  }
  public int editPattern(Pattern p) {
    option = JOptionPane.CANCEL_OPTION;
    if (p instanceof AbstractFieldPattern) {
      AbstractFieldPattern afp = (AbstractFieldPattern)p;
      patternPanel = new PatternPanelImpl(afp.getFieldId(), afp.getExpression());
      centerPanel.removeAll();
      centerPanel.add((PatternPanelImpl)patternPanel, BorderLayout.CENTER);
      centerPanel.invalidate();
      dialog.validate();
    } else {
      throw new IllegalArgumentException("Only AbstractFieldPatterns are supported");
    }
    dialog.pack();
    dialog.setVisible(true);
    return option;
  }
  public Pattern getPattern() {
    return patternPanel.getPattern();
  }
}
