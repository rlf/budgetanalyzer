/*
 * OrPattern.java
 *
 * Created on 10. juli 2007, 21:16
 *
 */

package budgetanalyzer.model.patterns;

import java.util.Collection;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class OrPattern extends CompositePattern {
  
  /** Creates a new instance of OrPattern */
  public OrPattern() {
  }
  public OrPattern(Collection<Pattern> children) {
    super(children);
  }

  public boolean matches() {
    boolean m = false;
    for (Pattern p : children) {
      m |= p.matches();
    }
    return m;
  }
  
}
