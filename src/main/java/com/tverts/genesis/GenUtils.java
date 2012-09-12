package com.tverts.genesis;

/* standard Java classes */

import java.util.Random;


/**
 * Genesis support utilities.
 *
 * @author anton.baukin@gmail.com
 */
public class GenUtils
{
	/* random values */

	public static String number(Random rnd, int len)
	{
		StringBuilder s = new StringBuilder(len);

		while(len-- > 0)
			s.append(rnd.nextInt(10));
		return s.toString();
	}
}