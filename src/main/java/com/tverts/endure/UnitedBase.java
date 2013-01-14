package com.tverts.endure;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Entity;


/**
 * {@link United} instance has it unified mirror
 * {@link Unity} not obligatory. At the most cases
 * such a classes are candidates to be an {@link Entity}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnitedBase
       extends        NumericBase
       implements     United
{
	/* public: United interface */

	public Unity getUnity()
	{
		return unity;
	}

	public void  setUnity(Unity unity)
	{
		this.unity = unity;
	}


	/* the unified mirror */

	private Unity unity;
}