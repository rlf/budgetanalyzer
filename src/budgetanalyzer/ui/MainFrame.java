/*
 * MainFrame.java
 *
 * Created on 10. juli 2007, 22:06
 *
 */

package budgetanalyzer.ui;

import dk.lockfuglsang.rasmus.util.ui.table.filters.Filter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import budgetanalyzer.io.CSVReader;
import budgetanalyzer.io.CSVWriter;
import budgetanalyzer.io.CategoryReader;
import budgetanalyzer.io.CategoryWriter;
import budgetanalyzer.model.BankEntryTableModel;
import budgetanalyzer.model.Category;
import budgetanalyzer.model.summary.MonthlySummaryResult;
import budgetanalyzer.model.summary.SummaryResult;
import budgetanalyzer.ui.model.EntryCategoryModel;
import budgetanalyzer.ui.table.BankEntryRenderer;
import budgetanalyzer.ui.table.EntryRenderer;
import budgetanalyzer.ui.table.IgnoreFilter;
import budgetanalyzer.ui.table.MonthlySummaryRenderer;
import budgetanalyzer.ui.table.TableModelBankEntryIterator;
import dk.lockfuglsang.rasmus.util.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.graph.ComplexChart;
import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;
import dk.lockfuglsang.rasmus.util.ui.graph.data.impl.TableModelGraphData;
import dk.lockfuglsang.rasmus.util.ui.table.AbstractTableModelDecorator;
import dk.lockfuglsang.rasmus.util.ui.table.ColumnClassTableModel;
import dk.lockfuglsang.rasmus.util.ui.table.FilterColumnTableModel;
import dk.lockfuglsang.rasmus.util.ui.table.FilterRowTableModel;
import dk.lockfuglsang.rasmus.util.ui.table.FilterTableHeaderRenderer;
import dk.lockfuglsang.rasmus.util.ui.table.FilterTablePopup;
import dk.lockfuglsang.rasmus.util.ui.table.TablePopupListener;
import dk.lockfuglsang.rasmus.util.ui.table.TableSorterModel;
import dk.lockfuglsang.rasmus.util.ui.table.filters.CompositeFilter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.NotFilter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.RegFilter;

/**
 * The main-frame of the application
 * @author Rasmus
 * @version 1.0
 */
public class MainFrame extends JFrame implements PropertyChangeListener {
  static public final Resource RES = new Resource("MainFrame");
  static private final String PROP_FILE = "budgetanalyzer.properties";
  static private final String PROP_SAVED_CSV = "last.saved.csvfile";
  static private final String PROP_LOADED_CSV = "last.loaded.csvfile";
  static private final String PROP_SAVED_CAT = "last.saved.catfile";
  static private final String PROP_LOADED_CAT = "last.loaded.catfile";
  static private final String PROP_FRAME_WIDTH = "frame.width";
  static private final String PROP_FRAME_HEIGHT = "frame.height";
  // TODO: Resource file
  static private final KeyStroke KEYSTROKE_NEXT = KeyStroke.getKeyStroke("ctrl released DOWN");
  static private final KeyStroke KEYSTROKE_PREV = KeyStroke.getKeyStroke("ctrl released UP");
  
  static private final int COL_TEKST = 1;
  static private final int COL_CATEGORY = BankEntryRenderer.COL_CATEGORY;
  
  static private final DateFormat MONTH = new SimpleDateFormat(RES.getString("MonthlySummaryResult.DateFormat", "dd-MM-yyyy"));
  
  private JFileChooser chooser;
  
  private CategoryEditor editor;

  private final JTable entryTable;
  private TableSorterModel entrySorter;
  private BankEntryTableModel entryTableModel;
  private FilterRowTableModel filterTableModel;
  
  private JTabbedPane tabbedSummary;
  private ColumnClassTableModel summaryTableModel;
  private ColumnClassTableModel monthSummaryTableModel;
  private ColumnClassTableModel quaterlySummaryTableModel;
  
  private EntryCategoryModel ecModel;
  
  private Properties prop;
  
  /** Creates a new instance of MainFrame */
  public MainFrame() {
    super(RES.getString("MainFrame.Title"));
    
    this.entryTable = new JTable();
    prop = new Properties();
    ecModel = new EntryCategoryModel(Arrays.asList(RES.getString("Column.Name.Date"), 
        RES.getString("Column.Name.Text"), RES.getString("Column.Name.Value"), RES.getString("Column.Name.Category")));
    setupUIComponents();
    ecModel.addPropertyChangeListener(this);
    // Initialization of non UI fields
    loadProperties();
  }
  private void setupUIComponents() {
    //setSize(new Dimension(RES.getInt("MainFrame.Width", 1200), RES.getInt("MainFrame.Height", 800)));
    
    Container con = this.getContentPane();
    JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    con.add(toolBar, BorderLayout.NORTH);
    
    editor = new CategoryEditor(this);
    editor.addPropertyChangeListener(this);
    
    setupMenuBar(toolBar);
    
    JSplitPane center = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    con.add(center, BorderLayout.CENTER);
    
    entryTableModel = ecModel.getEntryTableModel();
    entrySorter = new TableSorterModel(entryTableModel);
    filterTableModel = new FilterRowTableModel(entrySorter);
    entryTable.setModel(filterTableModel);
    filterTableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        updateSummary();
      }
    });
    JTable table = entryTable;
    
    JTableHeader tableHeader = table.getTableHeader();
    FilterTablePopup popup = new FilterTablePopup(filterTableModel, RES);
    FilterTableHeaderRenderer filterRenderer = new FilterTableHeaderRenderer(
        tableHeader.getDefaultRenderer(), filterTableModel, popup, RES);
    filterRenderer.setTableHeader(tableHeader);
    tableHeader.setDefaultRenderer(filterRenderer);
    entrySorter.setTableHeader(tableHeader);
    
    EntryRenderer renderer = new BankEntryRenderer(table);
    renderer.setColors(RES, "Entries.");
    center.setLeftComponent(new JScrollPane(table));
    setupPopupMenu(table, popup);
    table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
        if (ks.equals(KEYSTROKE_NEXT)) {
          selectNextRowWithoutCategory();
        } else if (ks.equals(KEYSTROKE_PREV)) {
          selectPrevRowWithoutCategory();
        }
      }
    });
    
    // Summary
    tabbedSummary = new JTabbedPane(JTabbedPane.TOP);
    center.setRightComponent(tabbedSummary);
    
    summaryTableModel = new ColumnClassTableModel();
    TableSorterModel sorter = new TableSorterModel(summaryTableModel);
    table = new JTable(sorter);
    sorter.setTableHeader(table.getTableHeader());
    renderer = new EntryRenderer(table);
    renderer.setColors(RES, "Categories.");
    tabbedSummary.add(RES.getString("Tab.Summary.Label"), new JScrollPane(table));
    
    monthSummaryTableModel = new ColumnClassTableModel();
    sorter = new TableSorterModel(monthSummaryTableModel);
    table = new JTable(sorter);
    sorter.setTableHeader(table.getTableHeader());
    MonthlySummaryRenderer renderer2 = new MonthlySummaryRenderer(table);
    renderer2.setColors(RES, "Monthly.");
    tabbedSummary.add(RES.getString("Tab.Monthly.Label"), new JScrollPane(table));
    
    // Remove the sub-categories, and the summary...
    FilterRowTableModel filterRows = new FilterRowTableModel(sorter);
    FilterColumnTableModel filterCols = new FilterColumnTableModel(filterRows, new int[] {1});
    GraphData<String,Number> graphData = new TableModelGraphData(filterCols);
    ComplexChart chart = new ComplexChart(graphData);

    quaterlySummaryTableModel = new ColumnClassTableModel();
    sorter = new TableSorterModel(quaterlySummaryTableModel);
    table = new JTable(sorter);
    sorter.setTableHeader(table.getTableHeader());
    MonthlySummaryRenderer renderer3 = new MonthlySummaryRenderer(table);
    renderer3.setColors(RES, "Monthly."); // Same as Monthly?
    tabbedSummary.add(RES.getString("Tab.Quaterly.Label"), new JScrollPane(table));
    
    tabbedSummary.add(RES.getString("Tab.Graph.Label"), chart);
    
    tabbedSummary.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        updateSummary();
      }
    });
    
    chooser = new JFileChooser(".");
    chooser.setDialogTitle(RES.getString("MainFrame.FileChooser.Title"));
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
  }
  private void setupMenuBar(JPanel toolBar) {
    JButton btn = null;
    
    JMenuBar menubar = new JMenuBar();
    this.setJMenuBar(menubar);
    JMenu menu = RES.createJMenu("MainFrame.Menu.File");
    menubar.add(menu);
    
    JMenuItem mi = RES.createJMenuItem("MainFrame.Menu.File.New");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clear();
      }
    });
    menu.add(mi);
    
    Action action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        openCSVFile();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.File.Open");
    menu.add(new JMenuItem(action));
    btn = new JButton(action);
    RES.prepareToolbarButton(btn);
    toolBar.add(btn);
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        saveCSVFile();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.File.Save");
    menu.add(new JMenuItem(action));
    btn = new JButton(action);
    RES.prepareToolbarButton(btn);
    toolBar.add(btn);
    
    menu.addSeparator();
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        refresh();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.File.Refresh");
    menu.add(new JMenuItem(action));
    btn = new JButton(action);
    RES.prepareToolbarButton(btn);
    toolBar.add(btn);
    
    menu.addSeparator();
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        saveAndExit();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.Exit");
    menu.add(new JMenuItem(action));
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        saveAndExit();
      }
    });
    menu = RES.createJMenu("MainFrame.Menu.Category");
    menubar.add(menu);
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        openCategoryFile();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.Category.Open");
    menu.add(new JMenuItem(action));
    btn = new JButton(action);
    RES.prepareToolbarButton(btn);
    toolBar.add(btn);
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        saveCategoryFile();
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.Category.Save");
    menu.add(new JMenuItem(action));
    btn = new JButton(action);
    RES.prepareToolbarButton(btn);
    toolBar.add(btn);
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        editor.editCategories(ecModel.getCategories());
      }
    };
    RES.prepareAction(action, "MainFrame.Menu.Category.Edit");
    menu.add(new JMenuItem(action));
    
    mi = RES.createJMenuItem("MainFrame.Menu.Category.Wizard");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showCategoryWizard();
      }
    });
    menu.add(mi);
    
    action = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        
      }
    };
    
    menu = RES.createJMenu("MainFrame.Menu.Results");
    menubar.add(menu);
    mi = RES.createJMenuItem("MainFrame.Menu.Results.Export");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exportResults();
      }
    });
    menu.add(mi);
  }
  private void setupPopupMenu(final JTable table, final FilterTablePopup filterPop) {
    JMenu menu = null;
    JMenuItem mi = null;
    final JPopupMenu popup = new JPopupMenu();
    final TablePopupListener popupListener = new TablePopupListener(popup, KeyStroke.getKeyStroke("ENTER"));
    table.addMouseListener(popupListener);
    table.addKeyListener(popupListener);
    menu = RES.createJMenu("Entries.Popup.Assign");
    menu.addMenuListener(new MenuListener() {
      public void menuCanceled(MenuEvent e) {
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuSelected(MenuEvent e) {
        // Build the submenu
        JMenu menu = (JMenu)e.getSource();
        menu.removeAll();
        ActionListener assign = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            assignToCategory(((JMenuItem)e.getSource()).getText(), popupListener.getSelectedRow());
          }
        };
        List<Category> list = new ArrayList<Category>(ecModel.getCategories());
        Collections.sort(list);
        for (Category c : list) {
          JMenuItem mi = new JMenuItem(c.getName());
          mi.addActionListener(assign);
          menu.add(mi);
        }
        JMenuItem mi = RES.createJMenuItem("Entries.Popup.NewCategory");
        mi.addActionListener(assign);
        menu.add(mi);
      }
    });
    popup.add(menu);
    
    menu = RES.createJMenu("Filter.PopupMenu");
    menu.addMenuListener(new MenuListener() {
      public void menuCanceled(MenuEvent e) {
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuSelected(MenuEvent e) {
        filterPop.setColumn(popupListener.getSelectedColumn());
        filterPop.prepareMenu((JMenu)e.getSource());
      }
    });
    popup.add(menu);
    
    mi = RES.createJMenuItem("Entries.Popup.Delete");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteEntry(popupListener.getSelectedRow());
      }
    });
    popup.add(mi);
    popup.add(menu);
  }
  private void openCSVFile() {
    chooser.setMultiSelectionEnabled(true);
    if (prop.getProperty(PROP_LOADED_CSV) != null) {
      chooser.setSelectedFile(new File(prop.getProperty(PROP_LOADED_CSV)));
    }
    int choice = chooser.showOpenDialog(this);
    if (choice == JFileChooser.APPROVE_OPTION) {
      File[] fs = chooser.getSelectedFiles();
      for (File f : fs) {
        loadCSVFile(f);
      }
    }
  }
  private void saveCSVFile() {
    chooser.setSelectedFile(new File(prop.getProperty(PROP_SAVED_CSV, "poster.csv")));
    int choice = chooser.showSaveDialog(this);
    if (choice == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      saveCSVFile(f);
    }
  }
  private void openCategoryFile() {
    chooser.setMultiSelectionEnabled(true);
    if (prop.containsKey(PROP_LOADED_CAT)) {
      chooser.setSelectedFile(new File(prop.getProperty(PROP_LOADED_CAT)));
    }
    int choice = chooser.showOpenDialog(this);
    if (choice == JFileChooser.APPROVE_OPTION) {
      File[] fs = chooser.getSelectedFiles();
      for (File f : fs) {
        loadCategoryFile(f);
      }
    }
  }
  private void saveCategoryFile() {
    if (prop.containsKey(PROP_SAVED_CAT)) {
      chooser.setSelectedFile(new File(prop.getProperty(PROP_SAVED_CAT)));
    }
    int choice = chooser.showSaveDialog(this);
    if (choice == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      saveCategoryFile(f);
    }
  }
  private void saveAndExit() {
    try {
      if (prop.containsKey(PROP_SAVED_CAT)) {
        saveCategoryFile(new File(prop.getProperty(PROP_SAVED_CAT)));
      }
      if (prop.containsKey(PROP_SAVED_CSV)) {
        saveCSVFile(new File(prop.getProperty(PROP_SAVED_CSV)));
      }
      saveProperties();
    } catch (RuntimeException e) {
      
    }
    System.exit(0);
  }
  private void refresh() {
    // Remember selected row, col
    int row = entryTable.getSelectedRow();
    updateSummary();
    entryTableModel.fireTableDataChanged();
    entryTable.getSelectionModel().setSelectionInterval(row, row);
  }
  private void updateSummary() {
    Throwable t = new RuntimeException("stacktracer");
    StackTraceElement[] stackTrace = t.getStackTrace();
    for (StackTraceElement trace : stackTrace) {
      if (trace != stackTrace[0] && trace.getClassName().equals(this.getClass().getName()) && trace.getMethodName().equals("updateSummary")) {
        return; // Bail out - avoid endless recursion by setting the filters below.
      }
    }
    Filter filter = filterTableModel.getFilter(COL_CATEGORY);
    if (filter != null) {
      filterTableModel.setFilter(COL_CATEGORY, new CompositeFilter(new IgnoreFilter(), filter));
    } else {
      filterTableModel.setFilter(COL_CATEGORY, new IgnoreFilter());
    }
    switch (tabbedSummary.getSelectedIndex()) {
      case 0: updateSummaryAll(); break;
      case 1: updateSummaryMonthly(); break;
      case 2: updateSummaryQuaterly(); break;
      case 3: updateSummaryMonthly();
        tabbedSummary.repaint();
        break;
      default:
        showException(new IllegalStateException("Unknown summary!"));
    }
    filterTableModel.setFilter(COL_CATEGORY, filter);
  }
  private void updateSummaryAll() {
    Set<SummaryResult> summarySet = ecModel.getSummary(new TableModelBankEntryIterator(filterTableModel));
    Object[][] dataVector = new Object[summarySet.size()][3];
    String[] colIds = { RES.getString("Summary.Name"),
    RES.getString("Summary.Count"),
    RES.getString("Summary.Sum")};
    
    int index = 0;
    for (SummaryResult summary : summarySet) {
      String name = summary.getName();
      name = name.replaceAll("[^_/]*/", "  ");
      name = name.replaceFirst(" ([^ ])", "  $1");
      dataVector[index][0] = name;
      dataVector[index][1] = new Integer(summary.getCount());
      dataVector[index][2] = new Float(summary.getSum());
      index++;
    }
    summaryTableModel.setDataVector(dataVector, colIds);
  }
  
  private void updateSummaryMonthly() {
    Set<MonthlySummaryResult> summarySet = ecModel.getMonthlySummary(new TableModelBankEntryIterator(filterTableModel));
    // Collect columns
    int nrows = summarySet.size();
    Map<String,Float[]> dataMap = new HashMap<String,Float[]>();
    int index = 0;
    for (MonthlySummaryResult msr : summarySet) {
      for (SummaryResult sr : msr.getSummary()) {
        String k = sr.getName();
        if (!dataMap.containsKey(k)) {
          dataMap.put(k, new Float[nrows]);
          Arrays.fill(dataMap.get(k), new Float(0));
        }
        dataMap.get(k)[index] = new Float(sr.getSum());
      }
      index++;
    }
    Object[][] dataVector = new Object[nrows][dataMap.size()+3];
    
    index = 0;
    List<String> cols = new ArrayList<String>();
    cols.add(RES.getString("Summary.Name"));
    cols.add(RES.getString("Summary.Sum"));
    cols.add(RES.getString("Summary.Average"));
    for (MonthlySummaryResult msr : summarySet) {
      int y = 0;
      String name = msr.getName();
      name = name.replaceAll("[^_/]*/", "  ");
      name = name.replaceFirst(" ([^ ])", "  $1");
      dataVector[index][y++] = name;
      dataVector[index][y++] = new Double(msr.getSum());
      dataVector[index][y++] = new Double(msr.getSum() / dataMap.size());
      for (String k : new TreeSet<String>(dataMap.keySet())) {
        dataVector[index][y] = dataMap.get(k)[index];
        try {
          Date d = MonthlySummaryResult.MONTH.parse(k);
          String month = MONTH.format(d);
          if (!cols.contains(month)) {
            cols.add(month);
          }
        } catch (ParseException e) {
          // Should never happen!
          throw new IllegalStateException(e);
        }        
        y++;
      }
      index++;
    }
    monthSummaryTableModel.setDataVector(dataVector, cols.toArray(new String[cols.size()]));
  }
  
  private void updateSummaryQuaterly() {
    Set<SummaryResult> summarySet = ecModel.getQuaterlySummary(new TableModelBankEntryIterator(filterTableModel));
    // Collect columns
    int nrows = summarySet.size();
    Map<String,Float[]> dataMap = new HashMap<String,Float[]>();
    int index = 0;
    for (SummaryResult msr : summarySet) {
      for (SummaryResult sr : msr.getSummary()) {
        String k = sr.getName();
        if (!dataMap.containsKey(k)) {
          dataMap.put(k, new Float[nrows]);
          Arrays.fill(dataMap.get(k), new Float(0));
        }
        dataMap.get(k)[index] = new Float(sr.getSum());
      }
      index++;
    }
    Object[][] dataVector = new Object[nrows][dataMap.size()+3];
    
    index = 0;
    List<String> cols = new ArrayList<String>();
    cols.add(RES.getString("Summary.Name"));
    cols.add(RES.getString("Summary.Sum"));
    cols.add(RES.getString("Summary.Average"));
    for (SummaryResult msr : summarySet) {
      int y = 0;
      String name = msr.getName();
      name = name.replaceAll("[^_/]*/", "  ");
      name = name.replaceFirst(" ([^ ])", "  $1");
      dataVector[index][y++] = name;
      dataVector[index][y++] = new Double(msr.getSum());
      dataVector[index][y++] = new Double(msr.getSum() / dataMap.size());
      for (String k : new TreeSet<String>(dataMap.keySet())) {
        dataVector[index][y] = dataMap.get(k)[index];
        if (!cols.contains(k)) {
          cols.add(k);
        }
        y++;
      }
      index++;
    }
    quaterlySummaryTableModel.setDataVector(dataVector, cols.toArray(new String[cols.size()]));
  }
  
  private void loadProperties() {
    FileInputStream fis;
    try {
      fis = new FileInputStream(PROP_FILE);
      prop.load(fis);
      String k = prop.getProperty(PROP_SAVED_CSV);
      if (k != null) {
        loadCSVFile(new File(k));
      } else {
        k = prop.getProperty(PROP_LOADED_CSV);
        if (k != null) {
          loadCSVFile(new File(k));
        }
      }
      k = prop.getProperty(PROP_SAVED_CAT);
      if (k != null) {
        loadCategoryFile(new File(k));
      } else {
        k = prop.getProperty(PROP_LOADED_CAT);
        if (k != null) {
          loadCategoryFile(new File(k));
        }
      }
    } catch (FileNotFoundException ex) {
      // No properties found...
    } catch (IOException ex) {
      showException(ex);
    }
  }
  private void saveProperties() {
    prop.setProperty(PROP_FRAME_WIDTH, "" + getWidth());
    prop.setProperty(PROP_FRAME_HEIGHT, "" + getHeight());
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(PROP_FILE);
      prop.store(fos, "Properties saved at " + new Date());
    } catch (IOException ex) {
      showException(ex);
    }
  }
  private void showException(Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, e);
  }
  private void loadCSVFile(File f) {
    try {
      ecModel.addEntries(CSVReader.getEntries(new FileInputStream(f)));
      prop.setProperty(PROP_LOADED_CSV, f.getAbsolutePath());
    } catch (FileNotFoundException e) {
      showException(e);
    } catch (IllegalArgumentException e) {
      IllegalArgumentException e2 = new IllegalArgumentException(f.getName() + ": " + e.getMessage());
      e2.initCause(e);
      showException(e2);
    }
  }
  private void saveCSVFile(File f) {
    CSVWriter writer = new CSVWriter(f);
    writer.writeEntries(entryTableModel.iterator());
    prop.setProperty(PROP_SAVED_CSV, f.getAbsolutePath());
  }
  private void loadCategoryFile(File f)  {
    try {
      BufferedReader rdr = new BufferedReader(new FileReader(f));
      Collection<Category> lst = CategoryReader.readCategories(rdr);
      ecModel.addCategories(lst);
      prop.setProperty(PROP_LOADED_CAT, f.getAbsolutePath());
    } catch (Exception e) {
      showException(e);
    }
  }
  private void saveCategoryFile(File f) {
    try {
      FileOutputStream fos = new FileOutputStream(f);
      PrintStream out = new PrintStream(fos);
      CategoryWriter.printCategories(out, ecModel.getCategories());
      out.flush();
      fos.close();
      prop.setProperty(PROP_SAVED_CAT, f.getAbsolutePath());
    } catch (IOException e) {
      showException(e);
    }
  }
  @SuppressWarnings("unchecked")
  public void propertyChange(PropertyChangeEvent evt) {
    if (EntryCategoryModel.PROP_CHANGED.equals(evt.getPropertyName())) {
      refresh();
    } else if (CategoryEditor.PROP_CATEGORIES_CHANGED.equals(evt.getPropertyName())) {
      ecModel.setCategories((Collection<Category>)evt.getNewValue());
    }
  }
  public void setVisible(boolean b) {
    super.setVisible(b);
    pack();
    this.setSize(new Dimension(
        Integer.parseInt(prop.getProperty(PROP_FRAME_WIDTH, "500")),
        Integer.parseInt(prop.getProperty(PROP_FRAME_HEIGHT, "500"))));
    repaint();
  }
  private void assignToCategory(String catName, int row) {
    TableModel model = filterTableModel;
    
    Category cat = null;
    Collection<Category> categories = ecModel.getCategories();
    if (!RES.getString("Entries.Popup.NewCategory").equals(catName)) {
      // Existing...
      Iterator<Category> it = categories.iterator();
      while (it.hasNext() && cat == null) {
        Category itCat = it.next();
        if (itCat.getName().equals(catName)) {
          cat = itCat;
        }
      }
    }
    if (cat != null) {
      categories.remove(cat);
    }
    String exp = model.getValueAt(row, COL_TEKST).toString();
    // Convert the value...
    exp = exp.replaceAll("[Dd]ankort-nota", "");
    exp = exp.replaceAll("[Vv]isa køb", "");
    exp = exp.replaceAll("[0-9]*", "");
    exp = exp.replaceAll(" [ ]*", " ");
    exp = exp.trim();
    CategoryDialog catDialog = new CategoryDialog(this, cat, exp);
    if (catDialog.showPatternDialog() == JOptionPane.OK_OPTION) {
      categories.add(catDialog.getCategory());
    } else if (cat != null) {
      // Add the category again (it was removed before)
      categories.add(cat);
    }
    ecModel.setCategories(categories);
  }
  private void showCategoryWizard() {
    CategoryWizard wiz = new CategoryWizard(entryTableModel.getDataVector());
    if (wiz.showWizard(this) == JOptionPane.OK_OPTION) {
      ecModel.addCategories(wiz.getCategories());
    }
  }
  private void clear() {
    entryTableModel.clear();
  }
  private int getEntryTableModelRow(int row) {
    int index = row;
    TableModel tm = filterTableModel;
    while (tm instanceof AbstractTableModelDecorator) {
      AbstractTableModelDecorator atmd = (AbstractTableModelDecorator) tm;
      index = atmd.modelIndex(index);
      tm = atmd.getTableModel();
    }
    return index;
  }
  private void deleteEntry(int row) {
    entryTableModel.removeRow(getEntryTableModelRow(row));
  }
  private void exportResults() {
    if (monthSummaryTableModel != null) {
      chooser.setSelectedFile(null);
      int choice = chooser.showSaveDialog(this);
      if (choice == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        try {
          switch (tabbedSummary.getSelectedIndex()) {
            case 0:
              CSVWriter.writeTableModel(new FileOutputStream(f), summaryTableModel);
              break;
            case 1:
              CSVWriter.writeTableModel(new FileOutputStream(f), monthSummaryTableModel);
              break;
            default:
              showException(new IllegalStateException("Unknown summary!"));
          }
        } catch (FileNotFoundException ex) {
          ex.printStackTrace();
          showException(ex);
        }
      }
    }
  }
  private void selectNextRowWithoutCategory() {
    int srow = entryTable.getSelectedRow();
    int n = entryTable.getRowCount();
    int row = (srow + 1) % n;
    // Search forward
    while (row != srow && entryTable.getValueAt(row, COL_CATEGORY) != null) {
      row = (row + 1) % n;
    }
    entryTable.getSelectionModel().setSelectionInterval(row, row);
    Rectangle aRect = entryTable.getCellRect(row, 0, true);
    entryTable.scrollRectToVisible(aRect);
  }
  private void selectPrevRowWithoutCategory() {
    int srow = entryTable.getSelectedRow();
    int row = srow - 1;
    int n = entryTable.getRowCount();
    if (row < 0) {
      row = n-1;
    }
    // Search forward
    while (row != srow && entryTable.getValueAt(row, COL_CATEGORY) != null) {
      row--;
      if (row < 0) {
        row = n-1;
      }
    }
    entryTable.getSelectionModel().setSelectionInterval(row, row);
    Rectangle aRect = entryTable.getCellRect(row, 0, true);
    entryTable.scrollRectToVisible(aRect);
  }
}
