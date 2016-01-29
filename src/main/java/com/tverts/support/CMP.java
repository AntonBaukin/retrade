package com.tverts.support;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.TxEntity;


/**
 * Various comparisons.
 *
 * @author anton.baukin@gmail.com
 */
public class CMP extends com.tverts.api.support.CMP
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
}