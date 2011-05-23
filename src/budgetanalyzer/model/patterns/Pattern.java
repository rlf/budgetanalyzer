/*
 * Pattern.java
 *
 * Created on 10. juli 2007, 20:39
 *
 */

package budgetanalyzer.model.patterns;

import java.io.Serializable;

/**
 * The interface for a pattern.
 *
 * Note: Uses a Visitor pattern.
 * @author Rasmus
 * @version 1.0
 */
public interface Pattern extends Serializable, Comparable<Object> {
  boolean matches();
  void visit(Object o);
}
