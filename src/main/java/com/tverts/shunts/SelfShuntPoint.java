package com.tverts.shunts;

/* com.tverts: shunts (protocol) */

import com.tverts.shunts.protocol.SeShProtocolFinish;
import com.tverts.shunts.protocol.SeShProtocolWebMulti;
import com.tverts.shunts.protocol.SeShRequestInitial;

/* com.tverts: shunts (service) */

import com.tverts.shunts.service.SelfShuntService;
import com.tverts.shunts.service.SelfShuntsSet;
import com.tverts.shunts.service.SelfShuntsRefsSet;

/**
 * Stores shared properties of the
 * Self Shunting Subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class SelfShuntPoint
{
	/*  Singleton */

	private static SelfShuntPoint INSTANCE =
	  new SelfShuntPoint();

	public static SelfShuntPoint getInstance()
	{
		return INSTANCE;
	}

	protected SelfShuntPoint()
	{}

	/* public: log destinations */

	/**
	 * General lof that may be used within the Shunt Units.
	 */
	public static final String LOG_SHARED  =
	  "com.tverts.shunts";

	/**
	 * Log destination for the Self Shunt Service.
	 * Note that it's root is the services root,
	 * not the shunts package.
	 */
	public static final String LOG_SERVICE =
	  "com.tverts.system.services.SelfShuntService";

	/**
	 * Log destination of various system components
	 * of the self shunting implementation.
	 */
	public static final String LOG_SYSTEM  =
	  "com.tverts.shunts.system";

	/* public: Self Shunt Facade */

	public SelfShuntService service()
	  throws IllegalStateException
	{
		SelfShuntService service = getService();

		if(service == null)
			throw new IllegalStateException(
			  "Self Shunt Service is not installed!");

		if(!service.getServiceStatus().isActive())
			throw new IllegalStateException(String.format(
			  "Self Shunt Service '%s' is not active!",
			  service.getServiceInfo().getServiceSignature()));

		return service;
	}

	public void enqueueSelfShuntWeb (
	              SeShRequestInitial request
	            )
	{
		service().enqueueProtocol(
		  new SeShProtocolWebMulti(request));
	}

	public void enqueueSelfShuntWeb (
	              SeShRequestInitial request,
	              SeShProtocolFinish finish
	            )
	{
		SeShProtocolWebMulti protocol =
		  new SeShProtocolWebMulti(request);

		protocol.setProtocolFinish(finish);
		service().enqueueProtocol(protocol);
	}

	/* public: access Self Shunts properties */

	/**
	 * The primary Self Shunt Service used in the system.
	 * This service is used when placing the Self Shunt
	 * Units via this point.
	 */
	public SelfShuntService getService()
	{
		return service;
	}

	public void             setService(SelfShuntService service)
	{
		this.service = service;
	}

	/**
	 * Shared within all Self Shunt Service instances.
	 * Tells whether they may run the shunts. If not,
	 * the services would not be active.
	 */
	public boolean          isActive()
	{
		return active;
	}

	public void             setActive(boolean active)
	{
		this.active = active;
	}

	public SelfShuntsSet    getShuntsSet()
	{
		return shuntsSet;
	}

	public void             setShuntsSet(SelfShuntsSet shuntsSet)
	{
		if(shuntsSet == null)
			throw new IllegalArgumentException();
		this.shuntsSet = shuntsSet;
	}

	/* private: primary Self Shunt Service reference */

	private SelfShuntService service;

	/* private: self shunts properties */

	private SelfShuntsSet    shuntsSet =
	  new SelfShuntsRefsSet();

	private boolean          active    = true;
}
