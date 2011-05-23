/*
 * CategoryEditor.java
 *
 * Created on 16. juli 2007, 22:01
 *
 */

package budgetanalyzer.ui;

import budgetanalyzer.io.CategoryReader;
import budgetanalyzer.io.CategoryWriter;
import budgetanalyzer.io.SyntaxErrorException;
import budgetanalyzer.model.Category;
import budgetanalyzer.ui.tree.CategoryTree;
import budgetanalyzer.ui.resources.Resource;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Allows the user to edit the categories in textual form.
 * @author Rasmus
 * @version 1.0
 */
public class CategoryEditor extends JDialog {
  static final public String PROP_CATEGORIES_CHANGED = "PROP_CATEGORIES_CHANGED";
  static final public Resource RES = new Resource("CategoryEditor");
  
  private JComboBox combo;
  
  private JPanel container;
  private JTextArea editor;
  private CategoryTree tree;
  
  private Collection<Category> categories;
  
  /** Creates a new instance of CategoryEditor */
  public CategoryEditor(JFrame owner) {
    this(owner, false);
  }
  public CategoryEditor(JFrame owner, boolean modal) {
    super(owner, RES.getString("Dialog.Title"), modal);
    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    
    JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    c.add(pane, BorderLayout.NORTH);
    
    combo = new JComboBox();
    combo.addItem(RES.getString("EditingMode.Textual"));
    combo.addItem(RES.getString("EditingMode.Tree"));
    combo.setSelectedIndex(1);
    combo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeView(true);
      }
    });
    pane.add(new JLabel(RES.getString("Label.EditingMode")));
    pane.add(combo);
    
    editor = new JTextArea(RES.getInt("Editor.Rows", 30),
        RES.getInt("Editor.Cols", 80));
    tree = new CategoryTree();
    
    container = new JPanel(new GridLayout(1,1));
    container.add(tree);
    JScrollPane scrollPane = new JScrollPane(container);
    scrollPane.setPreferredSize(editor.getPreferredSize());
    c.add(scrollPane, BorderLayout.CENTER);
    pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    c.add(pane, BorderLayout.SOUTH);
    JButton btn = RES.createJButton("Button.Cancel");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    pane.add(btn);
    
    if (!modal) {
      btn = RES.createJButton("Button.Save");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          save();
        }
      });
      pane.add(btn);
    }
    
    btn = RES.createJButton("Button.SaveAndClose");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAndClose();
      }
    });
    pane.add(btn);
    pack();
  }
  protected void changeView(boolean saveIt) {
    int type = combo.getSelectedIndex();
    if (saveIt) {
      save((type + 1) % 2);
    }
    switch (type) {
      case 0: gotoTextualMode(); break;
      case 1: gotoTreeMode(); break;
      default:
        throw new IllegalStateException("Unknown mode: " + combo.getSelectedIndex());
    }
  }
  public void editCategories(Collection<Category> lst) {
    categories = lst;
    changeView(false);
    setVisible(true);
  }
  public Collection<Category> getCategories() {
    return categories;
  }
  protected void saveAndClose() {
    if (save()) {
      this.setVisible(false);
    }
  }
  protected boolean save() {
    return save(combo.getSelectedIndex());
  }
  protected boolean save(int type) {
    switch (type) {
      case 0: return saveTextualMode();
      case 1: return saveTreeMode();
      default:
        throw new IllegalStateException("Unknown mode: " + combo.getSelectedIndex());
    }
  }
  /** Tries to save the categories back to the listener.
   * @throws IllegalStateException on SyntaxError. */
  protected boolean saveTextualMode() {
    boolean res = false;
    try {
      StringReader sr = new StringReader(editor.getText());
      BufferedReader rdr = new BufferedReader(sr);
      Collection<Category> lst = CategoryReader.readCategories(rdr);
      firePropertyChange(PROP_CATEGORIES_CHANGED, categories, lst);
      categories = lst;
      res = true;
    } catch (SyntaxErrorException e) {
      JOptionPane.showMessageDialog(this, "" + e.getLineNumber() + ": " + e.getMessage());
    } catch (IOException e) {
      // Do nothing
    }
    return res;
  }
  protected boolean saveTreeMode() {
    Collection<Category> lst = new ArrayList<Category>(categories);
    categories = tree.getCategories();
    firePropertyChange(PROP_CATEGORIES_CHANGED, lst, categories);
    return true;
  }
  private void gotoTextualMode() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    CategoryWriter.printCategories(ps, categories);
    ps.flush();
    editor.setText(baos.toString());
    container.removeAll();
    container.add(editor);
    refresh();
  }
  private void gotoTreeMode() {
    tree.setCategories(categories);
    container.removeAll();
    container.add(tree);
    refresh();
  }
  private void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        container.invalidate();
        container.getParent().validate(); // Scrollpane
        repaint();
      }
    });
  }
}
