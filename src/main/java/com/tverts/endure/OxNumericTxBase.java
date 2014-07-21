package com.tverts.endure;

/**
 * Simplification class that adds
 * support for Tx-indexed entities.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class OxNumericTxBase
       extends        OxNumericBase
       implements     TxEntity
{
	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	/**
	 * When updated, transaction number is also
	 * copied to the unified mirror.
	 */
	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}
}