package com.tverts.retrade.domain.selset;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.OxNumericBase;


/**
 * Items of an Selection Set.
 *
 * @author anton.baukin@gmail.com
 */
public class SelItem extends OxNumericBase
{
	/* Selection Set Item (bean) */

	public SelSet getSelSet()
	{
		return selSet;
	}

	private SelSet selSet;

	public void setSelSet(SelSet selSet)
	{
		this.selSet = selSet;
	}

	public Long getObject()
	{
		return (object == 0L)?(null):(object);
	}

	private long object;

	public void setObject(Long object)
	{
		this.object = (object == null)?(0L):(object);
	}

	/**
	 * Long or short name of Java class of ox-instance assigned.
	 */
	public String getOxClass()
	{
		return oxClass;
	}

	private String oxClass;

	public void setOxClass(String oxClass)
	{
		this.oxClass = oxClass;
	}
}