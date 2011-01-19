package com.tverts.shunts;

/* com.tverts: shunts (sets) */

import com.tverts.shunts.sets.SelfShuntsSet;
import com.tverts.shunts.sets.SelfShuntsRefsSet;

/**
 * Stores shared properties of the
 * Self Shunting Subsystem.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class SelfShuntPoint
{
	/*  Singleton */

	private static SelfShuntPoint INSTANCE =
	  new SelfShuntPoint();

	public static SelfShuntPoint getInstance()
	{
		return INSTANCE;
	}

	protected SelfShuntPoint()
	{}

	/* public: access self shunts properties */

	public SelfShuntsSet
	            getShuntsSet()
	{
		return shuntsSet;
	}

	public void setShuntsSet(SelfShuntsSet shuntsSet)
	{
		if(shuntsSet == null)
			throw new IllegalArgumentException();
		this.shuntsSet = shuntsSet;
	}

	/* private: self shunts properties */

	private SelfShuntsSet shuntsSet =
	  new SelfShuntsRefsSet();
}
