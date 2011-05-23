/*
 * CategoryWriterTest.java
 * JUnit based test
 *
 * Created on 15. juli 2007, 20:43
 */

package budgetanalyzer.io;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import junit.framework.*;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Collection;

/**
 *
 * @author Rasmus
 */
public class CategoryWriterTest extends TestCase {
  
  public CategoryWriterTest(String testName) {
    super(testName);
  }
  
  /**
   * Test of printCategories method, of class budgetanalyzer.io.CategoryWriter.
   */
  public void testPrintCategories() throws Exception {
    System.out.println("printCategories");
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(baos);
    String expected = "StringPatterns:\n" +
        "  AND {\n" +
        "    MyField CONTAINS \"aBc\"\n" +
        "    MyValue ICONTAINS \"abe\"\n" +
        "    OR {\n" +
        "      MyField REGEXP \"[aA][bB][ce]\"\n" +
        "    }\n" +
        "  }\n" +
        "Comparisons:\n" +
        "  NOT {\n" + 
        "    OR {\n" +
        "      MyField = \"string\"\n" +
        "      MyField = 0\n" + 
        "      MyField < 10\n" + 
        "      MyField > -100\n" + 
        "      MyField > \"aa\"\n" +
        "    }\n" +
        "  }\n";
    Collection<Category> list = Arrays.asList(
        new Category("StringPatterns", new AndPattern(Arrays.asList(
        new ContainsPattern("MyField", "aBc"),
        new ContainsPattern("MyValue", "aBe", true),
        new OrPattern(Arrays.asList(
        (Pattern)new RegExpPattern("MyField", "[aA][bB][ce]")
        ))))),
        new Category("Comparisons", new NotPattern(new OrPattern(Arrays.asList(
        (Pattern)new ComparisonPattern(ComparisonPattern.Type.EQUAL, "MyField", "string"),
        (Pattern)new ComparisonPattern(ComparisonPattern.Type.EQUAL, "MyField", new Integer(0)),
        (Pattern)new ComparisonPattern(ComparisonPattern.Type.LESSTHAN, "MyField", new Integer(10)),
        (Pattern)new ComparisonPattern(ComparisonPattern.Type.GREATERTHAN, "MyField", new Long(-100)),
        (Pattern)new ComparisonPattern(ComparisonPattern.Type.GREATERTHAN, "MyField", "aa")
        ))))
        );
    
    CategoryWriter.printCategories(out, list);
//    System.out.println("EXPECTED:\n" + expected);
//    System.out.println("ACTUAL:\n" + baos.toString());
    assertEquals(expected, baos.toString());
  }
  
}
