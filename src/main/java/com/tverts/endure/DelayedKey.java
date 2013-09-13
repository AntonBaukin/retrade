package com.tverts.endure;

/**
 * Strategy to obtain primary key of some entity
 * in the time the key is needed.
 *
 * @author anton.baukin@gmail.com
 */
public interface DelayedKey
{
	/* public: DelayedKey interface */

	public Long delayedKey();
}