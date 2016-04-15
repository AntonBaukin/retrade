package com.tverts.endure;

/**
 * Interface to load (or access) instance
 * at the time of need.
 *
 * @author anton.baukin@gmail.com
 */
public interface DelayedEntity
{
	/* public: DelayedEntity interface */

	public NumericIdentity accessEntity();
}