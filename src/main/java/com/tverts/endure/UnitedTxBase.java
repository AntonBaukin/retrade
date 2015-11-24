package com.tverts.endure;

import com.tverts.support.CMP;

/**
 * Basic implementation of {@link United} classes
 * with {@link TxEntity} transaction number.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnitedTxBase
       extends        UnitedBase
       implements     TxEntity
{
	/* Tx-Entity */

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
		setUnityTxn();
	}

	public void setUnityTxn()
	{
		//?: {has unity} assign to it
		if(getUnity() != null)
			getUnity().setTxn(this);
	}


	/* United */

	public void setUnity(Unity unity)
	{
		super.setUnity(unity);

		//?: {has no unity}
		if(unity == null)
			return;

		//~: update tx-number of the unity
		setUnityTxn();

		//?: {update own tx-number}
		if(CMP.txn(unity, this))
			this.txn = unity.getTxn();
	}
}