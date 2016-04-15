package com.tverts.system.tx;

/**
 * Transaction Wrapper does not expose new
 * or a nested transaction. It wraps the existing
 * one with extended interface related to the area
 * the transaction is used.
 *
 * Wrapping transaction has the same transaction
 * number ({@link #txn()}).
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface TxWrapper extends Tx
{
	/* public: TxWrapper interface */

	public Tx getWrappedTx();
}