package uk.org.bluettduncanj;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Unlimited-size rational number (fraction) with arbitrarily large numerator
 * and denominator.
 *
 * <p>This class does not provide any constructors, instead it provides
 * <em>static method factories</em>. For example:
 * <pre>
 *   BigRational half = BigRational.of(1, 2);  // -> 1/2
 *   BigRational twoThirds = BigRational.of(
 *       BigInteger.valueOf(2),
 *       BigInteger.valueOf(3));               // -> 2/3
 * </pre></p>
 *
 * <p>Unless explicitly stated otherwise, all methods in this class throw
 * {@code NullPointerException} when passed a {@code null} value.</p>
 *
 * <p>{@code BigRational}s are <em>thread-safe</em> and <em>immutable</em>.</p>
 */
public final class BigRational implements Comparable<BigRational> {

  /** The numerator of {@code this}. Must be non-null. */
  private final BigInteger numerator;

  /** The denominator of {@code this}. Must be non-null. */
  private final BigInteger denominator;

  /**
   * Private constructor of {@code BigRational} instances; prevents
   * instantiation by client.
   *
   * <p>
   *   {@code BigRational} instances are <em>normalised</em>. This means the
   *   following things happen with their passed-in numerators and denominators
   *   when created:
   *   <ul>
   *     <li>
   *       Their numerators and denominators are made to be as small as
   *       possible, e.g. {@code "2/4"} becomes {@code "1/2"}.
   *     </li>
   *     <li>If {@code denominator < 0}, then the sign values of
   *     {@code numerator} and {@code denominator} are inverted, e.g.
   *     {@code "1/-2"} becomes {@code "-1/2"}.</li>
   *   </ul>
   * </p>
   *
   * @param numerator   the numerator
   * @param denominator the denominator
   */
  private BigRational(BigInteger numerator, BigInteger denominator) {
    BigInteger num = safeInstance(
        requireNonNull(numerator, "numerator is null"));
    BigInteger den = safeInstance(
        requireNonNull(denominator, "denominator is null"));

    if (den.equals(BigInteger.ZERO)) {
      throw new IllegalArgumentException("denominator is zero");
    }

    // Reduce the fraction to prevent excessive memory usage
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
   * Defensively copies a new {@code BigInteger} if {@code val} is an untrusted subclass of {@code BigInteger},
   * otherwise returns {@code val}.
   *
   * <p>The purpose of this method is to guard against client code attacks which attempt to pass an instance of an
   * subclass of {@code BigInteger} whose members may violate the invariants of {@code BigInteger}. This is required
   * because this class depends on {@code BigInteger} being immutable to properly function.</p>
   *
   * @param val the instance to check.
   * @return a {@code BigInteger} copy of {@code val} if its class is not {@code BigInteger}, otherwise {@code val}
   * itself.
   */
  private static BigInteger safeInstance(BigInteger val) {
    if (val.getClass() != BigInteger.class) {
      return new BigInteger(val.toByteArray());
    }
    return val;
  }

  public static BigRational of(BigInteger number) {
    return of(number, BigInteger.ONE);
  }

  public static BigRational of(BigInteger numerator, BigInteger denominator) {
    return new BigRational(numerator, denominator);
  }

  public static BigRational of(long number) {
    return of(BigInteger.valueOf(number));
  }

  public static BigRational of(long numerator, long denominator) {
    return of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
  }

  public static BigRational of(String value) {
    return null;
  }

  /**
   * Compares two BigRational objects, {@code this} and {@code that}, which have unequal denominators.
   *
   * @param that The object to compare against {@code this}.
   * @return a negative integer, zero, or a positive integer as {@code this} is less than, equal to, or greater than
   * {@code that}.
   */
  private int compareWithUnequalDenominators(BigRational that) {
    // Use cross-multiplication
    BigInteger numThis = numerator.multiply(that.denominator);
    BigInteger numThat = that.numerator.multiply(denominator);
    return numThis.compareTo(numThat);
  }

  /**
   * @param val the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is {@code null}.
   */
  @Override public int compareTo(BigRational val) {
    return denominator.compareTo(val.denominator) == 0
        ? numerator.compareTo(val.numerator)
        : compareWithUnequalDenominators(val);
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
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
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
   *
   * @return
   */
  @Override public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  /**
   * Returns the numerator of this {@code BigRational} number.
   *
   * @return the numerator of {@code this}.
   */
  public BigInteger numerator() {
    return numerator;
  }

  /**
   * Returns the decimal {@code String} representation of this {@code BigRational} number. The string is of the format
   * {@code "num/den"}, where {@code num} is the numerator represented as a sequence of decimal digits, and
   * {@code den} is the denominator also represented as a sequence of decimal digits. For example, {@code "1/2"},
   * {@code "-1/2"} and {@code "34/567"} are valid string representations of possible {@code BigRational} numbers,
   * but {@code "1.5/2.3"}, {@code "1/-2"} and {@code "-1/-2"} are not.
   *
   * <p>(This representation is compatible with the {@link BigRational#of(String)} <em>static
   * factory method</em>, and allows for String concatenation with Java's {@code +} operator.)</p>
   *
   * @return the string representation of {@code this}.
   */
  @Override public String toString() {
    return denominator.equals(BigInteger.ONE)
        ? numerator.toString()
        : numerator.toString() + "/" + denominator.toString();
  }

}
