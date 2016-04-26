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
		eq_s2aw("");
		eq_s2aw("a", "a");
		eq_s2aw("abc", "abc");
		eq_s2aw("a-bc", "a-bc");
		eq_s2aw("a-b c", "a-b", "c");
		eq_s2aw("a-b ..c/d/e", "a-b", "c/d/e");
		eq_s2aw("a:b c d  ef   ", "a:b", "c", "d", "ef");
		eq_s2aw("\n a:b ef   ", "a:b", "ef");

		eq_s2as("");
		eq_s2as("a", "a");
		eq_s2as("abc", "abc");
		eq_s2as("a bc", "a", "bc");
		eq_s2as("a b,c", "a", "b", "c");
		eq_s2as(" \t a b\n,  c", "a", "b\n", "c");
		eq_s2as("abc, d f, e ", "abc", "d", "f", "e");
	}

	private static void eq_s2aw(String a, String... b)
	{
		String[] aa = SU.s2aw(a);
		EX.assertx(aa.length == b.length);
		for(int i = 0;(i < aa.length);i++)
			EX.assertx(aa[i].equals(b[i]));
	}

	private static void eq_s2as(String a, String... b)
	{
		String[] aa = SU.s2a(a, ',', ';', ' ', '\t');
		EX.assertx(aa.length == b.length);
		for(int i = 0;(i < aa.length);i++)
			EX.assertx(aa[i].equals(b[i]));
	}
}