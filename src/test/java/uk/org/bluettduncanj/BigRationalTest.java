package uk.org.bluettduncanj;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class BigRationalTest {

  @Test public void testCompareTo() throws Exception {
    fail("Not yet implemented.");
  }

  @Test public void testDenominator() throws Exception {
    BigRational tenth = BigRational.of(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Denominator has mutated", BigInteger.TEN,
        tenth.denominator());
  }

  @Test public void testEqualsEnforcesNonNullity() throws Exception {
    BigRational half = BigRational.of(1, 2);
    assertFalse("Equal to null", half.equals(null));
  }

  @Test public void testEqualsEnforcesSelfEquality() throws Exception {
    BigRational half = BigRational.of(1, 2);
    assertTrue("Not equal to itself", half.equals(half));
  }

  @Test public void testEqualsIsConsistent() throws Exception {
    BigRational half = BigRational.of(1, 2);
    BigRational twoQuarters = BigRational.of(2, 4);
    BigRational eightFifths = BigRational.of(8, 5);

    for (int i = 0; i < 10; i++) {
      assertTrue("Not consistent", half.equals(twoQuarters));
      assertFalse("Not consistent", eightFifths.equals(half));
    }
  }

  @Test public void testEqualsIsSymmetric() throws Exception {
    BigRational half = BigRational.of(1, 2);
    BigRational twoQuarters = BigRational.of(2, 4);
    assertTrue("Not symmetric", half.equals(twoQuarters) && twoQuarters.equals(half));
  }

  @Test public void testEqualsIsTransitive() throws Exception {
    BigRational half = BigRational.of(1, 2);
    BigRational twoQuarters = BigRational.of(2, 4);
    BigRational threeSixths = BigRational.of(3, 6);
    assertTrue("Not transitive", half.equals(twoQuarters)
        && twoQuarters.equals(threeSixths)
        && half.equals(threeSixths));
  }

  @Test public void testHashCode() throws Exception {
    fail("Not yet implemented.");
  }

  @Test public void testNumerator() throws Exception {
    BigRational tenth = BigRational.of(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Numerator has mutated", BigInteger.ONE, tenth.numerator());
  }

  @Test public void testToString() throws Exception {
    fail("Not yet implemented.");
  }

  @Test public void testOf() throws Exception {
    fail("Not yet implemented.");

    BigRational number = BigRational.of(impostor);
    assertTrue("Does not protect against impostor subclasses",
        number.numerator().getClass().equals(BigInteger.class));
  }

  @Test public void testOf1() throws Exception {
    fail("Not yet implemented.");

    BigRational number = BigRational.of(impostor, impostor);
    Class<?> numeratorClass = number.numerator().getClass();
    Class<?> denominatorClass = number.denominator().getClass();
    assertTrue("Does not protect against impostor subclasses",
        numeratorClass.equals(BigInteger.class)
            && denominatorClass.equals(BigInteger.class));
  }

  @Test public void testOf2() throws Exception {
    fail("Not yet implemented.");
  }

  @Test public void testOf3() throws Exception {
    fail("Not yet implemented.");
  }

  @Test public void testOf4() throws Exception {
    fail("Not yet implemented.");
  }

  /**
   * A test class to verify that {@code BigRational} does
   * not accept subclasses of BigInteger for any of its
   * noted method parameters.
   */
  private static class BigIntegerImpostor extends BigInteger {
    public BigIntegerImpostor(String val) {
      super(val);
    }
  }

  private static final BigIntegerImpostor impostor =
      new BigIntegerImpostor("1");

}