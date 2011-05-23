/*
 * CSVWriter.java
 *
 * Created on 11. juli 2007, 22:48
 *
 */

package budgetanalyzer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.table.TableModel;

import budgetanalyzer.model.BankEntry;
import budgetanalyzer.model.Category;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class CSVWriter {
  static final protected String C = ";";
  static final protected String N = "\n";
  static final public SimpleDateFormat DATETIME = new SimpleDateFormat("dd-MM-yyyy");
  private File file;
  /** Creates a new instance of CSVWriter */
  public CSVWriter(File f) {
    this.file = f;
  }
  public void writeEntries(Iterator<BankEntry> entries) {
    try {
      PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      writer.println("Dato;Tekst;Beløb;Category");
      for (;entries.hasNext();) {
        BankEntry be = entries.next();
        writer.print(DATETIME.format(be.getDate()) + ";");
        writer.print(be.getText() + ";");
        writer.print(floatToString(be.getValue()) + ";");
        Category cat = be.getCategory();
        writer.print((cat != null ? cat.getName() : "-"));
        writer.println();
      }
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  public void writeEntries(Collection<BankEntry> entries) {
    writeEntries(entries.iterator());
  }
  /** Stuffs a float with dots and commas. */
  private String floatToString(float f) {
    String s = "" + f;
    s = s.replaceAll("\\.", ",");
    String fraction = "";
    int ix = s.indexOf(",");
    if (ix != -1) {
      fraction = s.substring(ix);
      s = s.substring(0,ix);
    }
    ix = s.length();
    while (ix > 3) {
      s = s.substring(0, ix-3) + "." + s.substring(ix-3);
      ix -= 3;
    }
    return s + fraction;
  }
  static public void writeTableModel(OutputStream os, TableModel model) {
    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
    int nrows = model.getRowCount() - 1;
    int ncols = model.getColumnCount() - 1;
    for (int i = 0; i <= ncols; i++) {
      out.print(model.getColumnName(i));
      if (i < ncols) {
        out.print(C);
      }
    }
    out.print(N);
    for (int row = 0; row <= nrows; row++) {
      for (int col = 0; col <= ncols; col++) {
        out.print(model.getValueAt(row, col));
        if (col < ncols) {
          out.print(C);
        }
      }
      out.print(N);
    }
    out.flush();
    out.close();
  }
}