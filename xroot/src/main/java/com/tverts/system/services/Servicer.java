package com.tverts.system.services;

/**
 * Coordinator of the services.
 *
 * @author anton.baukin@gmail.com
 */
public interface Servicer
{
	/* public: Servicer interface */

	/**
	 * Returns the direct instance of the service
	 * registered thus allowing direct synchronous
	 * invocations of the service.
	 */
	public Service  service(String suid);

	/**
	 * Sends event to the registered service
	 * defined by UID {@link Event#getService()}.
	 * Leave this field undefined to broadcast.
	 */
	public void     send(Event event);

	/**
	 * Returns a read-only array of the registered
	 * services UIDs. The UIDs are in the order
	 * of services dependencies: each service with
	 * some index does not depend on a service with
	 * bigger index.
	 */
	public String[] services();
}