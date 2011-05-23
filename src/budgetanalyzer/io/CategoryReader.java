/*
 * CategoryReader.java
 *
 * Created on 10. juli 2007, 19:20
 *
 */

package budgetanalyzer.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.AndPattern;
import budgetanalyzer.model.patterns.ComparisonPattern;
import budgetanalyzer.model.patterns.ContainsPattern;
import budgetanalyzer.model.patterns.NotPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.model.patterns.RegExpPattern;
import budgetanalyzer.ui.resources.Resource;

/**
 * Reads a CSV file into memory.
 * @author Rasmus
 * @version 1.0
 * @see CategoryWriter
 */
public class CategoryReader {
  static final protected Resource RES = new Resource("CategoryReader");
  static final protected String S_LABEL = "[^:]+";
  static final protected String S_OP = "(CONTAINS)|(ICONTAINS)|(=)|(<)|(>)|(REGEXP)";
  static final protected String S_NOT = "NOT {";
  static final protected String S_END = "}";
  static final protected java.util.regex.Pattern PAT_LABEL =
      java.util.regex.Pattern.compile(S_LABEL + ":");
  static final protected java.util.regex.Pattern PAT_COMPOSITE =
      java.util.regex.Pattern.compile("((AND)|(OR)) \\{");
  static final protected java.util.regex.Pattern PAT_FIELD =
      java.util.regex.Pattern.compile("(" + S_LABEL + ") (" + S_OP + ") (.*)");
  
  /** Reads a pattern from the reader.
   * @return a Pattern or <code>null</code> if the end of a composite pattern is found.
   **/
  static public Pattern readPattern(LineReader rdr) throws IOException, SyntaxErrorException {
    Pattern pattern = null;
    String line = rdr.readLine();
    if (line != null) {
      line = line.trim();
      Matcher mField = PAT_FIELD.matcher(line);
      if (S_END.equals(line)) {
        return null;
      } else if (PAT_COMPOSITE.matcher(line).matches()) {
        Collection<Pattern> children = readComposite(rdr);
        if (line.startsWith("AND")) {
          return new AndPattern(children);
        } else if (line.startsWith("OR")) {
          return new OrPattern(children);
        }
      } else if (S_NOT.equals(line)) {
        pattern = new NotPattern(readPattern(rdr));
        String s = rdr.readLine();
        if (s != null) {
          s = s.trim();
          if (!s.equals(S_END)) {
            String[] args = { S_END, s };
            throw new SyntaxErrorException(rdr.getLineNumber(), RES.getString("Syntax.Error.NotWhatExpected", args)); 
          }
        }
      } else if (mField.matches()) {
        // Syntax: <field> <operator> <value>
        //         <grp1>  <grp2>     <grp9>
        String fieldId = mField.group(1);
        String op = mField.group(2);
        String val = mField.group(9);
        Object oVal = null;
        // Try to figure out what type of value it is...
        // Extract value...
        if (val.startsWith("\"") && val.endsWith("\"")) {
          oVal = val.substring(1, val.length()-1);
        }
        if (oVal == null) {
          try {
            oVal = new Integer(val);
          } catch (NumberFormatException e) {
            // Ignore
          }
        }
        if (oVal == null) {
          try {
            oVal = new Float(val);
          } catch (NumberFormatException e) {
            // Ignore
          }
        }
        if (oVal == null) {
          try {
            oVal = new Long(val);
          } catch (NumberFormatException e) {
            // Ignore
          }
        }
        if (oVal == null) {
          try {
            oVal = new Double(val);
          } catch (NumberFormatException e) {
            // Ignore
          }
        }
        if (oVal == null) {
          oVal = val;
        }
        // Create pattern
        if (CategoryWriter.TOKEN_OP_CONTAINS.equals(op) || CategoryWriter.TOKEN_OP_ICONTAINS.equals(op)) {
          pattern = new ContainsPattern(fieldId, oVal.toString(), CategoryWriter.TOKEN_OP_ICONTAINS.equals(op));
        } else if (CategoryWriter.TOKEN_OP_EQ.equals(op) ||
            CategoryWriter.TOKEN_OP_GT.equals(op) ||
            CategoryWriter.TOKEN_OP_LT.equals(op)) {
          pattern = new ComparisonPattern(ComparisonPattern.getType(op), fieldId, oVal);
        } else if (CategoryWriter.TOKEN_OP_REGEXP.equals(op)) {
          pattern = new RegExpPattern(fieldId, oVal.toString());
        } else {
          String[] args = { line };
          throw new SyntaxErrorException(rdr.getLineNumber(), RES.getString("Syntax.Error.UnsupportedPattern", args));
        }
      }
    }
    return pattern;
  }
  static private Collection<Pattern> readComposite(LineReader rdr) throws IOException, SyntaxErrorException {
    Collection<Pattern> result = new ArrayList<Pattern>();
    Pattern pat = readPattern(rdr);
    while (pat != null) {
      result.add(pat);
      pat = readPattern(rdr);
    }
    return result;
  }
  static public Collection<Category> readCategories(BufferedReader rdr) throws IOException, SyntaxErrorException {
    LineReader lrdr = new LineReader(rdr);
    Collection<Category> result = new ArrayList<Category>();
    String line = null;
    do {
      line = lrdr.readLine();
      if (line != null) {
        if (PAT_LABEL.matcher(line).matches()) {
          line = line.trim();
          String cName = line.substring(0, line.length()-1);
          Pattern p = readPattern(lrdr);
          result.add(new Category(cName, p));
        } else {
          String[] args = { line };
          throw new SyntaxErrorException(lrdr.getLineNumber(), RES.getString("Syntax.Error.SyntaxError", args));
        }
      }
    } while (line != null);
    return result;
  }
}
