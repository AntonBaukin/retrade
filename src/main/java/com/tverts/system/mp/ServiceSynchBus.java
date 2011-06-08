package com.tverts.system.mp;

/* com.tverts: services */

import com.tverts.system.Service;


/**
 * Observer os services activity synchronization.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ServiceSynchBus
{
	/* listener interface  */

	public static interface ServiceSynchBusListener
	{
		/* public: ServiceSynchBusListener interface */

		public void onServiceCompleted(Service service);
	}


	/* public: ServiceSynchBus interface */

	public void serviceCompleted(Service service);

	public void connectListener(ServiceSynchBusListener ssbl);

	public void removeListener(ServiceSynchBusListener ssbl);
}