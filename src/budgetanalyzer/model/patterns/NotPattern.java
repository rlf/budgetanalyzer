/*
 * NotPattern.java
 *
 * Created on 10. juli 2007, 21:07
 *
 */

package budgetanalyzer.model.patterns;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class NotPattern implements Pattern {
  private Pattern parent;
  /** Creates a new instance of NotPattern */
  public NotPattern(Pattern parent) {
    this.parent = parent;
  }
  
  public boolean matches() {
    return !parent.matches();
  }
  
  public void visit(Object o) {
    parent.visit(o);
  }
  public Pattern getPattern() {
    return parent;
  }
  
  public int hashCode() {
    int code = 0;
    code ^= parent.hashCode();
    return code;
  }
  public int compareTo(Object o) {
    NotPattern other = (NotPattern)o;
    int cmp = parent.compareTo(other.parent);
    return cmp;
  }
  public boolean equals(Object o) {
    if (!(o instanceof NotPattern)) {
      return false;
    }
    return compareTo(o) == 0;
  }
}
