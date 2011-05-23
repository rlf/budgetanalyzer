package budgetanalyzer.io.readers;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import budgetanalyzer.io.BankCSVReader;
import budgetanalyzer.model.BankEntry;

public class AbstractBankCSVReader implements BankCSVReader, Serializable {
  static final public String DELIM = ";";
  
  static final private DateFormat[] DATE_FORMATS = new DateFormat[] {
    new SimpleDateFormat("dd-MM-yyyy"),
    new SimpleDateFormat("dd.MM.yyyy"),
    new SimpleDateFormat("yyyy-MM-dd"),
    new SimpleDateFormat("yyyy.MM.dd")
  };
  
  public enum ColumnType {
    SKIP, DATE, VALUE, TEXT
  }
  /** Name of the reader. */
  private String name;
  
  /** The first line of the CSV-files, or a pattern to match the first line. */
  private String firstLine;
  
  private long lineNo;
  
  private Map<ColumnType, Integer> columnMap;
  
  private int numCols;
  
  public AbstractBankCSVReader(String name, String firstLine, Map<ColumnType, Integer> columns, int numCols) {
    this.name = name;
    this.firstLine = firstLine;
    this.numCols = numCols;
    
    this.columnMap = new HashMap<ColumnType, Integer>(columns);
    if (!columnMap.containsKey(ColumnType.DATE)) {
      throw new IllegalArgumentException("One column must contain a DATE!");
    }
    if (!columnMap.containsKey(ColumnType.TEXT)) {
      throw new IllegalArgumentException("One column must contain TEXT!");
    }
    if (!columnMap.containsKey(ColumnType.VALUE)) {
      throw new IllegalArgumentException("One column must contain a VALUE!");
    }
  }
  @Override
  public boolean canRead(String firstLine) {
    lineNo = 0;
    return firstLine.matches(this.firstLine);
  }
  
  @Override
  public BankEntry readEntry(String line) {
    String[] elements = readElements(line);
    lineNo++;
    if (elements.length != numCols) {
      throw new IllegalArgumentException("Wrong number of elements @" + lineNo + " in line [" + line + "], expected " + numCols + ", got " + elements.length);
    }
    Date date = readDate(elements[columnMap.get(ColumnType.DATE)]);
    String text = elements[columnMap.get(ColumnType.TEXT)];
    float value = readFloat(elements[columnMap.get(ColumnType.VALUE)]);
    
    return new BankEntry(date, text, value);
  }
  
  private Date readDate(String s) {
    Date d = null;
    for (int i = 0; d == null && i < DATE_FORMATS.length; i++) {
      try {
        d = DATE_FORMATS[i].parse(s);
      } catch (ParseException e) {
        d = null;
      }
    }
    return d;
  }
  private float readFloat(String s) {
    String sF = s.replaceAll("\\.", "");
    sF = sF.replaceAll(",", ".");
    return Float.parseFloat(sF);
  }
  private String[] readElements(String line) {
    String[] elements = line.split(DELIM);
    if (line.endsWith(";")) {
      // Split removes trailing empty strings...
      String[] newElements = new String[elements.length+1];
      System.arraycopy(elements, 0, newElements, 0, elements.length);
      newElements[elements.length] = "";
      elements = newElements;
    }
    for (int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      if (elements[i].startsWith("\"")) {
        elements[i] = elements[i].substring(1);
      }
      if (elements[i].endsWith("\"")) {
        elements[i] = elements[i].substring(0, elements[i].length()-1);
      }
    }
    return elements;
  }
  
  public String getName() {
    return name;
  }
}
