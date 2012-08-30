package com.tverts.endure.aggr.calc;

/* com.tverts: endure core */

import com.tverts.endure.NumericBase;


/**
 * Abstract component of the Aggregation calculation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrCalcItemBase extends NumericBase
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


	/* persisted attributes */

	private AggrCalc aggrCalc;
}