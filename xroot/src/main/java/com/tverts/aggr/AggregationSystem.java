package com.tverts.aggr;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;
import com.tverts.system.tx.TxWrapperBase;


/**
 * Implements system-level issues of root aggregators.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggregationSystem
       implements     Aggregator
{
	/* protected: aggregation transaction context */

	protected void installTx(AggrJob job)
	{
		//?: {has no transaction context} install the default one
		if(job.aggrTx() == null)
			installDefaultTx(job);
	}

	protected void installDefaultTx(AggrJob job)
	{
		job.aggrTx(new TxContext(TxPoint.txContext()));
	}


	/* protected: aggregation transaction context implementation */

	protected static class TxContext
	          extends      TxWrapperBase
	          implements   Tx
	{
		/* public: constructor */

		public TxContext(Tx tx)
		{
			super(tx);
		}
	}
}