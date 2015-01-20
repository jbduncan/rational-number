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
 * <p>Unlimited-size rational number (or fraction).</p>
 *
 * <p>This class does not have any public constructors, instead it uses
 * static factory methods. For example:</p>
 * <pre>
 *   BigRational half = BigRational.valueOf(1, 2);  {@literal // -> 1/2}
 *   BigRational twoThirds = BigRational.valueOf(
 *       BigInteger.valueOf(2),
 *       BigInteger.valueOf(3));                    {@literal // -> 2/3}
 * </pre>
 *
 * <p>{@code BigRational} objects automatically simplify their
 * numerators and denominators when created. This means that:
 * <ul>
 *   <li>If {@code (denominator &lt; 0)} then
 *   {@code (numerator = -numerator)} and
 *   {@code (denominator = -denominator)}.</li>
 *   <li>{@code numerator} and {@code denominator} are reduced
 *   as much as possible, e.g. {@code 2/4 -> 1/2}.</li>
 * </ul></p>
 *
 * <p>Unless said otherwise, all methods in this class throw
 * {@code NullPointerException} when passed a {@code null} value.</p>
 *
 * <p>{@code BigRational} objects are <strong>immutable</strong> and
 * <strong>thread-safe</strong>. This means {@code BigRational}s can be freely
 * shared between many threads and used concurrently safely.
 * However this also means that all methods in this class which return
 * {@code BigRational} create new instances, which makes the following code
 * usage incorrect.</p>
 *
 * <pre>
 *   BigRational rational = BigRational.valueOf(1, 2);
 *   rational.add(BigRational.valueOf(1, 5));  // Don't do this!
 *   System.out.println(rational);             // -> 1/2
 * </pre>
 *
 * <p>To achieve the effect you probably meant, do the following instead.</p>
 * <pre>
 *   BigRational rational = BigRational.valueOf(1, 2);
 *   rational = rational.add(BigRational.valueOf(1, 5));  // Correct!
 *   System.out.println(rational);                        // -> 7/10
 * </pre>
 */
@Immutable
public final class BigRational extends Number
    implements Comparable<BigRational>, Serializable {

  /** The numerator of {@code this}. Must be non-null. */
  private final BigInteger numerator;

  /** The denominator of {@code this}. Must be non-null. */
  private final BigInteger denominator;

  /**
   * Private constructor of {@code BigRational} instances; prevents
   * instantiation by client.
   *
   * @param numerator the numerator
   * @param denominator the denominator
   * @throws IllegalArgumentException if {@code denominator == 0}.
   */
  private BigRational(BigInteger numerator, BigInteger denominator) {
    BigInteger num = safeInstance(
        requireNonNull(numerator, "Numerator is null"));
    BigInteger den = safeInstance(
        requireNonNull(denominator, "Denominator is null"));

    if (den.equals(BigInteger.ZERO)) {
      throw new IllegalArgumentException("Denominator is zero");
    }

    // Reduce the rational to prevent excessive memory usage
    BigInteger gcd = num.gcd(den);
    num = num.divide(gcd);
    den = den.divide(gcd);

    // Ensure the denominator is always positive
    if (den.signum() < 0) {
      num = num.negate();
      den = den.negate();
    }

    this.numerator = num;
    this.denominator = den;
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
  private static int compareWithUnequalDenominators(BigRational a,
                                                    BigRational b) {
    // Cross multiply the numerators of a and b
    // with each other before comparing them.
    BigInteger aNumerator = a.numerator.multiply(b.denominator);
    BigInteger bNumerator = b.numerator.multiply(a.denominator);
    return aNumerator.compareTo(bNumerator);
  }

  /**
   * @param value the object to be compared
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object
   * @throws NullPointerException if {@code value == null}
   */
  @Override
  public int compareTo(BigRational value) {
    return denominator.compareTo(value.denominator) == 0
        ? numerator.compareTo(value.numerator)
        : compareWithUnequalDenominators(this, value);
  }

  /**
   * Returns the denominator of this {@code BigRational} number.
   *
   * @return the denominator of {@code this}
   */
  public BigInteger denominator() {
    return denominator;
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

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  /**
   * Returns the numerator of this {@code BigRational} number.
   *
   * @return the numerator of {@code this}
   */
  public BigInteger numerator() {
    return numerator;
  }

  public static BigRational valueOf(BigDecimal value) {
    return null;
  }

  public static BigRational valueOf(BigInteger value) {
    return valueOf(value, BigInteger.ONE);
  }

  public static BigRational valueOf(BigInteger numerator, BigInteger denominator) {
    return new BigRational(numerator, denominator);
  }

  public static BigRational valueOf(long value) {
    return valueOf(BigInteger.valueOf(value));
  }

  public static BigRational valueOf(long numerator, long denominator) {
    return valueOf(BigInteger.valueOf(numerator),
                   BigInteger.valueOf(denominator));
  }

  public static BigRational valueOf(String value) {
    return null;
  }

  private void readObject(ObjectInputStream stream)
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy required");
  }

  /**
   * Defensively copies a new {@code BigInteger} if {@code val} is an untrusted
   * subclass of {@code BigInteger}, otherwise returns {@code val}.
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
    if (val.getClass() != BigInteger.class) {
      return new BigInteger(val.toByteArray());
    }
    return val;
  }

  /**
   * <p>Returns the decimal {@code String} representation of this
   * {@code BigRational} number.</p>
   *
   * <p>The string is of the format {@code "num/den"},
   * where {@code num} is the numerator represented as a sequence of decimal
   * digits, and {@code den} is the denominator also represented as a sequence
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
    return denominator.equals(BigInteger.ONE)
        ? numerator.toString()
        : numerator.toString() + "/" + denominator.toString();
  }

  /**
   * Returns the value of the specified number as an {@code int},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code int}
   */
  @Override
  public int intValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code long},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code long}
   */
  @Override
  public long longValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code float},
   * which may involve rounding.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code float}
   */
  @Override
  public float floatValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code double},
   * which may involve rounding.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code double}
   */
  @Override
  public double doubleValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code byte},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code byte}
   */
  @Override
  public byte byteValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code short},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code short}
   */
  @Override
  public short shortValue() {
    return 0;
  }

  /**
   * Returns the value of the specified number as a {@code BigInteger},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code BigInteger}
   */
  public BigInteger bigIntegerValue() {
    return BigInteger.ZERO;
  }

  /**
   * Returns the value of the specified number as a {@code BigDecimal},
   * which may involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion
   * to type {@code BigDecimal}
   */
  public BigDecimal bigDecimalValue() {
    return BigDecimal.ZERO;
  }

  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * A proxy class whose instances are serialized in place of BigRational.
   */
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 2837712364721L;
    private final BigInteger numerator;
    private final BigInteger denominator;

    SerializationProxy(BigRational value) {
      this.numerator = value.numerator;
      this.denominator = value.denominator;
    }

    private Object readResolve() {
      return BigRational.valueOf(numerator, denominator);
    }
  }
}