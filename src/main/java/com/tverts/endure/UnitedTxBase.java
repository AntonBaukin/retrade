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

		if((getUnity() != null) && (txn != null) && !txn.equals(getUnity().getTxn()))
			getUnity().setTxn(txn);
	}


	/* public: United interface */

	public void setUnity(Unity unity)
	{
		super.setUnity(unity);

		if((unity != null) && (txn != 0L) && !getTxn().equals(unity.getTxn()))
			if(unity.getTxn() > getTxn())
				this.txn = unity.getTxn();
			else
				unity.setTxn(getTxn());
	}
}