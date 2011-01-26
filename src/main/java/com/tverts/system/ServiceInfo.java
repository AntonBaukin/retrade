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

	/**
	 * Service signature is the JVM unique name of the service.
	 *
	 * Not obliged, it consist of two parts separated with '#':
	 * the name of the service, and identity hash of the
	 * service's object.
	 */
	public String  getServiceSignature();

	public String  getServiceTitle(String lang);

	/**
	 * Tells whether this service is designed to has
	 * it's own behaviour, i.e. running threads (tasks).
	 * Te know whether the service is actually running
	 * check {@link ServiceStatus#isActive()}.
	 */
	public boolean isActiveService();
}