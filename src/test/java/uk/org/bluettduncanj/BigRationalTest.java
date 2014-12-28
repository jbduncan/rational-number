package uk.org.bluettduncanj;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class BigRationalTest {

  /**
   * A test class used to verify that {@code BigRational}
   * does not accept BigInteger subclass instances for any
   * of its noted method parameters.
   */
  private static class BigIntegerImpostor extends BigInteger {
    public BigIntegerImpostor(String val) {
      super(val);
    }
  }

  @Test
  public void testCompareTo() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testDenominator() throws Exception {
    BigRational tenth = BigRational.of(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Denominator has mutated", BigInteger.TEN,
        tenth.denominator());
  }

  @Test
  public void testEquals() throws Exception {
    BigRational half = BigRational.of(1, 2);
    BigRational twoQuarters = BigRational.of(2, 4);
    BigRational threeSixths = BigRational.of(3, 6);
    BigRational eightFifths = BigRational.of(8, 5);

    // Test reflexivity of equals()
    assertEquals("Not equal to itself", half, half);

    // Test non-nullity of equals()
    assertNotEquals("Equal to null", half, null);
    assertNotEquals("Equal to null", twoQuarters, null);
    assertNotEquals("Equal to null", threeSixths, null);
    assertNotEquals("Equal to null", eightFifths, null);

    // Test symmetry of equals()
    assertTrue("Not symmetric", half.equals(twoQuarters)
        && twoQuarters.equals(half));

    // Test transitivity of equals()
    assertTrue("Not transitive", half.equals(twoQuarters)
        && twoQuarters.equals(threeSixths)
        && half.equals(threeSixths));

    // Test consistency of equals()
    for (int i = 0; i < 100; i++) {
      assertEquals("Not consistent", half, twoQuarters);
    }
    for (int i = 0; i < 100; i++) {
      assertNotEquals("Not consistent", eightFifths, half);
    }
  }

  @Test
  public void testHashCode() throws Exception {
    BigRational half = BigRational.of(1, 2);
    BigRational twoQuarters = BigRational.of(2, 4);

    assertTrue("Hash codes retrieved twice from same object are not the same",
        half.hashCode() == half.hashCode());
    assertTrue("Hash codes between two equal objects are not the same",
        half.hashCode() == twoQuarters.hashCode());
  }

  @Test
  public void testNumerator() throws Exception {
    BigRational tenth = BigRational.of(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Numerator has mutated", BigInteger.ONE, tenth.numerator());
  }

  @Test
  public void testToString() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testOf() throws Exception {
    fail("Not yet implemented.");

    final BigIntegerImpostor impostorInteger = new BigIntegerImpostor("1");
    final BigRational impostorRational = BigRational.of(impostorInteger);
    Class<?> numeratorClass = impostorRational.numerator().getClass();
    assertTrue("Does not protect against impostor subclasses",
        numeratorClass.equals(BigInteger.class));
  }

  @Test
  public void testOf1() throws Exception {
    fail("Not yet implemented.");

    final BigIntegerImpostor impostorInteger = new BigIntegerImpostor("1");
    final BigRational impostorRational =
        BigRational.of(impostorInteger, impostorInteger);
    Class<?> numeratorClass = impostorRational.numerator().getClass();
    Class<?> denominatorClass = impostorRational.denominator().getClass();
    assertTrue("Does not protect against impostor subclasses",
        numeratorClass.equals(BigInteger.class)
            && denominatorClass.equals(BigInteger.class));
  }

  @Test
  public void testOf2() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testOf3() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testOf4() throws Exception {
    fail("Not yet implemented.");
  }

}