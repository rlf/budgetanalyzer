/*
 * MonthlySummaryRenderer.java
 *
 * Created on 24. juli 2007, 23:09
 *
 */

package budgetanalyzer.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class MonthlySummaryRenderer extends JComponent implements PropertyChangeListener, TableCellRenderer {
  
  protected double[] colSums;
  protected boolean dirty;
  
  protected String text;
  protected double value;
  
  protected Color bgColEven;
  protected Color bgColUneven;
  protected Color fgCol;
  protected Color barColNeg;
  protected Color barColPos;
  
  protected int column;
  protected boolean isNum;
  
  /** Creates a new instance of MonthlySummaryRenderer */
  public MonthlySummaryRenderer(JTable table) {
    table.addPropertyChangeListener(this);
    table.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent tme) {
        dirty = true;
      }
    });
    dirty = true;
    colSums = new double[0];
    value = 0;
    text = "";
    if (table != null) {
      table.setDefaultRenderer(String.class, this);
      table.setDefaultRenderer(Number.class, this);
      table.setDefaultRenderer(Integer.class, this);
      table.setDefaultRenderer(Long.class, this);
      table.setDefaultRenderer(Float.class, this);
      table.setDefaultRenderer(Double.class, this);
    }
  }
  public void setColors(dk.lockfuglsang.rasmus.util.resources.Resource res, String prefix) {
    bgColEven = res.createColor(prefix + "Bg.Color.Even");
    bgColUneven = res.createColor(prefix + "Bg.Color.Uneven");
    
    fgCol = res.createColor(prefix + "Fg.Color");
    barColNeg = res.createColor(prefix + "Bar.Color.Negative");
    barColPos = res.createColor(prefix + "Bar.Color.Positive");
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    Component c = this;
    this.value = 0;
    if (dirty || column >= colSums.length) {
      calculateSums(table);
    }
    if (value instanceof Number) {
      this.value = ((Number)value).doubleValue();
      text = String.format("%.2f", this.value);
      isNum = true;
    } else {
      isNum = false;
      text = value.toString();
    }
    
    if (row % 2 == 0) {
      setBackground(bgColEven);
    } else {
      setBackground(bgColUneven);
    }
    setForeground(fgCol);
    if (isSelected) {
      setBackground(table.getSelectionBackground());
      setForeground(table.getSelectionForeground());
    }
    this.column = column;
    return c;
  }
  private void calculateSums(JTable table) {
    TableModel model = table.getModel();
    colSums = new double[model.getColumnCount()];
    for (int col = 0; col < colSums.length; col++) {
      colSums[col] = 0;
      Class<? extends Object> colClass = model.getColumnClass(col);
      if (colClass.isAssignableFrom(Float.class) ||
          colClass.isAssignableFrom(Double.class) ||
          colClass.isAssignableFrom(Integer.class) ||
          colClass.isAssignableFrom(Long.class)) {
        for (int row = 0; row < model.getRowCount()-1; row++) { // Ignore 'Sum' row
          Object o = model.getValueAt(row, col);
          if (o instanceof Number) {
            double dVal = Math.abs(((Number)o).doubleValue());
            if (dVal > colSums[col]) colSums[col] = dVal;
          }
        }
      }
    }
    dirty = false;
  }
  public void propertyChange(PropertyChangeEvent evt) {
    dirty = true;
  }
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    g.setColor(getBackground());
    g.fillRect(0, 0, w, h);
    if (value != 0) {
      if (value < 0) {
        g.setColor(barColNeg);
      } else {
        g.setColor(barColPos);
      }
      int barW = (int)Math.round(((w * Math.abs(value)) / colSums[column]));
      g.fillRect(w-barW+2, 2, barW-4, h-4);
    }
    g.setColor(fgCol);
    FontMetrics fm = g.getFontMetrics();
    int fh = fm.getAscent();
    int sw = fm.stringWidth(text);
    int cy = (h+fh)/2;
    int rx = (isNum ? w - 6 - sw : 2);
    g.setColor(getForeground());
    g.drawString(text, rx, cy);
  }
}
