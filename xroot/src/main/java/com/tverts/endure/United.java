package com.tverts.endure;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Entity;


/**
 * While {@link Entity} subclass requires an instance
 * to have the unified mirror {@link Unity}, {@link United}
 * instance has this relation optionally.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface United extends PrimaryIdentity
{
	/* public: United interface */

	public Unity getUnity();

	public void  setUnity(Unity unity);
}