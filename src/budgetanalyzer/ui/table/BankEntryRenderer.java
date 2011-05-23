/*
 * BankEntryRenderer.java
 *
 * Created on 17. juli 2007, 20:40
 *
 */

package budgetanalyzer.ui.table;

import budgetanalyzer.ui.resources.Resource;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class BankEntryRenderer extends EntryRenderer {
  static final public int COL_CATEGORY = 3;
  protected Color bgColEvenCategorized;
  protected Color bgColUnevenCategorized;
  
  /** Creates a new instance of BankEntryRenderer */
  public BankEntryRenderer(JTable table) {
    super(table);
  }
  
  /** Creates a new instance of BankEntryRenderer */
  public BankEntryRenderer(JTable table, Resource res) {
    super(table, res);
  }
  
  public void setColors(Resource res, String prefix) {
    super.setColors(res, prefix);
    bgColEvenCategorized = res.createColor(prefix + "Bg.Color.Categorized.Even");
    bgColUnevenCategorized = res.createColor(prefix + "Bg.Color.Categorized.Uneven");
  }
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    boolean isCategorized = table.getModel().getValueAt(row, COL_CATEGORY) != null;
    if (isCategorized && !isSelected) {
      Color bgCol = c.getBackground();
      if (row % 2 == 0) {
        bgCol = bgColEvenCategorized;
      } else {
        bgCol = bgColUnevenCategorized;
      }
      c.setBackground(bgCol);
    }
    return c;
  }
}
