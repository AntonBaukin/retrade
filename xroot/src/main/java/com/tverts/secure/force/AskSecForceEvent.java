package com.tverts.secure.force;

/* com.tverts: events */

import com.tverts.event.EventBase;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;


/**
 * This event is sent when there is a request
 * to set permissions to the target login
 * of the Security Force defined by it's UID.
 *
 * It's assumed that the Force with that UID
 * would react.
 *
 * Additional parameters are passed as event
 * {@link #get(Object)}, and
 * {@link #put(Object, Object)}.
 *
 * The Force works in the Domain of the Login.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AskSecForceEvent extends EventBase
{
	/* public: constructors */

	public AskSecForceEvent(AuthLogin login)
	{
		super(login);
	}


	/* public: AskSecForceEvent interface */

	public AuthLogin         target()
	{
		return (AuthLogin) super.target();
	}

	public String            getForce()
	{
		return force;
	}

	public AskSecForceEvent  setForce(String force)
	{
		this.force = force;
		return this;
	}


	/* private: event parameters */

	private String force;
}