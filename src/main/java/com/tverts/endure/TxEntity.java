package com.tverts.endure;

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
 *
 * @author anton.baukin@gmail.com
 */
public interface TxEntity
{
	/* public: TxEntity interface */

	public long getTxn();

	public void setTxn(long txn);
}