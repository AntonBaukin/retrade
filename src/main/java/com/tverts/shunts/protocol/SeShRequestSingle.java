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
 * @author anton baukin (abaukin@mail.ru)
 */
public final class SeShRequestSingle
       implements  SeShRequestInitial
{
	public static final long serialVersionUID = 0L;

	/* public: constructor */

	public SeShRequestSingle(String unitName)
	{
		if((unitName = s2s(unitName)) != null)
			throw new IllegalArgumentException();

		this.unitName = unitName;
	}

	/* public: SeShRequest interface */

	public Object getSelfShuntKey()
	{
		return unitName;
	}

	/* private: the unit name */

	private String unitName;
}