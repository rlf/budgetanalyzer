/*
 * CategoryBankEntry.java
 *
 * Created on 16. juli 2007, 22:51
 *
 */

package budgetanalyzer.model;

/**
 * The CategoryBankEntry is a Decorator of a BankEntry.
 * @author Rasmus
 * @version 1.0
 */
public class CategoryBankEntry extends BankEntry {
  
  private Category category;
  
  /** Creates a new instance of CategoryBankEntry */
  public CategoryBankEntry(BankEntry be, Category category) {
    super(be);
    this.category = category;
  }
  
  public void setCategory(Category c) {
    this.category = c;
  }
  public Category getCategory() {
    return category;
  }
  
}
