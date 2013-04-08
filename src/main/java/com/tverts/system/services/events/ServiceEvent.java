package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;


/**
 * Event sent from a Z-Service. Stores the UID
 * of that service.
 *
 * @author anton.baukin@gmail.com
 */
public interface ServiceEvent extends Event
{
	/* public: ServiceEvent interface */

	public String getSourceService();
}
