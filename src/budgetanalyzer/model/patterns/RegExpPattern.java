/*
 * ContainsPattern.java
 *
 * Created on 10. juli 2007, 20:47
 *
 */

package budgetanalyzer.model.patterns;

import budgetanalyzer.model.BankEntry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A simple string-contains pattern.
 * Uses Template Method design pattern.
 * @author Rasmus
 * @version 1.0
 * @see AbstractFieldPattern
 */
public class RegExpPattern extends AbstractFieldPattern {
  /** Creates a new instance of ContainsPattern */
  public RegExpPattern(String fieldId, String exp) {
    super(fieldId, exp);
  }
  
  protected boolean match(Object value) {
    if (value != null) {
      return value.toString().matches(exp);
    }
    return false;
  }
  public int hashCode() {
    int code = 0;
    code ^= fieldId.hashCode();
    code ^= this.exp.hashCode();
    return code;
  }
  public int compareTo(Object o) {
    RegExpPattern other = (RegExpPattern)o;
    int cmp = fieldId.compareTo(other.fieldId);
    if (cmp == 0) {
      cmp = exp.toString().compareTo(other.exp.toString());
    }
    return cmp;
  }
  public boolean equals(Object o) {
    if (!(o instanceof RegExpPattern)) {
      return false;
    }
    return compareTo(o) == 0;
  }
}
