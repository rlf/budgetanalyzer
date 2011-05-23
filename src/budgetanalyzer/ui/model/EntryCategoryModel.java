/*
 * EntryCategoryModel.java
 *
 * Created on 12. juli 2007, 19:39
 *
 */

package budgetanalyzer.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import budgetanalyzer.model.BankEntry;
import budgetanalyzer.model.BankEntryTableModel;
import budgetanalyzer.model.Category;
import budgetanalyzer.model.summary.MonthlySummaryResult;
import budgetanalyzer.model.summary.SummaryResult;
import budgetanalyzer.model.summary.SummaryResultFactory;
import dk.lockfuglsang.rasmus.util.ui.UIModel;

/**
 * The EntryCategoryModel contains all logic linking a tablemodel of entries
 * with a set of categories.
 *
 * @author Rasmus
 * @version 1.0
 * @see Category
 * @see BankEntry
 */
public class EntryCategoryModel extends UIModel {
  static public final String PROP_CHANGED = "CHANGED";
  private Collection<Category> categories;
  private Set<SummaryResult> summary;
  private Set<MonthlySummaryResult> monthlySummary;
  private BankEntryTableModel tableModel;
  
  /** Creates a new instance of EntryCategoryModel */
  public EntryCategoryModel(java.util.List<String> colIds) {
    categories = new ArrayList<Category>();
    summary = null;
    monthlySummary = null;
    tableModel = new BankEntryTableModel(colIds);
    tableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.INSERT ||
            e.getType() == TableModelEvent.UPDATE)
          dirty();
      }
    });
  }
  public BankEntryTableModel getEntryTableModel() {
    return tableModel;
  }
  public void addEntries(Collection<BankEntry> lst) {
    tableModel.addAll(lst);
  }
  public void setCategories(Collection<Category> lst) {
    categories = new ArrayList<Category>(lst);
    changed();
  }
  
  public void addCategories(Collection<Category> lst) {
    categories.addAll(lst);
    changed();
  }
  public void addCategory(Category c) {
    categories.add(c);
    changed();
  }
  public void removeCategory(Category c) {
    categories.remove(c);
    changed();
  }
  public Collection<Category> getCategories() {
    return categories;
  }
  public Set<SummaryResult> getSummary(Iterator<BankEntry> iterator) {
    summary = SummaryResultFactory.createSummary(iterator, categories);
    return summary;
  }
  public Set<MonthlySummaryResult> getMonthlySummary(Iterator<BankEntry> iterator) {
    monthlySummary = SummaryResultFactory.createMonthlySummary(iterator, categories);
    return monthlySummary;
  }
  
  public Set<SummaryResult> getQuaterlySummary(Iterator<BankEntry> iterator) {
    return SummaryResultFactory.createQuarterlySummary(iterator, categories);
  }
  
  private void changed() {
    dirty();
    firePropertyChangeEvent(PROP_CHANGED, null);
  }
  private void dirty() {
    monthlySummary = null;
    summary = null;
  }
}
