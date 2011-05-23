/*
 * CategoryTree.java
 *
 * Created on 19. juli 2007, 18:04
 *
 */
package budgetanalyzer.ui.tree;

import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.AbstractFieldPattern;
import budgetanalyzer.model.patterns.CompositePattern;
import budgetanalyzer.model.patterns.NotPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.ui.PatternEditor;
import budgetanalyzer.ui.model.CategoryTransferHandler;
import budgetanalyzer.ui.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.tree.TreePopupListener;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class CategoryTree extends JTree {

  static final protected Resource res = new Resource("CategoryTree");
  protected MutableTreeNode selected;
  protected DefaultTreeModel model;

  /** Creates a new instance of CategoryTree */
  public CategoryTree() {
    super();
    MutableTreeNode root = new DefaultMutableTreeNode("/");
    model = new DefaultTreeModel(root);
    setModel(model);
    final JTree tree = this;
    addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        selected = (MutableTreeNode) tree.getLastSelectedPathComponent();
      }
    });
    final JPopupMenu popup = new JPopupMenu();
    final JMenu rootPopup = res.createJMenu("Menu.Tree");
    Action delAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        MutableTreeNode n = (MutableTreeNode) tree.getLastSelectedPathComponent();
        model.removeNodeFromParent(n);
      }
    };
    res.prepareAction(delAction, "Menu.TreeNode.Delete");
    final JMenuItem delete = new JMenuItem(delAction);
    final JMenuItem rename = res.createJMenuItem("Menu.TreeNode.Rename");
    rename.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        rename(n);
      }
    });
    final JMenuItem edit = res.createJMenuItem("Menu.TreeNode.Edit");
    edit.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        edit(n);
      }
    });
    JMenu menu = rootPopup;
    JMenuItem mi = null;
    mi = res.createJMenuItem("Menu.Tree.Collapse");
    mi.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        collapse();
      }
    });
    menu.add(mi);
    mi = res.createJMenuItem("Menu.Tree.Expand");
    mi.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        expand();
      }
    });
    menu.add(mi);

    addMouseListener(new TreePopupListener(popup));
    popup.addPopupMenuListener(new PopupMenuListener() {

      public void popupMenuCanceled(PopupMenuEvent e) {
        // Not used
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // Hmmm, not used either?
      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // Buildit!
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object o = n.getUserObject();
        popup.removeAll();
        if (o instanceof Category) {
          popup.add(rename);
          popup.add(rootPopup);
          popup.add(delete);
        } else if (o instanceof CompositePattern) {
          popup.add(rootPopup);
          popup.add(delete);
        } else if (o instanceof AbstractFieldPattern) {
          popup.add(edit);
          popup.add(delete);
        } else if (o instanceof Pattern) {
          popup.add(delete);
        } else if (o instanceof String && "/".equals(o)) {
          popup.add(rootPopup);
        } else {
          // Shouldnt happen
          popup.add(new JLabel("NEVER HAPPENS"));
        }
      }
    });
    this.setCellRenderer(new CategoryTreeRenderer());
    this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    this.setDragEnabled(true);
    this.setTransferHandler(new CategoryTransferHandler());
  }

  public void setCategories(Collection<Category> categories) {
    // Builds the tree...
    synchronized (model) {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
      root.removeAllChildren();
      for (Category c : categories) {
        root.add(getCategoryAsNode(c));
      }
      model.reload(root);
    }
  }

  public MutableTreeNode getCategoryAsNode(Category c) {
    DefaultMutableTreeNode n = new DefaultMutableTreeNode(c);
    n.setAllowsChildren(true);
    n.add(getPatternAsNode(c.getPattern()));
    return n;
  }

  public MutableTreeNode getPatternAsNode(Pattern p) {
    DefaultMutableTreeNode n = new DefaultMutableTreeNode(p);
    if (p instanceof CompositePattern) {
      n.setAllowsChildren(true);
      CompositePattern composite = (CompositePattern) p;
      for (Pattern pat : composite.getChildren()) {
        n.add(getPatternAsNode(pat));
      }
    } else if (p instanceof NotPattern) {
      n.add(getPatternAsNode(((NotPattern) p).getPattern()));
    }
    return n;
  }

  public Pattern getNodeAsPattern(DefaultMutableTreeNode node) {
    Object o = node.getUserObject();
    if (node.isLeaf() && o instanceof Pattern) {
      return (Pattern) o; // FIXME: this does not create a new object!
    } else if (o instanceof CompositePattern) {
      CompositePattern composite = null;
      try {
        composite = (CompositePattern) o.getClass().newInstance();
      } catch (Exception ex) {
        // Nothing
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        composite.addPattern(getNodeAsPattern((DefaultMutableTreeNode) node.getChildAt(i)));
      }
      return composite;
    }
    return null;
  }

  private Category getNodeAsCategory(DefaultMutableTreeNode n) {
    Category c = (Category) n.getUserObject();
    if (n.getFirstChild() == null) {
      n.add(getPatternAsNode(new OrPattern()));
    }
    Pattern p = getNodeAsPattern((DefaultMutableTreeNode) n.getFirstChild());
    if (c != null && p != null) {
      c = new Category(c.getName(), p);
    }
    return c;
  }

  public Collection<Category> getCategories() {
    Collection<Category> categories = new ArrayList<Category>();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
    for (int i = 0; i < root.getChildCount(); i++) {
      DefaultMutableTreeNode n = (DefaultMutableTreeNode) root.getChildAt(i);
      Category c = getNodeAsCategory(n);
      categories.add(c);
    }
    return categories;
  }

  private void collapse() {
    TreeNode node = (TreeNode) this.getLastSelectedPathComponent();
    for (TreePath path : buildCollapsePaths(node)) {
      this.collapsePath(path);
    }
  }

  private void expand() {
    TreeNode node = (TreeNode) this.getLastSelectedPathComponent();
    for (TreePath path : buildExpandPaths(node)) {
      this.expandPath(path);
    }
  }

  /** Traverses the tree containing the node and returns TreePaths for all leafs.
   **/
  private Collection<TreePath> buildExpandPaths(TreeNode n) {
    Collection<TreePath> paths = new ArrayList<TreePath>();
    if (n.isLeaf()) {
      paths.add(new TreePath(model.getPathToRoot(n.getParent())));
    } else {
      paths.add(new TreePath(model.getPathToRoot(n)));
      for (int i = 0; i < n.getChildCount(); i++) {
        paths.addAll(buildExpandPaths(n.getChildAt(i)));
      }
    }
    return paths;
  }

  private Collection<TreePath> buildCollapsePaths(TreeNode n) {
    Collection<TreePath> paths = new ArrayList<TreePath>();
    if (!n.isLeaf()) {
      paths.add(new TreePath(model.getPathToRoot(n)));
      for (int i = 0; i < n.getChildCount(); i++) {
        paths.addAll(buildCollapsePaths(n.getChildAt(i)));
      }
    }
    return paths;
  }

  private void rename(DefaultMutableTreeNode n) {
    Object o = n.getUserObject();
    if (o instanceof Category) {
      Category c = (Category) o;
      String name = JOptionPane.showInputDialog(this, res.getString("NewCategoryName.Title"), c.getName());
      if (name != null) {
        c = new Category(name, c.getPattern());
        n.setUserObject(c);
      }
    }
  }

  private void edit(DefaultMutableTreeNode n) {
    Object o = n.getUserObject();
    if (o instanceof AbstractFieldPattern) {
      PatternEditor editor = new PatternEditor(getFrame());
      if (editor.editPattern((AbstractFieldPattern) o) == JOptionPane.OK_OPTION) {
        n.setUserObject(editor.getPattern());
      }
    }
  }

  private JFrame getFrame() {
    Container c = getParent();
    while (!(c instanceof JFrame) && c != null) {
      c = c.getParent();
    }
    return (JFrame) c;
  }
}
