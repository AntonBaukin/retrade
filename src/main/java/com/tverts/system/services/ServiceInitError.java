package com.tverts.system.services;

/* com.tverts: system services */

import com.tverts.system.Service;

/**
 * Occures in the phases of initialization, start,
 * stop and free of the system services.
 *
 * @author anton.baukin@gmail.com
 */
public class   ServiceInitError
       extends RuntimeException
{
	/* public: constructor */

	public ServiceInitError(Service service, Throwable cause)
	{
		super(cause);
		this.service = service;
	}

	/* public: ServiceInitError interface */

	public final Service getService()
	{
		return service;
	}

	/* private: the service of error */

	private Service service;
}