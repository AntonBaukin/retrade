package com.tverts.endure.aggr.calc;

/* com.tverts: endure core */

import com.tverts.endure.NumericIdentity;


/**
 * Abstract component of the Aggregation calculation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrCalcItemBase implements NumericIdentity
{
	/* public: NumericIdentity interface */

	public Long     getPrimaryKey()
	{
		return primaryKey;
	}

	public void     setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: AggrCalcItem (bean) interface */

	public AggrCalc getAggrCalc()
	{
		return aggrCalc;
	}

	public void     setAggrCalc(AggrCalc aggrCalc)
	{
		this.aggrCalc = aggrCalc;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(!this.getClass().equals(o.getClass()))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((NumericIdentity)o).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int     hashCode()
	{
		Long k0 = this.getPrimaryKey();
		return (k0 == null)?(0):(k0.hashCode());
	}


	/* persisted attributes */

	private Long     primaryKey;
	private AggrCalc aggrCalc;
}