package com.tverts.system;

/**
 * Provides some information on the service.
 * Used for logging and by representation
 * layers.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface ServiceInfo
{
	/* public: ServiceInfo interface */

	public String  getServiceName();

	public String  getServiceTitle(String lang);

	/**
	 * Tells whether this service is designed to has
	 * it's own behaviour, i.e. running threads (tasks).
	 * Te know whether the service is actually running
	 * check {@link ServiceStatus#isActive()}.
	 */
	public boolean isActiveService();
}