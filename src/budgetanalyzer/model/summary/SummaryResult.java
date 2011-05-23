/*
 * SummaryResult.java
 *
 * Created on 12. juli 2007, 19:45
 *
 */

package budgetanalyzer.model.summary;

import budgetanalyzer.model.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * A SummaryResult is a mapping between a category and a collection of BankEntries.
 * @author Rasmus
 * @version 1.0
 * @see BankEntry
 * @see Category
 */
public class SummaryResult implements Comparable<Object> {
  protected String name;
  protected int count;
  protected float sum;
  
  public SummaryResult(String name) {
    this(name, 0, 0);
  }
  /** Creates a new instance of SummaryResult */
  public SummaryResult(String name, int count, float sum) {
    this.name = name;
    this.count = count;
    this.sum = sum;
  }

  public String getName() {
    return name;
  }

  public int getCount() {
    return count;
  }

  public float getSum() {
    return sum;
  }
  public void addToSum(Date d, float value) {
    count++;
    sum += value;
  }

  public int hashCode() {
    int code = name.hashCode();
    code ^= new Integer(count).hashCode();
    code ^= new Float(sum).hashCode();
    return code;
  }
  public boolean equals(Object o) {
    if (!(o instanceof SummaryResult)) {
      return false;
    }
    return compareTo(o) == 0;
  }
  public int compareTo(Object o) {
    SummaryResult other = (SummaryResult)o;
    int cmp = name.compareTo(other.name);
    if (cmp == 0) {
      cmp = count - other.count;
    }
    if (cmp == 0) {
      cmp = (int)(sum - other.sum);
    }
    return cmp;
  }
  public String toString() {
    return "SummaryResult['" + name + "', count=" + count + ",sum=" + sum + "]";
  }
  
  public Collection<SummaryResult> getSummary() {
    return Collections.<SummaryResult>emptyList();
  }
}
