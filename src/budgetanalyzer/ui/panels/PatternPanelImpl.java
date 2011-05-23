/*
 * PatternPanelImpl.java
 *
 * Created on 23. juli 2007, 22:20
 *
 */

package budgetanalyzer.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import budgetanalyzer.io.CategoryReader;
import budgetanalyzer.io.CategoryWriter;
import budgetanalyzer.io.LineReader;
import budgetanalyzer.io.SyntaxErrorException;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.resources.Resource;

/**
 * Easy editing of a pattern (AbstractFieldPattern).
 * @author Rasmus
 * @version 1.0
 */
public class PatternPanelImpl extends JPanel implements PatternPanel {
  static final String PATTERN_TYPES[] = {
    CategoryWriter.TOKEN_OP_ICONTAINS,
    CategoryWriter.TOKEN_OP_CONTAINS,
    CategoryWriter.TOKEN_OP_REGEXP,
    CategoryWriter.TOKEN_OP_EQ,
    CategoryWriter.TOKEN_OP_LT,
    CategoryWriter.TOKEN_OP_GT
  };
  
  protected String fieldId;
  protected JComboBox combo;
  protected JTextField tfExp;
  /**
   * Creates a new instance of PatternPanelImpl
   */
  public PatternPanelImpl(String fieldId, String expression) {
    super(new BorderLayout());
    this.fieldId = fieldId;
    this.add(new JLabel(fieldId));
    tfExp = new JTextField(expression, 40);
    tfExp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        firePropertyChange(PROP_OK, null, null);
      }
    });
    this.add(tfExp, BorderLayout.CENTER);
    combo = new JComboBox(PATTERN_TYPES);
    this.add(combo, BorderLayout.WEST);
  }
  public Pattern getPattern() {
    String sPattern = fieldId + " " + combo.getSelectedItem().toString() + " " + tfExp.getText();
    StringReader sr = new StringReader(sPattern);
    LineReader lrdr = new LineReader(new BufferedReader(sr));
    Pattern pattern;
    try {
      pattern = CategoryReader.readPattern(lrdr);
    } catch (SyntaxErrorException ex) {
      // Shouldn't happen!
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      // Shouldn't happen!
      throw new RuntimeException(ex);
    }
    return pattern;
  }
  public JComponent render() {
    return this;
  }
}
