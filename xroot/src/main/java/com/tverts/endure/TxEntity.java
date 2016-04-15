package com.tverts.endure;

/* com.tverts: support */

import com.tverts.support.CMP;


/**
 * Defines an Entity having Transaction Number (txn).
 *
 * Each modifying transaction has unique number that
 * is atomically incremented by the database.
 *
 * When an entity is saved or updated, its txn is set
 * to that number. An entity may be considered as
 * modified when some related object is modified.
 * But in this case the application must manually
 * update the value.
 *
 * Transaction value 0 means it is not set yet.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface TxEntity
{
	/* Transaction Number */

	public Long  getTxn();

	public void  setTxn(Long txn);

	default void setTxn(TxEntity e)
	{
		if((e != null) && CMP.txn(e, this))
			this.setTxn(e.getTxn());
	}
}