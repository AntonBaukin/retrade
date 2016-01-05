package com.tverts.support;

/* Java */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.TxEntity;


/**
 * Various comparisons.
 *
 * @author anton.baukin@gmail.com
 */
public class CMP
{
	/* Persistent Objects */

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
		return ((a == null) & (b == null)) | (a == b) ||
		  !((a == null) | (b == null)) && CMP.eq(a.getPrimaryKey(), b.getPrimaryKey());
	}

	public static boolean eq(NumericIdentity a, Long b)
	{
		return (a == null) & (b == null) ||
		  !((a == null) | (b == null)) && CMP.eq(a.getPrimaryKey(), b);
	}

	/**
	 * Tells whether old transactional number may be
	 * replaced with the new one.
	 */
	public static boolean txn(Long n, Long o)
	{
		return (n != null) && ((o == null) || (o.compareTo(n) < 0));
	}

	public static boolean txn(TxEntity n, TxEntity o)
	{
		return CMP.txn(
		  (n == null)?(null):n.getTxn(),
		  (o == null)?(null):o.getTxn()
		);
	}

	public static boolean txn(TxEntity n, Long o)
	{
		return CMP.txn((n == null)?(null):n.getTxn(), o);
	}

	public static boolean txn(Long n, TxEntity o)
	{
		return CMP.txn(n, (o == null)?(null):o.getTxn());
	}


	/* Decimal Values */

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

	public static boolean gre(BigDecimal a, BigDecimal b)
	{
		EX.assertn(a);
		EX.assertn(b);
		return (a.compareTo(b) >= 0);
	}

	public static boolean gr(BigDecimal a, BigDecimal b)
	{
		EX.assertn(a);
		EX.assertn(b);
		return (a.compareTo(b) > 0);
	}


	/* Integers */

	public static int     cmp(Integer a, Integer b)
	{
		return (a == null && b == null)?(0):(a == null)?(-1):
		  (b == null)?(+1):Integer.compare(a, b);
	}


	/* General Equality */

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


	/* Strings Comparison */

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