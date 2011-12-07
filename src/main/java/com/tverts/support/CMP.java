package com.tverts.support;

/* standard Java classes */

import java.math.BigDecimal;


/**
 * Various comparisons.
 *
 * @author anton.baukin@gmail.com
 */
public class CMP
{
	/* decimal values */

	public static boolean grZero(BigDecimal n)
	{
		return (n != null) && (BigDecimal.ZERO.compareTo(n) < 0);
	}


	/* general equality */

	public static boolean eq(Object a, Object b)
	{
		return ((a == null) && (b == null)) ||
		  ((a != null) && a.equals(b));
	}
}