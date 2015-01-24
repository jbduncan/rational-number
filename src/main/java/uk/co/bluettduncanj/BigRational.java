package uk.co.bluettduncanj;

import net.jcip.annotations.Immutable;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * <p>An immutable, unlimited-size fraction.</p>
 *
 * <p>This class does not have any public constructors - instead it has
 * static factory methods. To give two examples:</p>
 * <pre>
 *   BigRational half = BigRational.valueOf(1, 2);  // -&gt; 1/2
 *   BigRational twoThirds = BigRational.valueOf(
 *       BigInteger.valueOf(2),
 *       BigInteger.valueOf(3));                    // -&gt; 2/3
 * </pre>
 *
 * <p>{@code BigRational} objects are automatically simplified when created (this means {@code 2/4}
 * becomes {@code 1/2}). Also the signs of their numerator and denominator are inverted if the
 * denominator is negative (this means {@code 1/-5} becomes {@code -1/5}). It is invalid for
 * a denominator to be zero.</p>
 *
 * <p>{@code BigRational} objects are <strong>immutable</strong> and
 * <strong>thread-safe</strong>. This means {@code BigRational}s can be freely
 * and safely shared between many threads and used concurrently.
 * However this also means that all methods in this class which return
 * {@code BigRational} create new instances, which means calling these methods
 * won't change the source object. For example:</p>
 * <pre>
 *   BigRational rational = BigRational.valueOf(1, 2);
 *   rational.add(BigRational.valueOf(1, 5));  // Don't do this!
 *   System.out.println(rational);             // -&gt; 1/2
 * </pre>
 * <p>To achieve the desired effect, do the following instead.</p>
 * <pre>
 *   BigRational rational = BigRational.valueOf(1, 2);
 *   rational = rational.add(BigRational.valueOf(1, 5));  // Correct usage
 *   System.out.println(rational);                        // -&gt; 7/10
 * </pre>
 *
 * <p>Unless otherwise noted, passing {@code null} to any of the methods of a {@code
 * BigRational} will cause a {@code NullPointerException} to be thrown.</p>
 *
 * @author Jonathan Bluett-Duncan
 */
@Immutable
public final class BigRational extends Number implements Comparable<BigRational>, Serializable {

  /**
   * The numerator of {@code this}. Must be non-null. Can be zero, in which case {@code
   * denominator} must be one.
   *
   * @serial
   */
  private final BigInteger numerator;

  /**
   * The denominator of {@code this}. Must be non-null. Cannot be zero.
   *
   * @serial
   */
  private final BigInteger denominator;

  /**
   * Private constructor of {@code BigRational} instances; prevents instantiation by client.
   *
   * @param numerator   the numerator
   * @param denominator the denominator
   * @throws IllegalArgumentException if {@code denominator == 0}.
   */
  private BigRational(BigInteger numerator, BigInteger denominator) {
    BigInteger num = safeInstance(requireNonNull(numerator, "Numerator is null"));
    BigInteger den = safeInstance(requireNonNull(denominator, "Denominator is null"));

    if (den.equals(BigInteger.ZERO)) {
      throw new IllegalArgumentException("Denominator is zero");
    }

    if (num.equals(BigInteger.ZERO)) {
      // Optimisation
      num = BigInteger.ZERO;
      den = BigInteger.ONE;
    } else {

      // Reduce the rational to prevent excessive memory usage
      BigInteger gcd = num.gcd(den);
      num = num.divide(gcd);
      den = den.divide(gcd);

      // Ensure the denominator is always positive
      if (den.signum() < 0) {
        num = num.negate();
        den = den.negate();
      }
    }

    this.numerator = num;
    this.denominator = den;
  }

  /**
   * <p>Defensively copies a new {@code BigInteger} if {@code val} is an untrusted
   * subclass of {@code BigInteger}, otherwise returns {@code val}.</p>
   *
   * <p>The purpose of this method is to guard against client code attacks
   * which attempt to pass an instance of an subclass of {@code BigInteger}
   * whose members violate the invariants of {@code BigInteger}. This is
   * required because this class depends on {@code BigInteger} being immutable
   * to function properly.</p>
   *
   * @param val the instance to check
   * @return a {@code BigInteger} copy of {@code val} if its class is not
   * {@code BigInteger}, otherwise {@code val} itself
   */
  private static BigInteger safeInstance(BigInteger val) {
    // Implemented as per Joshua Bloch's example in Effective Java 2nd Edition, p.
    if (val.getClass() != BigInteger.class) {
      return new BigInteger(val.toByteArray());
    }
    return val;
  }

  public static BigRational valueOf(BigDecimal value) {
    return null;
  }

  public static BigRational valueOf(long value) {
    return valueOf(BigInteger.valueOf(value));
  }

  public static BigRational valueOf(BigInteger value) {
    return valueOf(value, BigInteger.ONE);
  }

  public static BigRational valueOf(BigInteger numerator, BigInteger denominator) {
    return new BigRational(numerator, denominator);
  }

  public static BigRational valueOf(long numerator, long denominator) {
    return valueOf(BigInteger.valueOf(numerator),
                   BigInteger.valueOf(denominator));
  }

  public static BigRational valueOf(String value) {
    return null;
  }

  /**
   * <p>Returns the value of the specified number as a {@code byte},
   * which may involve rounding or truncation.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code byte}
   */
  @Override
  public byte byteValue() {
    return 0;
  }

  public byte byteValueExact() {
    return 0;
  }

  /**
   * @param value the object to be compared
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object
   * @throws NullPointerException if value is {@code null}
   */
  @Override
  public int compareTo(BigRational value) {
    if (this == value) {
      return 0;
    }
    return (denominator.compareTo(value.denominator) == 0)
        ? numerator.compareTo(value.numerator)
        : compareWithUnequalDenominators(this, value);
  }

  /**
   * Compares two BigRational objects, {@code a} and {@code b}, which are
   * assumed to have unequal denominators.
   *
   * @param a A {@code BigRational} object
   * @param b A {@code BigRational} object to compare against {@code a}
   * @return a negative integer, zero, or a positive integer as {@code a} is
   * less than, equal to, or greater than {@code b}
   */
  private static int compareWithUnequalDenominators(BigRational a, BigRational b) {
    // Cross multiply the numerators of a and b with each other before comparing them.
    BigInteger aNum = a.numerator.multiply(b.denominator);
    BigInteger bNum = b.numerator.multiply(a.denominator);
    return aNum.compareTo(bNum);


  }

  /**
   * Returns the denominator of this {@code BigRational} number.
   *
   * @return the denominator of {@code this}.
   */
  public BigInteger denominator() {
    return denominator;
  }

  /**
   * <p>Returns the value of the specified number as a {@code double},
   * which may involve rounding.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code double}
   */
  @Override
  public double doubleValue() {
    return 0;
  }

  public double doubleValueExact() {
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BigRational)) {
      return false;
    }
    BigRational rational = (BigRational) o;
    return numerator.equals(rational.numerator)
        && denominator.equals(rational.denominator);
  }

  /**
   * <p>Returns the value of the specified number as a {@code float},
   * which may involve rounding.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code float}
   */
  @Override
  public float floatValue() {
    return 0;
  }

  public float floatValueExact() {
    return 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  /**
   * <p>Returns the value of the specified number as an {@code int},
   * which may involve rounding or truncation.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code int}
   */
  @Override
  public int intValue() {
    return 0;
  }

  public int intValueExact() {
    return 0;
  }

  /**
   * <p>Returns the value of the specified number as a {@code long},
   * which may involve rounding or truncation.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code long}
   */
  @Override
  public long longValue() {
    return 0;
  }

  public long longValueExact() {
    return 0;
  }

  /**
   * <p>Returns the numerator of this {@code BigRational} number.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the numerator of {@code this}
   */
  public BigInteger numerator() {
    return numerator;
  }

  /**
   * <p>Returns the value of the specified number as a {@code short},
   * which may involve rounding or truncation.</p>
   *
   * <p>This method is currently <strong>not implemented</strong>, and so will return zero.</p>
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code short}
   */
  @Override
  public short shortValue() {
    return 0;
  }

  public short shortValueExact() {
    return 0;
  }

  /**
   * <p>Returns the exact value of the specified number as a {@code BigDecimal}.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the {@code BigDecimal} representation of {@code this}.
   */
  public BigDecimal toBigDecimal() {
    return BigDecimal.ZERO;
  }

  /**
   * <p>Returns the value of the specified number as a {@code BigInteger},
   * rounded to the nearest whole number.</p>
   *
   * <p>This method is currently <em>not implemented</em>, and so will return zero.</p>
   *
   * @return the {@code BigInteger} representation of {@code this}.
   */
  public BigInteger toBigInteger() {
    return BigInteger.ZERO;
  }

  /**
   * <p>Returns the decimal {@code String} representation of this
   * {@code BigRational} number.</p>
   *
   * <p>The string is of the format {@code "num/den"},
   * where {@code num} is the numerator represented as a sequence of decimal
   * digits, and {@code den} is the denominator represented as a sequence
   * of decimal digits. For example, {@code "1/2"}, {@code "-1/2"} and
   * {@code "34/567"} are valid string representations of possible
   * {@code BigRational} numbers, but {@code "1.5/2.3"}, {@code "1/-2"} and
   * {@code "-1/-2"} are not.</p>
   *
   * <p>This representation is compatible with the
   * {@link BigRational#valueOf(String)} <em>static factory method</em>.</p>
   *
   * @return the string representation of {@code this}
   */
  @Override
  public String toString() {
    return (denominator.equals(BigInteger.ONE))
        ? numerator.toString()
        : numerator.toString() + "/" + denominator.toString();
  }

  /**
   * Unsupported serialization method.
   *
   * @param stream the object input stream
   * @throws InvalidObjectException always, because this serialization method is unsupported
   */
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Proxy required");
  }

  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * Serialization proxy for BigRational class.
   */
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 2837712364721L;
    private final BigInteger numerator;
    private final BigInteger denominator;

    SerializationProxy(BigRational value) {
      numerator = value.numerator;
      denominator = value.denominator;
    }

    private Object readResolve() {
      return BigRational.valueOf(numerator, denominator);
    }
  }
}
