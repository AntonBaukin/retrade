package com.tverts.support;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;


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

	public static boolean greZero(BigDecimal n)
	{
		return (n != null) && (BigDecimal.ZERO.compareTo(n) <= 0);
	}


	/* general equality */

	public static boolean eq(Object a, Object b)
	{
		return ((a == null) && (b == null)) ||
		  ((a != null) && a.equals(b));
	}

	public static boolean eq(Date a, Date b)
	{
		return ((a == null) && (b == null)) ||
		  ((a != null) && (a.getTime() == b.getTime()));
	}


	/* strings comparison */

	public static int cmp(String a, String b)
	{
		if((a == null) && (b == null)) return 0;
		if(a == null) return -1; if(b == null) return +1;
		return a.compareTo(b);
	}

	public static int cmpic(String a, String b)
	{
		if((a == null) && (b == null)) return 0;
		if(a == null) return -1; if(b == null) return +1;
		return a.compareToIgnoreCase(b);
	}
}