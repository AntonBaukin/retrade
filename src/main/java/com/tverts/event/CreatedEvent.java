package com.tverts.event;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;


/**
 * Domain event raised after entity
 * (and the related ones) were created
 * and saved to the database.
 *
 * @author anton.baukin@gmail.com
 */
public class CreatedEvent extends ActiveEventBase
{
	/* public: constructor */

	public CreatedEvent(NumericIdentity target)
	{
		super(target);
	}


	/* public: CreatedEvent (bean) interface */

	public boolean      isFlushSession()
	{
		return flushSession;
	}

	public CreatedEvent setFlushSession(boolean flushSession)
	{
		this.flushSession = flushSession;
		return this;
	}


	/* protected: ActiveEventBase interface */

	protected void act()
	{
		if(isFlushSession())
			session().flush();
	}


	/* private: event properties */

	private boolean flushSession = true;
}