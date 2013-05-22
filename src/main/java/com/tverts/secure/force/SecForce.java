package com.tverts.secure.force;

/* com.tverts: events */

import com.tverts.event.Reactor;

/* com.tverts: endure (auth + secure) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.secure.SecRule;


/**
 * Security Force is an algorithm that
 * creates and maintains Security Rules.
 *
 * It reacts on system events, and is
 * the key actor of the system security.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SecForce extends Reactor
{
	/* public: SecForce interface */

	/**
	 * Unique ID of the Security Force instance
	 * (within all other Force instances).
	 */
	public String uid();

	/**
	 * Initializes this Force instance during
	 * the system start.
	 */
	public void   init();

	public String getTitle();

	public String getTitle(SecRule rule);

	public String getDescr();

	public String getDescr(SecRule rule);
}