/*
 * CategoryDialog.java
 *
 * Created on 23. juli 2007, 22:19
 *
 */

package budgetanalyzer.ui;

import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.CompositePattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.panels.PatternPanel;
import budgetanalyzer.ui.panels.PatternPanelImpl;
import budgetanalyzer.ui.resources.Resource;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Enables the easy assignment of a pattern to a category.
 * @author Rasmus
 * @version 1.0
 */
public class CategoryDialog extends JDialog {
  static final Resource RES = new Resource("CategoryDialog");
  
  protected Category category;
  protected JTextField categoryName;
  protected PatternPanel patternPanel;
  protected int option;
  
  /** Creates a new instance of CategoryDialog */
  public CategoryDialog(JFrame owner, Category category, String exp) {
    super(owner, RES.getString("Title"), true);
    this.category = category;
    JPanel panel = new JPanel();
    Container c = this.getContentPane();
    c.setLayout(new BorderLayout());
    c.add(panel, BorderLayout.CENTER);
    if (category == null) {
      categoryName = new JTextField(RES.getString("NewCategoryName"));
      panel.add(categoryName);
    } else {
      categoryName = null;
      panel.add(new JLabel(category.getName()));
    }
    patternPanel = new PatternPanelImpl(RES.getString("FieldId"), exp);
    patternPanel.addPropertyChangeListener(PatternPanel.PROP_OK, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        ok();
      }
    });
    panel.add(patternPanel.render());
    
    option = JOptionPane.NO_OPTION;
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btn = RES.createJButton("Button.Cancel");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        option = JOptionPane.CANCEL_OPTION;
        setVisible(false);
      }
    });
    panel.add(btn);
    btn = RES.createJButton("Button.Ok");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ok();
      }
    });
    panel.add(btn);
    c.add(panel, BorderLayout.SOUTH);
    this.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }
      public void keyReleased(KeyEvent e) {
      }
      public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          ok();
        }
      }
    });
    pack();
  }
  public int showPatternDialog() {
    // Highlight something
    if (categoryName != null) {
      categoryName.selectAll();
      categoryName.requestFocus();
    }
    setVisible(true);
    return option;
  }
  public Category getCategory() {
    Pattern pattern = patternPanel.getPattern();
    if (category != null) {
      Pattern cpat = category.getPattern();
      if (cpat instanceof CompositePattern) {
        CompositePattern composite = (CompositePattern)cpat;
        composite.addPattern(pattern);
      } else {
        OrPattern pat = new OrPattern();
        pat.addPattern(cpat);
        pat.addPattern(pattern);
        category = new Category(category.getName(), pat);
      }
    } else {
      category = new Category(categoryName.getText(), pattern);
    }
    return category;
  }
  private void ok() {
    option = JOptionPane.OK_OPTION;
    setVisible(false);
  }
}
