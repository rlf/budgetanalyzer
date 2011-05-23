/*
 * Category.java
 *
 * Created on 10. juli 2007, 21:19
 *
 */

package budgetanalyzer.model;

import java.io.Serializable;

import budgetanalyzer.model.patterns.Pattern;

/**
 * Categories are matched dynamically.
 * @author Rasmus
 * @version 1.0
 */
public class Category implements Serializable, Comparable<Object> {
  protected String name;
  protected Pattern pattern;
  
  /** Creates a new instance of Category */
  public Category(String name, Pattern pattern) {
    this.name = name;
    this.pattern = pattern;
  }
  public boolean isCategory(Object o) {
    pattern.visit(o);
    return pattern.matches();
  }
  public String toString() {
    return "Category[name=" + getName() + ",pattern=" + pattern + "]";
  }
  public String getName() {
    return name;
  }
  public Pattern getPattern() {
    return pattern;
  }
  
  public int hashCode() {
    int code = 0;
    code ^= name.hashCode();
    code ^= pattern.hashCode();
    return code;
  }
  public int compareTo(Object o) {
    Category other = (Category)o;
    int cmp = 0;
    cmp = name.compareTo(other.name);
    if (cmp == 0) {
      try {
        cmp = pattern.compareTo(other.pattern);
      } catch (ClassCastException e) {
        // Note: This doesn't 100% comply with the compareTo scheme
        cmp = e.getMessage().hashCode();
      }
    }
    return cmp;
  }
  public boolean equals(Object o) {
    if (!(o instanceof Category)) {
      return false;
    }
    return compareTo(o) == 0;
  }
}
