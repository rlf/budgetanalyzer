/*
 * SyntaxErrorException.java
 *
 * Created on 18. juli 2007, 20:47
 *
 */

package budgetanalyzer.io;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class SyntaxErrorException extends Exception {
  protected int lineno;
  /** Creates a new instance of SyntaxErrorException */
  public SyntaxErrorException(int lineno, String msg) {
    super(msg);
    this.lineno = lineno;
  }
  public int getLineNumber() {
    return lineno;
  }
}
