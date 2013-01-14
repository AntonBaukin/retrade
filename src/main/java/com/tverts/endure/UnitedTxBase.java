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
	/* public: TxEntity interface */

	public long getTxn()
	{
		return txn;
	}

	private long txn;

	/**
	 * When updated, transaction number is also
	 * copied to the unified mirror.
	 */
	public void setTxn(long txn)
	{
		this.txn = txn;

		if((getUnity() != null) && (getUnity().getTxn() != txn))
			getUnity().setTxn(txn);
	}


	/* public: United interface */

	public void setUnity(Unity unity)
	{
		super.setUnity(unity);

		if((unity != null) && (unity.getTxn() != getTxn()))
			if(unity.getTxn() > getTxn())
				this.txn = unity.getTxn();
			else
				unity.setTxn(getTxn());
	}
}