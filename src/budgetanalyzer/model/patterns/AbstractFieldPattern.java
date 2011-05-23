/*
 * AbstractFieldPattern.java
 *
 * Created on 10. juli 2007, 20:57
 *
 */

package budgetanalyzer.model.patterns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Implements the Template Method pattern.
 *
 * @author Rasmus
 * @version 1.0
 */
public abstract class AbstractFieldPattern implements Pattern {
  protected boolean match;
  protected String fieldId;
  protected String exp;
  /** Creates a new instance of AbstractFieldPattern */
  public AbstractFieldPattern(String fieldId, String exp) {
    this.fieldId = fieldId;
    this.exp = exp;
    this.match = false;
  }
  public final boolean matches() {
    return match;
  }
  
  protected abstract boolean match(Object value);
  
  public final void visit(Object o) {
    try {
      Method getter = o.getClass().getMethod("get" + fieldId);
      Object value = getter.invoke(o, (Object[])null);
      match = match(value);
    } catch (InvocationTargetException ex) {
      match = false;
    } catch (IllegalAccessException ex) {
      match = false;
    } catch (SecurityException ex) {
      match = false;
    } catch (NoSuchMethodException ex) {
      match = false;
    }
  }
  public String getFieldId() {
    return fieldId;
  }
  public String getExpression() {
    return exp;
  }
}
