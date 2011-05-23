package budgetanalyzer.ui.table;

import java.util.Iterator;

import javax.swing.table.TableModel;

import dk.lockfuglsang.rasmus.util.ui.table.TableModelFilter;

import budgetanalyzer.model.BankEntry;
import budgetanalyzer.model.BankEntryTableModel;

public class TableModelBankEntryIterator implements Iterator<BankEntry> {
  private TableModelFilter filteredModel;
  private int row;
  
  public TableModelBankEntryIterator(TableModelFilter filteredModel) {
    this.filteredModel = filteredModel;
    this.row = 0;
  }
  @Override
  public boolean hasNext() {
    return row < filteredModel.getRowCount();
  }
  
  @Override
  public BankEntry next() {
    TableModel mdl = filteredModel.getModel();
    int realRow = filteredModel.modelIndex(row);
    while (mdl instanceof TableModelFilter) {
      realRow = ((TableModelFilter)mdl).modelIndex(realRow);
      mdl = ((TableModelFilter)mdl).getModel();
    }
    row++;
    BankEntry be = null;
    if (mdl instanceof BankEntryTableModel) {
      be = ((BankEntryTableModel)mdl).getDataVector().get(realRow);
    } else {
      throw new IllegalStateException("Expected BankEntryTableModel got " + mdl);
    }
    return be;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove not supported on TableModeBankEntryIterator");
  }
}
