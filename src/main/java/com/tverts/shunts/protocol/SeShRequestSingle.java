package com.tverts.shunts.protocol;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Initial request to call the shunt unit
 * registered by it's name.
 *
 * The key of this request is the key of
 * the unit name.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SeShRequestSingle
       implements SeShRequestInitial
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public SeShRequestSingle(String unitName)
	{
		if((unitName = s2s(unitName)) == null)
			throw new IllegalArgumentException();

		this.unitName = unitName;
	}


	/* public: SeShRequest interface */

	public Object getSelfShuntKey()
	{
		return unitName;
	}

	public String getContextUID()
	{
		return contextUID;
	}

	public void   setContextUID(String contextUID)
	{
		this.contextUID = contextUID;
	}


	/* private: the protocol parameters */

	private String contextUID;
	private String unitName;
}