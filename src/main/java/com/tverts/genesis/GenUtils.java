package com.tverts.genesis;

/* Java */

import java.util.Random;

/* com.tverts.support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Genesis support utilities.
 *
 * @author anton.baukin@gmail.com
 */
public class GenUtils
{
	public static String number(Random rnd, int len)
	{
		StringBuilder s = new StringBuilder(len);

		for(int i = 0;(i < len);i++)
			//?: {first digit} not zero
			if(i == 0)
				s.append(1 + rnd.nextInt(9));
			else
				s.append(rnd.nextInt(10));

		return s.toString();
	}

	/**
	 * Generates up to maximum number of the phones formed
	 * as prefix with random digits of the length given.
	 */
	public static String phones(Random rnd, String prefix, int max, int len)
	{
		EX.asserts(prefix);
		EX.assertx(max > 0);
		EX.assertx(len > 0);

		String[] ps = new String[1 + rnd.nextInt(max)];
		for(int i = 0;(i < ps.length);i++)
			ps[i] = SU.cats(prefix, number(rnd, len));

		return SU.scats("; ", ps);
	}
}