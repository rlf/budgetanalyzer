/*
 * AndPattern.java
 *
 * Created on 10. juli 2007, 21:08
 *
 */

package budgetanalyzer.model.patterns;

import java.util.Collection;

/**
 * A simple and construct.
 * @author Rasmus
 * @version 1.0
 */
public class AndPattern extends CompositePattern {
  
  /** Creates a new instance of AndPattern */
  public AndPattern() {
  }
  public AndPattern(Collection<Pattern> children) {
    super(children);
  }
  
  public boolean matches() {
    boolean result = true;
    for (Pattern p : children) {
      result &= p.matches();
      if (!result) break;
    }
    return result;
  }
}
