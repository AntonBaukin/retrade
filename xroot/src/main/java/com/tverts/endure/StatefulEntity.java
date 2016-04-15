package com.tverts.endure;

/**
 * Defines that entity has a state.
 *
 * @author anton.baukin@gmail.com
 */
public interface StatefulEntity extends NumericIdentity
{
	/* public: StatefulEntity interface */

	public EntityState getEntityState();
}