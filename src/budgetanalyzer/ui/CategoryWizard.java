/*
 * CategoryWizard.java
 *
 * Created on 24. juli 2007, 18:38
 *
 */

package budgetanalyzer.ui;

import budgetanalyzer.model.BankEntry;
import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.ContainsPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.resources.Resource;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class CategoryWizard {
  static final private int MIN_LENGTH = 4;
  static final private int MIN_FREQUENCY = 3;
  static final private String PAT_NUMBER = "[0-9][0-9]+";
  static final private String PAT_REMOVE[] = {
    PAT_NUMBER,
    "DK "
  };
  
  private Map<String,Integer> frequencyMap;
  private Collection<BankEntry> entries;
  private Collection<Category> categories;
  
  private int option;
  /** Creates a new instance of CategoryWizard */
  public CategoryWizard(Collection<BankEntry> entries) {
    frequencyMap = new HashMap<String,Integer>();
    this.entries = new ArrayList<BankEntry>(entries);
    this.categories = new ArrayList<Category>();
  }
  /** Displays the wizard. */
  public int showWizard(JFrame parent) {
    option = JOptionPane.OK_OPTION;
    Resource res = new Resource("CategoryWizard");
    final JDialog dialog = new JDialog(parent, res.getString("Title"), false);
    Container c = dialog.getContentPane();
    c.setLayout(new BorderLayout());
    JLabel lbl = new JLabel(res.getString("Status.Collecting"));
    lbl.setHorizontalAlignment(JLabel.CENTER);
    c.add(lbl, BorderLayout.NORTH);
    
    JPanel panel = new JPanel(new BorderLayout());
    JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, entries.size());
    panel.add(progress, BorderLayout.NORTH);
    MessageFormat msgFormat = new MessageFormat(res.getString("Message.NumWordsFound"));
    
    Object[] args = { new Integer(0) };
    JLabel numCats = new JLabel(msgFormat.format(args));
    panel.add(numCats, BorderLayout.SOUTH);
    c.add(panel, BorderLayout.CENTER);
    
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btn = res.createJButton("Cancel");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        option = JOptionPane.CANCEL_OPTION;
        dialog.setVisible(false);
      }
    });
    panel.add(btn);
    c.add(panel, BorderLayout.SOUTH);
    
    dialog.pack();
    dialog.setVisible(true);
    int cnt = 0;
    for (BankEntry be : entries) {
      /*
      String[] keys = be.getTekst().split(" ");
      for (int i = 0; i < keys.length; i++) {
        String k = keys[i].trim();
        if (k.length() >= MIN_LENGTH && !k.matches(PAT_NUMBER)) {
          Integer freq = new Integer(0);
          if (frequencyMap.containsKey(k)) {
            freq = frequencyMap.get(k);
          }
          freq = new Integer(freq.intValue()+1);
          frequencyMap.put(k, freq);
        }
      }
       */
      String k = be.getText().trim();
      for (int i = 0; i < PAT_REMOVE.length; i++) {
        k = k.replaceAll(PAT_REMOVE[i], "");
      }
      k = k.trim();
      if (k.length() >= MIN_LENGTH) {
        Integer freq = new Integer(0);
        if (frequencyMap.containsKey(k)) {
          freq = frequencyMap.get(k);
        }
        freq = new Integer(freq.intValue()+1);
        frequencyMap.put(k, freq);
      }
      progress.setValue(++cnt);
      args[0] = new Integer(frequencyMap.size());
      numCats.setText(msgFormat.format(args));
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          dialog.repaint();
        }
      });
      if (option != JOptionPane.OK_OPTION) break;
    }
    // Next phase...
    progress.setMaximum(frequencyMap.size());
    progress.setValue(0);
    msgFormat = new MessageFormat(res.getString("Message.NumCategoriesFound"));
    lbl.setText(res.getString("Status.Analyzing"));
    cnt = 0;
    TreeSet<Category> cats = new TreeSet<Category>();
    for (String k : frequencyMap.keySet()) {
      int freq = frequencyMap.get(k).intValue();
      if (freq >= MIN_FREQUENCY) {
        Pattern pattern = new ContainsPattern("Tekst", k, true);
        cats.add(new Category(String.format("%s (%03d): ", k, freq), pattern));
      }
      progress.setValue(++cnt);
      args[0] = new Integer(cats.size());
      numCats.setText(msgFormat.format(args));
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          dialog.repaint();
        }
      });
      if (option != JOptionPane.OK_OPTION) break;
    }
    categories = new ArrayList<Category>(cats);
    dialog.setVisible(false);
    // Now show the editor
    CategoryEditor editor = new CategoryEditor(parent, true);
    option = JOptionPane.CANCEL_OPTION;
    PropertyChangeListener propListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if (CategoryEditor.PROP_CATEGORIES_CHANGED.equals(evt.getPropertyName())) {
          categories = (Collection<Category>)evt.getNewValue();
          option = JOptionPane.OK_OPTION;
        }
      }
    };
    editor.addPropertyChangeListener(propListener);
    editor.editCategories(categories);
    return option;
  }
  public Collection<Category> getCategories() {
    return categories;
  }
}
