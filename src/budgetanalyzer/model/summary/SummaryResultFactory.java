/*
 * SummaryResultFactory.java
 *
 * Created on 12. juli 2007, 19:49
 *
 */

package budgetanalyzer.model.summary;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import budgetanalyzer.model.BankEntry;
import budgetanalyzer.model.Category;
import budgetanalyzer.ui.resources.Resource;

/**
 *
 * @author Rasmus
 * @version 1.0
 * TODO: Add support for generic summaryresult (refactor the addMonthly etc.)
 */
public class SummaryResultFactory {
  private static final Resource RES = new Resource("SummaryResultFactory");
  private static final String ALL = RES.getString("All");
  private static final String UNKNOWN = RES.getString("Unknown");
  private static final String DELIM = RES.getString("Delimiter");
  
  /** Creates a new instance of SummaryResultFactory */
  private SummaryResultFactory() {
  }
  
  static public Set<SummaryResult> createSummary(
      java.util.Iterator<BankEntry> entries,
      Collection<Category> categories) {
    Map<String, SummaryResult> map = new HashMap<String,SummaryResult>();
    SummaryResult sr = new SummaryResult(UNKNOWN);
    map.put(UNKNOWN, sr);
    sr = new SummaryResult(ALL);
    map.put(ALL, sr);
    
    boolean matched = false;
    for (BankEntry be = null; entries.hasNext(); ) {
      be = entries.next();
      be.setCategory(null);
      matched = false;
      for (Category c : categories) {
        if (c.isCategory(be)) {
          be.setCategory(c);
          add(be, map);
          matched = true;
          break; // The category was matched!
        }
      }
      map.get(ALL).addToSum(be.getDate(), be.getValue());
      if (!matched) {
        map.get(UNKNOWN).addToSum(be.getDate(), be.getValue());
      }
    }
    return new SummarySet<SummaryResult>(map.values());
  }
  /** Adds the value of the {@link BankEntry} to the {@link SummaryResult}s in the Map.
   * 
   * @param be  The bank entry to add to the map.
   * @param map The map to hold the {@link SummaryResult}.
   */
  static private void add(BankEntry be, Map<String, SummaryResult> map) {
    String name = be.getCategory().getName();
    add(be, name, map);
    int ix = name.indexOf(DELIM);
    while (ix != -1) {
      add(be, name.substring(0, ix), map);
      ix = name.indexOf(DELIM, ix+1);
    }
  }
  /** Adds the value of the {@link BankEntry} to the {@link SummaryResult}s in the Map.
   * 
   * @param be  The bank entry to add to the map.
   * @param map The map to hold the {@link SummaryResult}.
   */
  static private void addMonthly(BankEntry be, Map<String, MonthlySummaryResult> map) {
    String name = be.getCategory().getName();
    addMonthly(be, name, map);
    int ix = name.indexOf(DELIM);
    while (ix != -1) {
      addMonthly(be, name.substring(0, ix), map);
      ix = name.indexOf(DELIM, ix+1);
    }
  }
  
  static private void addQuarterly(BankEntry be, Map<String, SummaryResult> map) {
    String name = be.getCategory().getName();
    addQuarterly(be, name, map);
    int ix = name.indexOf(DELIM);
    while (ix != -1) {
      addQuarterly(be, name.substring(0, ix), map);
      ix = name.indexOf(DELIM, ix+1);
    }
  }
  
  static private void add(BankEntry be, String key, Map<String, SummaryResult> map) {
    SummaryResult sr = map.get(key);
    if (sr == null) {
      sr = new SummaryResult(key);
      map.put(key, sr);
    }
    map.get(key).addToSum(be.getDate(), be.getValue());
  }
  
  static private void addMonthly(BankEntry be, String key, Map<String, MonthlySummaryResult> map) {
    MonthlySummaryResult sr = map.get(key);
    if (sr == null) {
      sr = new MonthlySummaryResult(key);
      map.put(key, sr);
    }
    map.get(key).addToSum(be.getDate(), be.getValue());
  }
  
  static private void addQuarterly(BankEntry be, String key, Map<String, SummaryResult> map) {
    SummaryResult sr = map.get(key);
    if (sr == null) {
      sr = new QuarterlySummaryResult(key);
      map.put(key, sr);
    }
    map.get(key).addToSum(be.getDate(), be.getValue());
  }
  
  static public Set<MonthlySummaryResult> createMonthlySummary(
      Iterator<BankEntry> iterator,
      Collection<Category> categories) {
    Map<String, MonthlySummaryResult> map = new HashMap<String,MonthlySummaryResult>();
    MonthlySummaryResult sr = new MonthlySummaryResult(UNKNOWN);
    map.put(UNKNOWN, sr);
    sr = new MonthlySummaryResult(ALL);
    map.put(ALL, sr);
    
    boolean matched = false;
    for (BankEntry be = null; iterator.hasNext(); ) {
      be = iterator.next();
      be.setCategory(null);
      matched = false;
      for (Category c : categories) {
        if (c.isCategory(be)) {
          be.setCategory(c);
          addMonthly(be, map);
          matched = true;
          break; // The category was matched!
        }
      }
      map.get(ALL).addToSum(be.getDate(), be.getValue());
      if (!matched) {
        map.get(UNKNOWN).addToSum(be.getDate(), be.getValue());
      }
    }
    return new SummarySet<MonthlySummaryResult>(map.values());
  }
  
  static public Set<SummaryResult> createQuarterlySummary(
      Iterator<BankEntry> iterator,
      Collection<Category> categories) {
    Map<String, SummaryResult> map = new HashMap<String,SummaryResult>();
    SummaryResult sr = new QuarterlySummaryResult(UNKNOWN);
    map.put(UNKNOWN, sr);
    sr = new QuarterlySummaryResult(ALL);
    map.put(ALL, sr);
    
    boolean matched = false;
    for (BankEntry be = null; iterator.hasNext(); ) {
      be = iterator.next();
      be.setCategory(null);
      matched = false;
      for (Category c : categories) {
        if (c.isCategory(be)) {
          be.setCategory(c);
          addQuarterly(be, map);
          matched = true;
          break; // The category was matched!
        }
      }
      map.get(ALL).addToSum(be.getDate(), be.getValue());
      if (!matched) {
        map.get(UNKNOWN).addToSum(be.getDate(), be.getValue());
      }
    }
    return new SummarySet<SummaryResult>(map.values());
  }
  
  public static class SummarySet<T extends SummaryResult> extends TreeSet<T> {
    public SummarySet(Collection<T> collection) {
      super(new SummaryComparator<T>());
      addAll(collection);
    }
  }
  public static class SummaryComparator<T extends SummaryResult> implements Comparator<T> {
    public int compare(SummaryResult o1, SummaryResult o2) {
      int cmp = o1.getName().compareTo(o2.getName());
      if (cmp != 0) {
        if (o1.getName().equals(ALL)) {
          // ALL is bigger than everything else!
          cmp = 2;
        } else if (o2.getName().equals(ALL)) {
          cmp = -2;
        } else if (o1.getName().equals(UNKNOWN)) {
          cmp = 1;
        } else if (o2.getName().equals(UNKNOWN)) {
          cmp = -1;
        }
      }
      return cmp;
    }
  }
}
