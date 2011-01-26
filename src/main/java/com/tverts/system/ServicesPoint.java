package com.tverts.system;

/* standard Java classes */

import java.util.List;

/* tverts.com: system services */

import com.tverts.system.services.ServicesList;

/**
 * Singleton point to access the system services configured
 * as Spring Beans.
 *
 * Note that the services are collected at the configuration
 * time, when the list of root references is provided.
 * Consider {@link ServicesList}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   ServicesPoint
       extends ServicesList
{
	/* public: Singleton */

	public static ServicesPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ServicesPoint INSTANCE =
	  new ServicesPoint();

	protected ServicesPoint()
	{}

	/* public: log destinations */

	public static final String LOG_SERVICE_MAIN =
	  "com.tverts.system.services";

	public static final String LOG_SERVICE_BOOT =
	  "com.tverts.system.services.boot";

	/* public: log utilities */

	public static void appendServicesList (
	              StringBuilder string,
	              List<Service> services
	            )
	{
		int len = string.length();

		for(Service service : services) string.
			append((string.length() != len)?(", "):("")).
			append(service.getServiceInfo().getServiceSignature());
	}

	public static void appendActiveServicesList (
	              StringBuilder string,
	              List<Service> services
	            )
	{
		int len = string.length();

		for(Service service : services)
			if(service.getServiceInfo().isActiveService()) string.
				append((string.length() != len)?(", "):("")).
				append(service.getServiceInfo().getServiceSignature());
	}
}