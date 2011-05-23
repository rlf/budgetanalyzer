/*
 * CategoryTransferHandler.java
 *
 * Created on 21. juli 2007, 20:55
 *
 */

package budgetanalyzer.ui.model;

import budgetanalyzer.io.CategoryReader;
import budgetanalyzer.io.CategoryWriter;
import budgetanalyzer.io.LineReader;
import budgetanalyzer.io.SyntaxErrorException;
import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.CompositePattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.tree.CategoryTree;
import dk.lockfuglsang.rasmus.util.datatransfer.StringTransferHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class CategoryTransferHandler extends StringTransferHandler {
  /** The node being dragged. */
  DefaultMutableTreeNode node;
  DefaultMutableTreeNode parent;
  
  /** Creates a new instance of CategoryTransferHandler */
  public CategoryTransferHandler() {
  }
  
  protected String exportString(JComponent c) {
    String s = null;
    CategoryTree tree = (CategoryTree)c;
    DefaultMutableTreeNode n = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if (n != null) {
      node = n;
      parent = (DefaultMutableTreeNode)node.getParent();
      Object o = n.getUserObject();
      if (o instanceof Category) {
        s = CategoryWriter.toString((Category)o);
      } else if (o instanceof Pattern) {
        s = CategoryWriter.toString((Pattern)o);
      }
    }
    return s;
  }
  
  protected void importString(JComponent c, String str) {
    CategoryTree tree = (CategoryTree)c;
    DefaultMutableTreeNode n = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    Pattern pattern = null;
    Category cat = readCategory(str);
    if (cat == null) {
      pattern = readPattern(str);
    }
    if (n != null) {
      if (cat != null && !insertCategory(tree, cat, n)) {
        node = null;
      } else if (pattern != null && !insertPattern(tree, pattern, n)) {
        node = null;
      }
    } else {
      // Make sure the move is not in effect
      node = null;
    }
  }
  private Category readCategory(String str) {
    Category cat = null;
    StringReader sr = new StringReader(str);
    try {
      Collection<Category> cats = CategoryReader.readCategories(new LineReader(new BufferedReader(sr)));
      if (cats.size() == 1) {
        cat = cats.iterator().next();
      }
    } catch (SyntaxErrorException ex) {
      // Ignore
    } catch (IOException ex) {
      // Ignore
    }
    return cat;
  }
  private Pattern readPattern(String str) {
    StringReader sr = new StringReader(str);
    Pattern pattern = null;
    try {
      pattern = CategoryReader.readPattern(new LineReader(new BufferedReader(sr)));
    } catch (SyntaxErrorException ex) {
      // Ignore
    } catch (IOException ex) {
      // Ignore
    }
    return pattern;
  }
  private boolean insertPattern(CategoryTree tree, Pattern pattern, DefaultMutableTreeNode n) {
    boolean result = true;
    DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
    Object o = n.getUserObject();
    if (o instanceof Category) {
      if (n.getFirstChild() == null) {
        model.insertNodeInto(tree.getPatternAsNode(pattern), n, 0);
      } else {
        result = insertPattern(tree, pattern, (DefaultMutableTreeNode)n.getFirstChild());
      }
    } else if (o instanceof CompositePattern) {
      model.insertNodeInto(tree.getPatternAsNode(pattern), n, n.getChildCount());
    } else if (o instanceof Pattern) {
      // Wrap in an OR pattern... unless the parent is already a composite...
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode)n.getParent();
      Object po = parent.getUserObject();
      if (po instanceof CompositePattern) {
        int ix = parent.getIndex(n);
        model.insertNodeInto(tree.getPatternAsNode(pattern), parent, ix+1);
      } else {
        OrPattern composite = new OrPattern();
        n.setUserObject(composite);
        composite.addPattern((Pattern)o);
        composite.addPattern(pattern);
        model.insertNodeInto(tree.getPatternAsNode((Pattern)o), n, 0);
        model.insertNodeInto(tree.getPatternAsNode(pattern), n, 1);
      }
    } else {
      result = false;
    }
    return result;
  }
  protected boolean insertCategory(CategoryTree tree, Category cat, DefaultMutableTreeNode target) {
    boolean result = true;
    DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
    Object o = target.getUserObject();
    if (o instanceof Category) {
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) target.getParent();
      int ix = model.getIndexOfChild(parent, target);
      model.insertNodeInto(tree.getCategoryAsNode(cat), parent, ix+1);
    } else {
      result = false;
    }
    return result;    
  }
  
  protected void cleanup(JComponent c, boolean remove) {
    CategoryTree tree = (CategoryTree)c;
    if (remove && node != null) {
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      model.removeNodeFromParent(node);
      node = null;
    }
  }
}
