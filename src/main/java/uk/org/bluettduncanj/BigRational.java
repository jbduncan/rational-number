package uk.org.bluettduncanj;

import java.math.BigInteger;

/**
 * An unlimited-size, <em>immutable</em> rational number (fraction) with an arbitrarily large numerator and
 * denominator.
 *
 * <p>This class does not provide any constructors, instead it provides <em>static method factories</em> for
 * construction of {@code BigRational} instances.
 *   <ul>
 *     <li>{@link uk.org.bluettduncanj.BigRational#of(java.math.BigInteger)}</li>
 *     <li>{@link uk.org.bluettduncanj.BigRational#of(java.math.BigInteger, java.math.BigInteger)}</li>
 *     <li>{@link uk.org.bluettduncanj.BigRational#of(int)}</li>
 *     <li>{@link uk.org.bluettduncanj.BigRational#of(long)}</li>
 *     <li>{@link uk.org.bluettduncanj.BigRational#of(String)}</li>
 *   </ul>
 * </p>
 *
 * <p>Unless explicitly stated otherwise, all methods in this class throw a {@code NullPointerException} when
 * a {@code null} value is passed to any of their parameters.</p>
 *
 * <p>This class is <em>thread-safe</em>.</p>
 */
public final class BigRational implements Comparable<BigRational> {

  /** The numerator of {@code this}. Must be non-null. */
  private final BigInteger numerator;

  /** The denominator of {@code this}. Must be non-null. */
  private final BigInteger denominator;

  /**
   * Private constructor of {@code BigRational} instances; prevents instantiation by client.
   *
   * @param numerator the numerator.

   * @param denominator the denominator.
   */
  private BigRational(BigInteger numerator, BigInteger denominator) {
    BigInteger num = safeInstance(
        checkNotNull(numerator, "numerator is null"));
    BigInteger den = safeInstance(
        checkNotNull(denominator, "denominator is null"));

    // Reduce the fraction to prevent excessive memory usage
    BigInteger gcd = num.gcd(den);
    num = num.divide(gcd);
    den = num.divide(gcd);

    this.numerator = num;
    this.denominator = den;
  }

  /**
   * Checks that the given {@code object} is not {@code null}, and otherwise throws a {@code NullPointerException} with
   * the given {@code errorMessage}.
   *
   * @param object the object to check.
   * @param errorMessage the message to pass to the {@code NullPointerException} if {@code object} is {@code null}; may
   *                     be {@code null} itself.
   * @param <T> the type of {@code object}.
   * @return {@code object}.
   */
  private static <T> T checkNotNull(T object, String errorMessage) {
    if (object == null) {
      throw new NullPointerException(errorMessage);
    }
    return object;
  }

  /**
   *
   *
   * @param val the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is {@code null}.
   */
  @Override public int compareTo(BigRational val) {
    if (denominator.compareTo(val.denominator) == 0) {
      return numerator.compareTo(val.numerator);
    } else {
      // Use cross-multiplication
      BigInteger thisCrossMultipliedNum = numerator.multiply(val.denominator);
      BigInteger valCrossMultipliedNum = val.numerator.multiply(denominator);
      return thisCrossMultipliedNum.compareTo(valCrossMultipliedNum);
    }
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
   *
   *
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
    return numerator.equals(rational.numerator) && denominator.equals(rational.denominator);
  }

  /**
   *
   *
   * @return
   */
  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + numerator.hashCode();
    result = 31 * result + denominator.hashCode();
    return result;
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
   * Defensively copies a new {@code BigInteger} if {@code val} is an untrusted subclass of {@code BigInteger},
   * otherwise returns {@code val}.
   *
   * <p>The purpose of this method is to guard against client code attacks which attempt to pass an instance of an
   * untrusted subclass of {@code BigInteger}, whose members may violate the invariants of {@code BigInteger}. This is
   * required because this class depends on the immutability of {@code BigInteger}.</p>
   *
   * @param val the instance to check.
   * @return a {@code BigInteger} copy of {@code val} if its class is not {@code BigInteger}, otherwise {@code val}
   *         itself.
   */
  private static BigInteger safeInstance(BigInteger val) {
    if (val.getClass() != BigInteger.class) {
      return new BigInteger(val.toByteArray());
    }
    return val;
  }

  /**
   * Returns the decimal {@code String} representation of this {@code BigRational} number. The string is of the format
   * {@code "num/den"}, where {@code num} is the numerator represented as a sequence of decimal digits, and
   * {@code den} is the denominator also represented as a sequence of decimal digits. For example, {@code "1/2"},
   * {@code "-1/2"} and {@code "34/567"} are valid string representations of possible {@code BigRational} numbers,
   * but {@code "1/-2"} and {@code "-1/-2"} are not.
   *
   * <p>(This representation is compatible with the {@link BigRational#of(String)} <em>static
   * factory method</em>, and allows for String concatenation with Java's {@code +} operator.)</p>
   *
   * @return the string representation of {@code this}.
   */
  @Override public String toString() {
    return numerator.toString() + "/" + denominator.toString();
  }

  /**
   *
   *
   * @param number
   * @return
   */
  public static BigRational of(BigInteger number) {
    return of(number, BigInteger.ONE);
  }

  /**
   *
   *
   * @param numerator
   * @param denominator
   * @return
   */
  public static BigRational of(BigInteger numerator, BigInteger denominator) {
    return new BigRational(numerator, denominator);
  }

  /**
   *
   *
   * @param number
   * @return
   */
  public static BigRational of(int number) {
    return of((long) number);
  }

  public static BigRational of(int numerator, int denominator) {
    return of((long) numerator, (long) denominator);
  }

  /**
   *
   *
   * @param number
   * @return
   */
  public static BigRational of(long number) {
    return of(BigInteger.valueOf(number), BigInteger.ONE);
  }

  public static BigRational of(long numerator, long denominator) {
    return of(
        BigInteger.valueOf(numerator),
        BigInteger.valueOf(denominator));
  }

  public static BigRational of(String value) {
    String[] parts = value.split("/");
    BigInteger num = new BigInteger(parts[0]);
    BigInteger den = new BigInteger(parts[1]);
    return of(num, den);
  }

}
