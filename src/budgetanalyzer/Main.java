/*
 * Main.java
 *
 * Created on 10. juli 2007, 19:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package budgetanalyzer;

import budgetanalyzer.ui.MainFrame;
import dk.lockfuglsang.rasmus.util.ui.UIHelper;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rasmus
 */
public class Main {
  
  /** Creates a new instance of Main */
  public Main() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // Setup the system LAF
    UIHelper.getLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        MainFrame mf = new MainFrame();
        mf.pack();
        mf.setVisible(true);
      }
    });
  }
  
}
