/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package budgetanalyzer.model.summary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import budgetanalyzer.ui.resources.Resource;

/**
 *
 * @author Rasmus
 */
public class QuarterlySummaryResult extends SummaryResult {
  
  Map<String,SummaryResult> summary;
  
  public QuarterlySummaryResult(String name) {
    this(name, 0, 0);
  }
  /** Creates a new instance of SummaryResult */
  public QuarterlySummaryResult(String name, int count, float sum) {
    super(name, count, sum);
    summary = new TreeMap<String,SummaryResult>();
  }
  
  public int hashCode() {
    int code = super.hashCode();
    return code;
  }
  public boolean equals(Object o) {
    if (!(o instanceof MonthlySummaryResult)) {
      return false;
    }
    return compareTo(o) == 0;
  }
  public int compareTo(Object o) {
    MonthlySummaryResult other = (MonthlySummaryResult)o;
    int cmp = super.compareTo(other);
    return cmp;
  }
  public String toString() {
    return "QuarterlySummaryResult['" + name + "', count=" + count + ",sum=" + sum + "]";
  }
  
  public void addToSum(Date d, float value) {
    super.addToSum(d, value);
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    String k = String.format("%4dQ%1d", cal.get(Calendar.YEAR), ((cal.get(Calendar.MONTH)+1)/4)+1);
    if (!summary.containsKey(k)) {
      summary.put(k, new SummaryResult(k));
    }
    summary.get(k).addToSum(d, value);
  }
  public Collection<SummaryResult> getSummary() {
    return summary.values();
  }
}
