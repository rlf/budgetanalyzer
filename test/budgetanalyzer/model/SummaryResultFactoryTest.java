/*
 * SummaryResultFactoryTest.java
 * JUnit based test
 *
 * Created on 12. juli 2007, 20:10
 */

package budgetanalyzer.model;

import budgetanalyzer.io.BankCSVReader;
import budgetanalyzer.io.readers.AbstractBankCSVReader;
import budgetanalyzer.model.patterns.AndPattern;
import budgetanalyzer.model.patterns.ComparisonPattern;
import budgetanalyzer.model.patterns.ContainsPattern;
import budgetanalyzer.model.patterns.OrPattern;
import budgetanalyzer.model.patterns.Pattern;
import budgetanalyzer.model.patterns.RegExpPattern;
import budgetanalyzer.model.summary.SummaryResult;
import budgetanalyzer.model.summary.SummaryResultFactory;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Rasmus
 */
public class SummaryResultFactoryTest extends TestCase {
  
  public SummaryResultFactoryTest(String testName) {
    super(testName);
  }
  
  /**
   * Test of createSummary method, of class budgetanalyzer.model.SummaryResultFactory.
   */
  public void testCreateSummary() {
    System.out.println("createSummary");
    
    Collection<BankEntry> entries = new ArrayList(ENTRIES);
    Collection<Category> categories = new ArrayList(CATEGORIES);
    
    SummaryResultFactory instance = null;
    
    Set<SummaryResult> expResult = new TreeSet(RESULT);
    Set<SummaryResult> result = SummaryResultFactory.createSummary(entries.iterator(), categories);
    assertEquals(expResult, result);
  }
  
  static final Map<AbstractBankCSVReader.ColumnType, Integer> READER_COLS = new HashMap<AbstractBankCSVReader.ColumnType, Integer>();
  static {
      READER_COLS.put(AbstractBankCSVReader.ColumnType.DATE, 0);
      READER_COLS.put(AbstractBankCSVReader.ColumnType.DATE, 1);
      READER_COLS.put(AbstractBankCSVReader.ColumnType.TEXT, 2);
      READER_COLS.put(AbstractBankCSVReader.ColumnType.VALUE, 3);
  }
  static final BankCSVReader READER = new AbstractBankCSVReader("abstract", "#test", READER_COLS, 5);
  static final Collection<BankEntry> ENTRIES = Arrays.asList(
      READER.readEntry("10.10.2006;09.10.2006;DK 04333 La Lanterna;-1.040,00;-1.494,80;nej"),
      READER.readEntry("10.10.2006;09.10.2006;DK 01733 Hvelplund;-8.085,00;-9.579,80;nej"),
      READER.readEntry("10.10.2006;09.10.2006;DK 22460 Baresso Coffee A/S;-84,00;-9.663,80;nej"),
      READER.readEntry("11.10.2006;10.10.2006;DK 28905 Hammelstrupvej Fdb Superbr;-563,50;-10.227,30;nej"),
      READER.readEntry("12.10.2006;11.10.2006;DK 03901 Sjælør Cykler;-300,00;-10.527,30;nej"),
      READER.readEntry("16.10.2006;13.10.2006;DK 08885 DANSKE BANK;-1.200,00;-11.727,30;nej"),
      READER.readEntry("16.10.2006;13.10.2006;DK 24147 Fakta Borgbjergvej;-113,70;-11.841,00;nej"),
      READER.readEntry("17.10.2006;16.10.2006;DK 07843 Restaurant Arken;-570,00;-12.411,00;nej"),
      READER.readEntry("18.10.2006;17.10.2006;DK 97376 Kafe Kys;-240,00;-12.651,00;nej"),
      READER.readEntry("19.10.2006;18.10.2006;DK 07925 Chili Restaurant A/S;-236,00;-12.887,00;nej"),
      READER.readEntry("19.10.2006;18.10.2006;DK 01598 Hammelstrupvej Fdb Superbr;-229,40;-13.116,40;nej"),
      READER.readEntry("20.10.2006;19.10.2006;DK 30387 Hammelstrupvej Fdb Superbr;-908,60;-14.025,00;nej"),
      READER.readEntry("24.10.2006;23.10.2006;DK 51410 Just-Eat.Dk Aps;-449,00;-14.474,00;nej"),
      READER.readEntry("24.10.2006;23.10.2006;DK 05828 Fakta Borgbjergvej;-490,75;-14.964,75;nej"),
      READER.readEntry("25.10.2006;25.10.2006;Lønoverførsel;28.604,70;13.639,95;nej"),
      READER.readEntry("27.10.2006;26.10.2006;DK 010 Kiosk Bien;-115,00;13.524,95;nej"),
      READER.readEntry("27.10.2006;26.10.2006;DK 008 Fisketorvet Føtex;-120,00;13.404,95;nej")
      );
  
  static final Collection<Category> CATEGORIES = Arrays.asList(
      new Category("Restaurants", new OrPattern(Arrays.asList(
      (Pattern) new ContainsPattern("Tekst", "Lanterna"),
      (Pattern) new ContainsPattern("Tekst", "Just-Eat"),
      (Pattern) new ContainsPattern("Tekst", "Restaurant")
      ))),
      new Category("Bicycles", new ComparisonPattern(ComparisonPattern.Type.EQUAL, "Tekst", "DK 03901 Sjælør Cykler")),
      new Category("FDB", new ContainsPattern("Tekst", "fDB", true)),
      new Category("Salary", new AndPattern(Arrays.asList(
      (Pattern) new RegExpPattern("Tekst", "Løn.*"),
      (Pattern) new ComparisonPattern(ComparisonPattern.Type.GREATERTHAN, "Beloeb", new Float(25000))
      )))
      );
  static final Collection<SummaryResult> RESULT = Arrays.asList(
      new SummaryResult("Restaurants", 4, -2295f),
      new SummaryResult("Bicycles", 1, -300f),
      new SummaryResult("FDB", 3, -1701.5f),
      new SummaryResult("Salary", 1, 28604.70f),
      new SummaryResult("All", 17, 13859.75f),
      new SummaryResult("Not matched", 8, -10448.45f)
      );
  // <[SummaryResult['All', count=17,sum=13859.75], SummaryResult['Bicycles', count=1,sum=-300.0], SummaryResult['FDB', count=3,sum=-1701.5], SummaryResult['Not matched', count=9,sum=-10448.45], SummaryResult['Restaurants', count=4,sum=-2295.0], SummaryResult['Salary', count=1,sum=28604.7]]> but was:
  // <[SummaryResult['All', count=17,sum=13859.749], SummaryResult['Bicycles', count=1,sum=-300.0], SummaryResult['FDB', count=3,sum=-1701.5], SummaryResult['Not matched', count=8,sum=-10448.45], SummaryResult['Restaurants', count=4,sum=-2295.0], SummaryResult['Salary', count=1,sum=28604.7]]>


}
