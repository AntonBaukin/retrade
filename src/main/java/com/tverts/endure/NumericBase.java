package com.tverts.endure;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;


/**
 * Base class for simple persistent objects.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericBase implements NumericIdentity
{
	/* Numeric Identity */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	private Long primaryKey;

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* Object */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null) return false;

		if(!this.getClass().equals(HiberPoint.type(o)))
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
}