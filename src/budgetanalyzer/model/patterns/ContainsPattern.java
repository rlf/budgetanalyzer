/*
 * ContainsPattern.java
 *
 * Created on 10. juli 2007, 20:47
 *
 */

package budgetanalyzer.model.patterns;


/**
 * A simple string-contains pattern.
 * Uses Template Method design pattern.
 * @author Rasmus
 * @version 1.0
 * @see AbstractFieldPattern
 */
public class ContainsPattern extends AbstractFieldPattern {
  private boolean ignoreCase;
 
  /** Creates a new instance of ContainsPattern */
  public ContainsPattern(String fieldId, String exp, boolean ignoreCase) {
    super(fieldId, exp);
    this.ignoreCase = ignoreCase;
    if (ignoreCase) {
      this.exp = this.exp.toLowerCase();
    }
  }
  /** Creates a new instance of ContainsPattern */
  public ContainsPattern(String fieldId, String exp) {
    this(fieldId, exp, false);
  }
  
  protected boolean match(Object value) {
    if (value != null) {
      if (ignoreCase) {
        return value.toString().toLowerCase().contains(this.exp);
      } else {
        return value.toString().contains(this.exp);
      }
    }
    return false;
  }
  public boolean isIgnoreCase() {
    return ignoreCase;
  }
  public String getExpression() {
    return this.exp;
  }

  public int hashCode() {
    int code = 0;
    code ^= fieldId.hashCode();
    code ^= this.exp.hashCode();
    return code;
  }
  public int compareTo(Object o) {
    ContainsPattern other = (ContainsPattern)o;
    int cmp = fieldId.compareTo(other.fieldId);
    if (cmp == 0) {
      cmp = exp.toString().compareTo(other.exp.toString());
    }
    return cmp;
  }
  public boolean equals(Object o) {
    if (!(o instanceof ContainsPattern)) {
      return false;
    }
    return compareTo(o) == 0;
  }
}
