package com.tverts.system;

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


	/* private: services */

	private Service[] services = new Service[0];
}