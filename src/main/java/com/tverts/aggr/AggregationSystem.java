package com.tverts.aggr;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxContextWrapper;
import com.tverts.system.tx.TxPoint;


/**
 * Implements system-level issues of root aggregators.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggregationSystem
       implements     Aggregator
{
	/* protected: aggregation transaction context */

	protected void installAggrTx(AggrJob job)
	{
		//?: {has no transaction context} install the default one
		if(job.aggrTx() == null)
			installDefaultAggrTx(job);
	}

	protected void installDefaultAggrTx(AggrJob job)
	{
		job.aggrTx(new AggrTxContext(TxPoint.txContext()));
	}


	/* protected: aggregation transaction context implementation */

	protected static class AggrTxContext
	          extends      TxContextWrapper
	          implements   AggrTx
	{
		/* public: constructor */

		public AggrTxContext(TxContext tx)
		{
			super(tx);
		}
	}
}