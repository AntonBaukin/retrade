package com.tverts.retrade.domain.selset;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.NumericBase;


/**
 * Items of an Selection Set.
 *
 * @author anton.baukin@gmail.com
 */
public class SelItem extends NumericBase
{
	/* public: SelItem (bean) interface */

	public SelSet getSelSet()
	{
		return selSet;
	}

	public void   setSelSet(SelSet selSet)
	{
		this.selSet = selSet;
	}

	public long   getObject()
	{
		return object;
	}

	public void   setObject(long object)
	{
		this.object = object;
	}


	/* selection item attributes */

	private SelSet selSet;
	private long   object;
}