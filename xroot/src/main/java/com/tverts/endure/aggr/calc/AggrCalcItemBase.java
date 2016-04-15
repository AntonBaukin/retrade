package com.tverts.endure.aggr.calc;

/* com.tverts: endure core */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;


/**
 * Abstract component of the Aggregation calculation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrCalcItemBase
       extends        NumericBase
       implements     TxEntity
{
	/* public: AggrCalcItem (bean) interface */

	public AggrCalc getAggrCalc()
	{
		return aggrCalc;
	}

	public void     setAggrCalc(AggrCalc aggrCalc)
	{
		this.aggrCalc = aggrCalc;
	}


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


	/* persisted attributes */

	private AggrCalc aggrCalc;
}