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

	public long getTxn()
	{
		return txn;
	}

	private long txn;

	public void setTxn(long txn)
	{
		this.txn = txn;
	}


	/* persisted attributes */

	private AggrCalc aggrCalc;
}