/*
 * LineReader.java
 *
 * Created on 18. juli 2007, 20:50
 *
 */

package budgetanalyzer.io;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Decorator for the BufferedReader for counting the lines read.
 * @author Rasmus
 * @version 1.0
 */
public class LineReader extends BufferedReader {
  protected int lineNumber;
  /** Creates a new instance of LineReader */
  public LineReader(BufferedReader rdr) {
    super(rdr);
    lineNumber = 1;
  }

  public String readLine() throws IOException {
    String s = super.readLine();
    lineNumber++;
    return s;
  }
  public int getLineNumber() {
    return lineNumber;
  }
}
