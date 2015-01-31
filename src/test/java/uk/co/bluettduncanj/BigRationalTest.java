package uk.co.bluettduncanj;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class BigRationalTest {

  @Test
  public void testCompareTo() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testDenominator() throws Exception {
    final BigRational tenth = BigRational.valueOf(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Denominator has mutated.", BigInteger.TEN, tenth.denominator());
  }

  @Test
  public void testEquals() throws Exception {
    final BigRational half = BigRational.valueOf(1, 2);
    final BigRational twoQuarters = BigRational.valueOf(2, 4);
    final BigRational threeSixths = BigRational.valueOf(3, 6);
    final BigRational eightFifths = BigRational.valueOf(8, 5);

    // Test reflexivity of equals()
    assertEquals("Not equal to itself.", half, half);

    // Test non-nullity of equals()
    assertNotEquals("Equal to null.", half, null);
    assertNotEquals("Equal to null.", twoQuarters, null);
    assertNotEquals("Equal to null.", threeSixths, null);
    assertNotEquals("Equal to null.", eightFifths, null);

    // Test symmetry of equals()
    assertTrue("Not symmetric.", half.equals(twoQuarters) && twoQuarters.equals(half));

    // Test transitivity of equals()
    final boolean transitive = half.equals(twoQuarters)
                               && twoQuarters.equals(threeSixths)
                               && half.equals(threeSixths);
    assertTrue("Not transitive.", transitive);

    // Test consistency of equals()
    for (int i = 0; i < 100; i++) {
      assertEquals("Not consistent.", half, twoQuarters);
    }
    for (int i = 0; i < 100; i++) {
      assertNotEquals("Not consistent.", eightFifths, half);
    }
  }

  @Test
  public void testHashCode() throws Exception {
    final BigRational half = BigRational.valueOf(1, 2);
    final BigRational twoQuarters = BigRational.valueOf(2, 4);

    assertTrue("Hash codes retrieved twice from same object are not the same.",
               half.hashCode() == half.hashCode());
    assertTrue("Hash codes between two equal objects are not the same.",
               half.hashCode() == twoQuarters.hashCode());
  }

  @Test
  public void testNumerator() throws Exception {
    final BigRational tenth = BigRational.valueOf(BigInteger.ONE, BigInteger.TEN);
    assertEquals("Numerator has mutated.", BigInteger.ONE, tenth.numerator());
  }

  @Test
  public void testToString() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testValueOf() throws Exception {
    fail("Not yet implemented.");

    BigRational impostorRational = BigRational.valueOf(new BigIntegerImpostor());
    Class<?> numeratorClass = impostorRational.numerator().getClass();
    final boolean condition = numeratorClass.equals(BigInteger.class);
    assertTrue("Does not protect against impostor subclasses.", condition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfDenominatorIsZero() throws Exception {
    fail("Not yet implemented.");

    BigRational.valueOf(BigInteger.valueOf(1), BigInteger.valueOf(0));
  }

  @Test
  public void testValueOf1() throws Exception {
    fail("Not yet implemented.");

    BigIntegerImpostor impostorInteger = new BigIntegerImpostor();
    BigRational impostorRational = BigRational.valueOf(impostorInteger, impostorInteger);
    Class<?> numeratorClass = impostorRational.numerator().getClass();
    Class<?> denominatorClass = impostorRational.denominator().getClass();
    final boolean condition = numeratorClass.equals(BigInteger.class)
        && denominatorClass.equals(BigInteger.class);
    assertTrue("Does not protect against impostor subclasses.", condition);
  }

  @Test
  public void testValueOf2() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testalueOf3() throws Exception {
    fail("Not yet implemented.");
  }

  @Test
  public void testValueOf4() throws Exception {
    fail("Not yet implemented.");
  }

  /**
   * A test class used to verify that {@code BigRational} does not accept instances of untrusted
   * BigInteger subclasses for any of its appropriate method parameters.
   */
  private static class BigIntegerImpostor extends BigInteger {
    public BigIntegerImpostor() {
      super("1");
    }
  }

}