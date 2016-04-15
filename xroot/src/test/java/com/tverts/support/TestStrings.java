package com.tverts.support;

/* standard Java classes */

import java.util.Random;


/**
 * Tests of String Utilities.
 *
 * @author anton.baukin@gmail.com.
 */
public class TestStrings
{
	@org.junit.Test
	public void testIntegerToHex()
	{
		Random rnd = new Random();

		for(int i = 0;(i < 1000000);i++)
		{
			int    x = rnd.nextInt();
			String R = ((x < 0)?("-"):("")) +
			  Integer.toHexString(Math.abs(x)).toUpperCase();
			String X = SU.i2h(x);

			EX.assertx(R.equals(X), "[", i, "]: ", R, " != ", X);
		}
	}

	@org.junit.Test
	public void testLongToHex()
	{
		Random rnd = new Random();

		for(int i = 0;(i < 1000000);i++)
		{
			long   x = rnd.nextLong();
			String R = ((x < 0L)?("-"):("")) +
			  Long.toHexString(Math.abs(x)).toUpperCase();
			String X = SU.i2h(x);

			EX.assertx(R.equals(X), "[", i, "]: ", R, " != ", X);
		}
	}

}