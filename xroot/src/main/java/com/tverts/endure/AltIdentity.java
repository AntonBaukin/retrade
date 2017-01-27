package com.tverts.endure;

/**
 * Interface of an entity that has clear
 * alternate key. A key must be an instance
 * with has code and equals method.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface AltIdentity
{
	/* public: AltIdentity interface */

	public Object altKey();
}