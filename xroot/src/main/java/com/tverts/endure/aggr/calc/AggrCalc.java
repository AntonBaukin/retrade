package com.tverts.endure.aggr.calc;

/* com.tverts: endure core */

import com.tverts.endure.core.Entity;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrValue;


/**
 * Defines calculation over the Aggregated value.
 * The components of the calculation does not store
 * references to the sources of the items of the
 * aggregated value. They contain data (values)
 * defined by (from) that items.
 *
 * The calculated value has no own data fields.
 * It is the facade to store the data in it's
 * components.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrCalc extends Entity
{
	/* public: AggrCalc (bean) interface */

	public AggrValue  getAggrValue()
	{
		return aggrValue;
	}

	public void       setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
	}


	/* persisted attributes */

	private AggrValue aggrValue;
}