package com.tverts.shunts;

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
	public static final Class  LOG_SERVICE =
	  SelfShuntService.class;

	/**
	 * Log destination of various system components
	 * of the self shunting implementation.
	 */
	public static final String LOG_SYSTEM  =
	  "com.tverts.shunts.system";


	/* public: access Self Shunts properties */

	/**
	 * Globally assigned set of all the Self-Shunts
	 * written for the system.
	 */
	public SelfShuntsSet getShuntsSet()
	{
		return shuntsSet;
	}

	public void setShuntsSet(SelfShuntsSet shuntsSet)
	{
		if(shuntsSet == null)
			throw new IllegalArgumentException();
		this.shuntsSet = shuntsSet;
	}


	/* private: self shunts properties */

	private SelfShuntsSet shuntsSet =
	  new SelfShuntsRefsSet();
}
