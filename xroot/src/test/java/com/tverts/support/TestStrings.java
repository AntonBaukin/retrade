package com.tverts.support;

/* Java */

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

	@org.junit.Test
	public void testSplit()
	{
		eqs2a("");
		eqs2a("a", "a");
		eqs2a("abc", "abc");
		eqs2a("a-bc", "a-bc");
		eqs2a("a-b c", "a-b", "c");
		eqs2a("a-b ..c/d/e", "a-b", "c/d/e");
		eqs2a("a:b c d  ef   ", "a:b", "c", "d", "ef");
	}

	private static void eqs2a(String a, String... b)
	{
		String[] aa = SU.s2a(a);
		EX.assertx(aa.length == b.length);
		for(int i = 0;(i < aa.length);i++)
			EX.assertx(aa[i].equals(b[i]));
	}
}