package com.tverts.secure.session;

import java.io.Serializable;

/**
 * Defines session expire strategy.
 *
 * @author anton.baukin@gmail.com
 */
public interface ExpireStrategy
       extends   Serializable
{
	/* public: ExpireStrategy interface */

	/**
	 * Checks the session is expired. Must be
	 * invoked before touching the session
	 * (when not creating one).
	 */
	public boolean isExpired(SecSession session);

	/**
	 * Touches the session thus reactivating it.
	 * Must also be called after creating the session
	 * before the first expired check.
	 */
	public void    touch(SecSession session);
}
