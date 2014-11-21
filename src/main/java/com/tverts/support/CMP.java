package com.tverts.support;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

import com.tverts.endure.NumericIdentity;


/**
 * Various comparisons.
 *
 * @author anton.baukin@gmail.com
 */
public class CMP
{
	/* persistent objects */

	/**
	 * Compares that two entities has the same key, or
	 * the same object, or both are undefined.
	 *
	 * Note that this implementation does not inspect
	 * the classes of the entities, and keys of different
	 * classes may be the same while the objects are not!
	 */
	public static boolean eq(NumericIdentity a, NumericIdentity b)
	{
		if(((a == null) & (b == null)) | (a == b))
			return true;

		if((a == null) | (b == null))
			return false;

		return CMP.eq(a.getPrimaryKey(), b.getPrimaryKey());
	}

	public static boolean eq(NumericIdentity a, Long b)
	{
		if((a == null) & (b == null))
			return true;

		if((a == null) | (b == null))
			return false;

		return CMP.eq(a.getPrimaryKey(), b);
	}


	/* decimal values */

	public static boolean grZero(BigDecimal n)
	{
		return (n != null) && (BigDecimal.ZERO.compareTo(n) < 0);
	}

	public static boolean greZero(BigDecimal n)
	{
		return (n != null) && (BigDecimal.ZERO.compareTo(n) <= 0);
	}

	public static boolean eqZero(BigDecimal n)
	{
		return (n != null) && (BigDecimal.ZERO.compareTo(n) == 0);
	}

	public static boolean eq(BigDecimal a, BigDecimal b)
	{
		return ((a == null) && (b == null)) ||
		  ((a != null) && (b != null) && (a.compareTo(b) == 0));
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