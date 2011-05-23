/*
 * CSVReader.java
 *
 * Created on 10. juli 2007, 19:20
 *
 */

package budgetanalyzer.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import budgetanalyzer.io.readers.AbstractBankCSVReader;
import budgetanalyzer.io.readers.AbstractBankCSVReader.ColumnType;
import budgetanalyzer.model.BankEntry;

/**
 * Reads a CSV file into memory.
 * @author Rasmus
 * @version 1.0
 */
public class CSVReader {
  static final List<BankCSVReader> READERS = new ArrayList<BankCSVReader>();
  static {
    Map<AbstractBankCSVReader.ColumnType, Integer> cols = new HashMap<AbstractBankCSVReader.ColumnType, Integer>();
    
    // Sydbank
    cols.put(ColumnType.DATE, 0);
    cols.put(ColumnType.TEXT, 2);
    cols.put(ColumnType.VALUE, 3);
    READERS.add(new AbstractBankCSVReader("Sydbank", "Dato;Valør;Tekst;Beløb;Saldo;Afstemt", cols, 6));
    
    // BudgetAnalyzer
    cols.put(ColumnType.DATE, 0);
    cols.put(ColumnType.TEXT, 1);
    cols.put(ColumnType.VALUE, 2);
    READERS.add(new AbstractBankCSVReader("BudgetAnalyzer", "Dato;Tekst;Beløb;Category", cols, 4));    
    
    // Danske bank
    cols.put(ColumnType.DATE, 0);
    cols.put(ColumnType.TEXT, 1);
    cols.put(ColumnType.VALUE, 2);
    READERS.add(new AbstractBankCSVReader("Danske bank", "\"Dato\";\"Tekst\";\"Beløb\";\"Saldo\";\"Status\"", cols, 5));

    // Nordea
    cols.put(ColumnType.DATE, 0);
    cols.put(ColumnType.TEXT, 1);
    cols.put(ColumnType.VALUE, 3);
    READERS.add(new AbstractBankCSVReader("Nordea", "Bogført;Tekst;Rentedato;Beløb;Saldo", cols, 5));
  }
  static public Collection<BankEntry> getEntries(InputStream is) {
    java.util.List<BankEntry> lst = new ArrayList<BankEntry>();
    try {
      LineReader rdr = new LineReader(new BufferedReader(new InputStreamReader(is)));
      String line = rdr.readLine(); // First line contains headers.
      if ("".equals(line)) {
        line = rdr.readLine();
      }
      BankCSVReader reader = getReader(line);
      if (reader != null) {
        do {
          line = rdr.readLine();
          if (line != null) {
            try {
              lst.add(reader.readEntry(line));
            } catch (IllegalArgumentException e) {
              throw new IllegalArgumentException("" + rdr.getLineNumber() + ": " + e.getMessage(), e);
            }
          }
        } while (line != null);
      } else {
        throw new IllegalArgumentException("File could not be recognized!");
      }
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
    return lst;
  }
  static private BankCSVReader getReader(String firstLine) {
    BankCSVReader reader = null;
    for (Iterator<BankCSVReader> it = READERS.iterator(); reader == null && it.hasNext(); ) {
      BankCSVReader rdr = it.next();
      if (rdr.canRead(firstLine)) {
        reader = rdr;
      }
    }
    return reader;
  }
  static public void addReader(BankCSVReader reader) {
    READERS.add(reader);
  }
  static public List<BankCSVReader> getReaders() {
    return new ArrayList<BankCSVReader>(READERS);
  }
}
