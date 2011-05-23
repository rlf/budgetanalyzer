/*
 * BankEntry.java
 *
 * Created on 10. juli 2007, 19:22
 *
 */

package budgetanalyzer.model;

import java.util.Date;

/**
 * A BankEntry contains all raw data extracted from the account listings.
 * @author Rasmus
 * @version 1.0
 */
public class BankEntry implements Comparable<BankEntry> {
  private Date date;
  private String text;
  private float value;
  private Category category;
  
  /** Clone constructor. */
  public BankEntry(BankEntry be) {
    this.date = be.date;
    this.text = be.text;
    this.value = be.value;
    this.category = be.category;
  }
  public BankEntry(Date date, String text, float value) {
    this(date, text, value, null);
  }
  /** Creates a new instance of BankEntry */
  public BankEntry(Date date, String text, float value, Category category) {
    this.date = date;
    this.text = text;
    this.value = value;
    this.category = category;
  }
  
  public Date getDate() {
    return date;
  }
  
  public String getText() {
    return text;
  }
  
  public float getValue() {
    return value;
  }
  
  public void setCategory(Category c) {
    this.category = c;
  }
  public Category getCategory() {
    return category;
  }
  public int hashCode() {
    int code = date.hashCode();
    code ^= text.hashCode();
    code ^= new Float(value).hashCode();
    return code;
  }
  public int compareTo(BankEntry other) {
    int res = date.compareTo(other.date);
    if (res == 0) {
      res = text.compareTo(other.text);
    }
    if (res == 0) {
      res = (int)(value - other.value);
    }
    return res;
  }
  public boolean equals(Object o) {
    if (!(o instanceof BankEntry)) {
      return false;
    }
    return compareTo((BankEntry)o) == 0;
  }
}
