package com.tverts.system.zservices;

/**
 * Singleton point to access the system services
 * configured as Spring Beans and registered and
 * managed by {@link ServicesSystem} coordinator.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ServicesPoint
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


	/* public: ServicesPoint (static) interface */

	public static ServicesSystem system()
	{
		ServicesSystem system = getInstance().getSystem();

		if(system == null) throw new IllegalStateException(
		  "Services System is not built and attached to the Point!");
		return system;
	}

	public static ServicesPoint  send(String service, Event event)
	{
		event.setService(service);
		system().send(event);

		return getInstance();
	}

	public static ServicesPoint  broadcast(Event event)
	{
		event.setService(null);
		system().send(event);

		return getInstance();
	}


	/* public: ServicesPoint (bean) interface */

	public ServicesSystem getSystem()
	{
		return system;
	}

	public void           setSystem(ServicesSystem system)
	{
		this.system = system;
	}


	/* private: services system */

	private ServicesSystem system;
}
