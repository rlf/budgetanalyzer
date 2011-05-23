/*
 * CompositePattern.java
 *
 * Created on 10. juli 2007, 20:43
 *
 */

package budgetanalyzer.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The abstract composite pattern.
 * @author Rasmus
 * @version 1.0
 */
public abstract class CompositePattern implements Pattern {
  protected List<Pattern> children;
  
  public CompositePattern() {
    children = new ArrayList<Pattern>();
  }
  public CompositePattern(Collection<Pattern> children) {
    this.children = new ArrayList<Pattern>(children);
  }
  public void addPattern(Pattern pattern) {
    children.add(pattern);
  }
  public final void visit(Object o) {
    for (Iterator<Pattern> it = children.iterator(); it.hasNext(); ) {
      it.next().visit(o);
    }
  }
  public Collection<Pattern> getChildren() {
    return children;
  }
  public int hashCode() {
    int code = 0;
    code ^= children.size();
    code ^= this.getClass().hashCode();
    return code;
  }
  public int compareTo(Object o) {
    CompositePattern other = (CompositePattern)o;
    if (!(o.getClass().equals(this.getClass()))) {
      throw new ClassCastException("Expected " + this.getClass().getName() +
          " got " + o.getClass().getName());
    }
    int cmp = children.size() - other.children.size();
    if (cmp == 0) {
      for (int i = 0; cmp == 0 && i < children.size(); i++) {
        cmp = children.get(i).compareTo(other.children.get(i));
      }
    }
    return cmp;
  }
  public boolean equals(Object o) {
    try {
      return compareTo(o) == 0;
    } catch (ClassCastException e) {
      // Fall through
    }
    return false;
  }
  
}
