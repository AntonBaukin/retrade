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
	          extends      TxWrapperBase
	          implements   AggrTx
	{
		/* public: constructor */

		public AggrTxContext(Tx tx)
		{
			super(tx);
		}
	}
}