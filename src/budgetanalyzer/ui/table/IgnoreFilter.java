/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package budgetanalyzer.ui.table;

import budgetanalyzer.model.Category;
import dk.lockfuglsang.rasmus.util.ui.table.filters.Filter;

/**
 * Ignores rows whose category starts with a '-'
 */
public class IgnoreFilter extends Filter {

  @Override
  public boolean match(Object o) {
    if (o == null) {
      return true;
    }
    if (o instanceof Category) {
      return !((Category)o).getName().startsWith("-");
    }
    return false;
  }
  
}
