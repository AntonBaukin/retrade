package com.tverts.shunts;

/* com.tverts: shunts (sets) */

import com.tverts.shunts.sets.SelfShuntsSet;
import com.tverts.shunts.sets.SelfShuntsRefsSet;

/**
 * Stores shared properties of the
 * Self Shunting Subsystem.
 *
 * @author anton baukin (abaukin@mail.ru)
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

	/* public: access self shunts properties */

	/**
	 * Shared within all Self Shunt Service instances.
	 * Tells whether they may run the shunts. If not,
	 * the services would not be active.
	 */
	public boolean       isActive()
	{
		return active;
	}

	public void          setActive(boolean active)
	{
		this.active = active;
	}

	public SelfShuntsSet getShuntsSet()
	{
		return shuntsSet;
	}

	public void          setShuntsSet(SelfShuntsSet shuntsSet)
	{
		if(shuntsSet == null)
			throw new IllegalArgumentException();
		this.shuntsSet = shuntsSet;
	}

	/* private: self shunts properties */

	private SelfShuntsSet shuntsSet =
	  new SelfShuntsRefsSet();

	private boolean       active    = true;
}
