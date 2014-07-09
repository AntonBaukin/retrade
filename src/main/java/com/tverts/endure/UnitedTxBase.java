package com.tverts.endure;

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
	/* public: TxEntity Interface */

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

		//?: {has unity}
		if((getUnity() != null) && (txn != null))
			getUnity().setTxn(txn);
	}


	/* public: United Interface */

	public void setUnity(Unity unity)
	{
		super.setUnity(unity);

		//?: {has no unity}
		if(unity == null)
			return;

		//~: assign the transaction number
		Long ux = unity.getTxn();
		Long tx = this.getTxn();

		if((tx == null) && (ux != null))
			this.txn = ux;
		else if((tx != null) && (ux == null))
			unity.setTxn(tx);
		else if((ux != null) && (tx != null) && (ux > tx))
			this.txn = ux;

	}
}