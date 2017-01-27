package com.tverts.endure;

/**
 * Extends basic identity class to support
 * transaction numbers.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericTxBase
       extends        NumericBase
       implements     TxEntity
{
	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}
}