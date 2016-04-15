package com.tverts.api.core;

/**
 * Represents API Entity having Tx number.
 */
public interface TxObject
{
	/* public: TxObject interface */

	/**
	 * Transaction number of last insert-update
	 * operation in ReTrade system.
	 */
	public Long getTx();

	public void setTx(Long tx);
}