package budgetanalyzer.ui;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;

import dk.lockfuglsang.rasmus.util.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.editor.EditorFactory;
import dk.lockfuglsang.rasmus.util.ui.tree.EditorObjectTree;
import dk.lockfuglsang.rasmus.util.ui.tree.PreferenceObjectTreeModel;

public class PreferencesDialog extends JDialog {
  private Resource res;
  public PreferencesDialog(JFrame frame) {
    super(frame, true);
    this.res = new Resource("PreferencesDialog");
    this.setTitle(res.getString("PrefDialog.Title"));
    
    Container cont = this.getContentPane();
    PreferenceObjectTreeModel treeModel = new PreferenceObjectTreeModel();
    EditorFactory editFactory = null;
    cont.add(new EditorObjectTree(treeModel, editFactory));
  }
  public static void main(String[] args) {
    JFrame frame = new JFrame("test");
    PreferencesDialog dialog = new PreferencesDialog(frame);
    dialog.setVisible(true);
  }
}
