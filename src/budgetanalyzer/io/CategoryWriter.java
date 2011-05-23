/*
 * CategoryWriter.java
 *
 * Created on 10. juli 2007, 21:29
 *
 */

package budgetanalyzer.io;

import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.AbstractFieldPattern;
import budgetanalyzer.model.patterns.AndPattern;
import budgetanalyzer.model.patterns.ComparisonPattern;
import budgetanalyzer.model.patterns.CompositePattern;
import budgetanalyzer.model.patterns.ContainsPattern;
import budgetanalyzer.model.patterns.NotPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.model.patterns.RegExpPattern;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Collection;

/**
 * Allows the writing of the category list.
 * @author Rasmus
 * @version 1.0
 * @see CategoryReader
 */
public class CategoryWriter {
  static final public String TOKEN_OP_CONTAINS = "CONTAINS";
  static final public String TOKEN_OP_ICONTAINS = "ICONTAINS";
  static final public String TOKEN_OP_EQ = ComparisonPattern.TOKEN_EQ;
  static final public String TOKEN_OP_LT = ComparisonPattern.TOKEN_LT;
  static final public String TOKEN_OP_GT = ComparisonPattern.TOKEN_GT;
  static final public String TOKEN_OP_REGEXP = "REGEXP";
  
  static final protected String INDENT = "  ";
  /** Creates a new instance of CategoryWriter */
  private CategoryWriter() {
  }
  static public void writeCategoriesBinary(File f, Collection<Category> list) {
    ObjectOutputStream oos;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(f, false));
      oos.writeObject(list);
    } catch (FileNotFoundException ex) {
      throw new IllegalStateException(ex);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }
  /** Writes the categories as a textual pseudo programming language. */
  static public void printCategories(PrintStream out, Collection<Category> list) {
    for (Category c : list) {
      printCategory(out, c);
    }
  }
  static public void printCategory(PrintStream out, Category c) {
    out.print(c.getName() + ":\n");
    printPattern(out, c.getPattern(), INDENT);
  }
  static public void printPattern(PrintStream out, Pattern p, String indent) {
    // Find out the name of the pattern...
    if (p instanceof CompositePattern) {
      CompositePattern pat = (CompositePattern)p;
      if (p instanceof AndPattern) {
        out.print(indent + "AND {\n");
      } else if (p instanceof OrPattern) {
        out.print(indent + "OR {\n");
      } else {
        throw new IllegalArgumentException("Unknown CompositePattern: " + p);
      }
      for (Pattern p2 : pat.getChildren()) {
        printPattern(out, p2, indent + INDENT);
      }
      out.print(indent + "}\n");
    } else if (p instanceof NotPattern) {
      out.print(indent + "NOT {\n");
      printPattern(out, ((NotPattern)p).getPattern(), indent + INDENT);
      out.print(indent + "}\n");
    } else if (p instanceof AbstractFieldPattern) {
      // Leaf pattern
      String exp = "<unset>";
      AbstractFieldPattern afp = (AbstractFieldPattern)p;
      out.print(indent + afp.getFieldId() + " ");
      if (afp instanceof ComparisonPattern) {
        ComparisonPattern pat = (ComparisonPattern)afp;
        String cmp = "";
        switch (pat.getType()) {
          case EQUAL: cmp = TOKEN_OP_EQ; break;
          case GREATERTHAN: cmp = TOKEN_OP_GT; break;
          case LESSTHAN: cmp = TOKEN_OP_LT; break;
          default: cmp = pat.getType().toString();
        }
        out.print(cmp + " ");
        exp = pat.getExpression();
      } else if (afp instanceof ContainsPattern) {
        ContainsPattern pat = (ContainsPattern)afp;
        if (pat.isIgnoreCase()) {
          out.print(TOKEN_OP_ICONTAINS);
        } else {
          out.print(TOKEN_OP_CONTAINS);
        }
        out.print(" ");
        exp = "\"" + pat.getExpression() + "\"";
      } else if (afp instanceof RegExpPattern) {
        RegExpPattern pat = (RegExpPattern)afp;
        out.print(TOKEN_OP_REGEXP + " ");
        exp = "\"" + pat.getExpression() + "\"";
      }
      out.print(exp + "\n");
    }
  }
  static public String toString(Category c) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    printCategory(ps, c);
    ps.flush();
    return baos.toString();
  }
  static public String toString(Pattern p) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    printPattern(ps, p, "");
    ps.flush();
    return baos.toString();
  }
}
