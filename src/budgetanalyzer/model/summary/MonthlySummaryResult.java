/*
 * MonthlySummaryResult.java
 *
 * Created on 17. juli 2007, 21:04
 *
 */

package budgetanalyzer.model.summary;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import budgetanalyzer.ui.resources.Resource;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class MonthlySummaryResult extends SummaryResult {
  static final Resource RES = new Resource("SummaryResultFactory");
  public static final SimpleDateFormat MONTH = new SimpleDateFormat(RES.getString("MonthlySummary.DataFormat.Month"));
  
  Map<String,SummaryResult> monthlySummary;
  
  public MonthlySummaryResult(String name) {
    this(name, 0, 0);
  }
  /** Creates a new instance of SummaryResult */
  public MonthlySummaryResult(String name, int count, float sum) {
    super(name, count, sum);
    monthlySummary = new TreeMap<String,SummaryResult>();
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
    return "MonthlySummaryResult['" + name + "', count=" + count + ",sum=" + sum + "]";
  }
  
  public void addToSum(Date d, float value) {
    super.addToSum(d, value);
    String k = MONTH.format(d);
    if (!monthlySummary.containsKey(k)) {
      monthlySummary.put(k, new SummaryResult(k));
    }
    monthlySummary.get(k).addToSum(d, value);
  }
  public Collection<SummaryResult> getSummary() {
    return monthlySummary.values();
  }
}
