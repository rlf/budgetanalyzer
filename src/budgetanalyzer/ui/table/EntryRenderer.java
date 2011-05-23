/*
 * EntryRenderer.java
 *
 * Created on 11. juli 2007, 22:09
 *
 */

package budgetanalyzer.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import budgetanalyzer.model.Category;
import budgetanalyzer.ui.resources.Resource;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class EntryRenderer implements TableCellRenderer {
  protected Resource res;
  
  protected Color bgColEven;
  protected Color bgColUneven;
  
  protected Color fgColSelected;
  protected Color bgColSelected;
  
  protected Color fgColDefault;
  protected Color fgColNegativeNumber;
  
  protected SimpleDateFormat dateFormat;
  
  protected String nullString;
  
  protected JPanel p;
  protected JLabel l;
  /**
   * Creates a new instance of EntryRenderer
   */
  public EntryRenderer(JTable table, Resource res) {
    p = new JPanel( new GridLayout(1,1));
    l = new JLabel();
    p.add(l);
    if (table != null) {
      table.setDefaultRenderer(String.class, this);
      table.setDefaultRenderer(Number.class, this);
      table.setDefaultRenderer(Integer.class, this);
      table.setDefaultRenderer(Long.class, this);
      table.setDefaultRenderer(Float.class, this);
      table.setDefaultRenderer(Double.class, this);
      table.setDefaultRenderer(Date.class, this);
      table.setDefaultRenderer(Category.class, this);
    }
    if (res != null) {
      setColors(res, "");
    }
  }
  public EntryRenderer(JTable table) {
    this(table, null);
  }
  public EntryRenderer() {
    this(null, null);
  }
  public void setColors(dk.lockfuglsang.rasmus.util.resources.Resource res, String prefix) {
    bgColEven = res.createColor(prefix + "Bg.Color.Even");
    bgColUneven = res.createColor(prefix + "Bg.Color.Uneven");
    bgColSelected = res.createColor(prefix + "Bg.Color.Selected");
    fgColSelected = res.createColor(prefix + "Fg.Color.Selected");
    
    fgColDefault = res.createColor(prefix + "Fg.Color.Default");
    fgColNegativeNumber = res.createColor(prefix + "Fg.Color.NegativeNumber");
    
    dateFormat = new SimpleDateFormat(res.getString(prefix + "DataFormat.Date"));
    nullString = res.getString(prefix + "NullString");
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    l.setText("" + value);
    l.setForeground(fgColDefault);
    Color bgCol = null;
    if (isSelected) {
      bgCol = bgColSelected;
      l.setForeground(fgColSelected);
    } else {
      if (row % 2 == 0) {
        bgCol = bgColEven;
      } else {
        bgCol = bgColUneven;
      }
    }
    p.setBackground(bgCol);
    if (value instanceof Number) {
      l.setHorizontalAlignment(SwingConstants.RIGHT);
      
      double dVal = ((Number)value).doubleValue();
      if (dVal < 0 && !isSelected) {
        l.setForeground(fgColNegativeNumber);
      }
      if (value instanceof Float || value instanceof Double) {
        l.setText(String.format("%.2f", dVal));
      } else {
        l.setText(value.toString());
      }
    } else {
      if (value instanceof Date) {
        l.setText(dateFormat.format((Date)value));
      } else if (value instanceof Category) {
        l.setText(((Category)value).getName());
      }
      l.setHorizontalAlignment(SwingConstants.LEFT);
    }
    if (value == null) {
      l.setText(nullString);
    }
    return p;
  }
}
