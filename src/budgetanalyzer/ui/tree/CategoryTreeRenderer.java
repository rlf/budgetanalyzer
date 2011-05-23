/*
 * CategoryTreeRenderer.java
 *
 * Created on 21. juli 2007, 19:14
 *
 */

package budgetanalyzer.ui.tree;

import budgetanalyzer.io.CategoryWriter;
import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.AndPattern;
import budgetanalyzer.model.patterns.CompositePattern;
import budgetanalyzer.model.patterns.NotPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.resources.Resource;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class CategoryTreeRenderer extends DefaultTreeCellRenderer {
  static final Resource RES = new Resource("CategoryTreeRenderer");
  /** Creates a new instance of CategoryTreeRenderer */
  public CategoryTreeRenderer() {
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded,
        leaf, row, hasFocus);
    if (value instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode n = (DefaultMutableTreeNode)value;
      Object o = n.getUserObject();
      String s = RES.getString(o.getClass().getName(), (String)null);
      if (s == null) {
        if (o instanceof Category) {
          s = ((Category)o).getName();
        } else if (o instanceof CompositePattern) {
          // Shouldn't happen
        } else if (o instanceof Pattern) {
          s = CategoryWriter.toString((Pattern)o);
        }
      }
      if (s != null) {
        this.setText(s);
      }
    }
    return c;
  }
  
}
