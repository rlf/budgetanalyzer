/*
 * ComparisonPattern.java
 *
 * Created on 10. juli 2007, 21:42
 *
 */

package budgetanalyzer.model.patterns;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class ComparisonPattern extends AbstractFieldPattern {
  static public final String TOKEN_LT = "<";
  static public final String TOKEN_GT = ">";
  static public final String TOKEN_EQ = "=";
  
  static public enum Type {
    LESSTHAN, GREATERTHAN, EQUAL
  };
  protected Type type;
  protected Object exp;
  /**
   * Creates a new instance of ComparisonPattern
   */
  public ComparisonPattern(Type type, String field, Object exp) {
    super(field, exp.toString());
    this.type = type;
    this.exp = exp;
  }
  
  protected boolean match(Object value) {
    if (value == null) throw new IllegalArgumentException("Value cannot be null in ComparisonPattern");
    // Extract the values...
    Class<? extends Object> cl = value.getClass();
    if (cl.isPrimitive() || value instanceof Number) {
      // int, float etc.
      if (cl == Integer.TYPE || cl == Long.TYPE || cl == Float.TYPE || cl == Double.TYPE ||
          value instanceof Number) {
        double dValue = Double.parseDouble(value.toString());
        double dExp = Double.parseDouble(exp.toString());
        switch (type) {
          case LESSTHAN: return dValue < dExp;
          case GREATERTHAN: return dValue > dExp;
          case EQUAL: return Double.compare(dValue, dExp) == 0;
          default:
            throw new IllegalStateException("Unsupported type: " + type);
        }
      } else {
        throw new IllegalArgumentException("Type of argument must be a number in ComparisonPattern if primitive");
      }
    } else if (value instanceof String) {
      int cmp = value.toString().compareTo(exp.toString());
      switch (type) {
        case LESSTHAN: return cmp < 0;
        case GREATERTHAN: return cmp > 0;
        case EQUAL: return cmp == 0;
        default:
          throw new IllegalStateException("Unsupported type: " + type);
      }
    } else {
      throw new IllegalArgumentException("Unsupported argument value : " + value);
    }
  }
  public Type getType() {
    return type;
  }
  public String getExpression() {
    String s = exp.toString();
    if (exp instanceof String) {
      s = "\"" + s + "\"";
    }
    return s;
  }
  static public Type getType(String s) {
    if (TOKEN_EQ.equals(s)) {
      return Type.EQUAL;
    } else if (TOKEN_LT.equals(s)) {
      return Type.LESSTHAN;
    } else if (TOKEN_GT.equals(s)) {
      return Type.GREATERTHAN;
    }
    return null;
  }
  
  public int hashCode() {
    int code = 0;
    code ^= fieldId.hashCode();
    code ^= type.hashCode();
    code ^= exp.hashCode();
    return code;
  }
  public int compareTo(Object o) {
    ComparisonPattern other = (ComparisonPattern)o;
    int cmp = fieldId.compareTo(other.fieldId);
    if (cmp == 0) {
      cmp = type.compareTo(other.type);
    }
    if (cmp == 0) {
      cmp = exp.toString().compareTo(other.exp.toString());
    }
    return cmp;
  }
  public boolean equals(Object o) {
    if (!(o instanceof ComparisonPattern)) {
      return false;
    }
    return compareTo(o) == 0;
  }
}
