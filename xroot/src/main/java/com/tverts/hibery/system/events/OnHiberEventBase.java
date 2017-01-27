package com.tverts.hibery.system.events;

/* Hibernate Persistence Layer */

import com.tverts.hibery.system.events.OnHiberEvent;
import org.hibernate.event.spi.PostLoadEvent;

/* com.tverts: hibery system */


/**
 * Implementation base for listener
 * of various Hibernate events.
 *
 * @author anton.baukin@gmail.com
 */
public class OnHiberEventBase implements OnHiberEvent
{
	/* public: OnEvent interface */

	public void onHiberEvent(Object e)
	{
		if(e instanceof PostLoadEvent)
			onPostLoad((PostLoadEvent) e);
	}


	/* protected: supported events */

	protected void onPostLoad(PostLoadEvent e)
	{}
}