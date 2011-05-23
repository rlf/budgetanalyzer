package budgetanalyzer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class BankEntryTableModel extends AbstractTableModel implements TableModel {
  
  private List<BankEntry> entries;
  private List<String> columnIdentifiers;

  public BankEntryTableModel(List<String> columnIdentifiers) {
    entries = new ArrayList<BankEntry>();
    this.columnIdentifiers = new ArrayList<String>(columnIdentifiers);
  }

  public void setDataVector(List<BankEntry> dataVector, List<String> columnIdentifiers) {
    this.columnIdentifiers = new ArrayList<String>(columnIdentifiers);
    this.entries = dataVector;
  }
  @Override
  public String getColumnName(int column) {
    return columnIdentifiers.get(column);
  }
  @Override
  public Object getValueAt(int row, int column) {
    return getValue(entries.get(row), column);
  }
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0: return Date.class;
      case 1: return String.class;
      case 2: return Float.class;
      case 3: return Category.class;
      default: throw new IndexOutOfBoundsException("Column MUST be in the range [0..3]");
    }
  }
  
  private Object getValue(BankEntry be, int column) {
    switch (column) {
      case 0: return be.getDate();
      case 1: return be.getText();
      case 2: return be.getValue();
      case 3: return be.getCategory();
      default: throw new IndexOutOfBoundsException("Column MUST be in the range [0..3]");
    }
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public synchronized int getRowCount() {
    return entries.size();
  }
  
  public synchronized void addAll(Collection<BankEntry> list) {
    int firstRow = entries.size();
    entries.addAll(list);
    entries = new ArrayList<BankEntry>(new TreeSet<BankEntry>(entries));
    int lastRow = entries.size();
    fireTableRowsInserted(firstRow, lastRow);
  }
  
  public Iterator<BankEntry> iterator() {
    List<BankEntry> copy = new ArrayList<BankEntry>(entries);
    Collections.sort(copy);
    return copy.iterator();
  }
  public synchronized List<BankEntry> getDataVector() {
    return new ArrayList<BankEntry>(entries);
  }
  public synchronized void clear() {
    entries.clear();
    fireTableStructureChanged();
  }
  public synchronized BankEntry removeRow(int rowIndex) {
    BankEntry entry = entries.remove(rowIndex);
    fireTableRowsDeleted(rowIndex, rowIndex);
    return entry;
  }
}