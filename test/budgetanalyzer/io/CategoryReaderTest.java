/*
 * CategoryReaderTest.java
 * JUnit based test
 *
 * Created on 16. juli 2007, 20:49
 */

package budgetanalyzer.io;

import budgetanalyzer.model.Category;
import budgetanalyzer.model.patterns.AndPattern;
import budgetanalyzer.model.patterns.ComparisonPattern;
import budgetanalyzer.model.patterns.ContainsPattern;
import budgetanalyzer.model.patterns.NotPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.model.patterns.RegExpPattern;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.*;

/**
 *
 * @author Rasmus
 */
public class CategoryReaderTest extends TestCase {
  
  public CategoryReaderTest(String testName) {
    super(testName);
  }
  
  /**
   * Test of testReadCategories method, of class budgetanalyzer.io.CategoryReader.
   */
  public final void testReadCategories() throws Exception {
    String input = "StringPatterns:\n" +
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
    Collection<Category> expected = Arrays.asList(
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
    
    StringReader rdr = new StringReader(input);
    
    Collection<Category> actual = CategoryReader.readCategories(new BufferedReader(rdr));
//    CategoryWriter.printCategories(System.out, actual);
    assertEquals(expected, actual);
  }
  static public void assertEquals(Collection<Category> c1, Collection<Category> c2) {
    assertEquals("same size", c1.size(), c2.size());
    Iterator<Category> it1 = c1.iterator();
    Iterator<Category> it2 = c2.iterator();
    while (it1.hasNext()) {
      assertEquals(it1.next(), it2.next());
    }
  }
}
