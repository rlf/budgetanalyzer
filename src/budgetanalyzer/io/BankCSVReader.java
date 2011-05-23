/*
 * BankCSVReader.java
 *
 * Created on 24. juli 2007, 17:45
 *
 */

package budgetanalyzer.io;

import budgetanalyzer.model.BankEntry;

/**
 * Interface defining how to read specific Bank CSV files.
 *
 * @author Rasmus
 * @version 1.0
 */
public interface BankCSVReader {
  /** Whether or not this reader can read the file. 
   * 
   * @param firstLine The first line of the CSV file.
   * @return <code>true</code> iff the reader can recognise the file-format.
   */
  boolean canRead(String firstLine);
  BankEntry readEntry(String line);
}
