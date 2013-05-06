package com.tverts.shunts;

/* standard Java classes */

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/* com.tverts: shunts (service) */

import com.tverts.shunts.service.SelfShuntService;
import com.tverts.shunts.service.SelfShuntsSet;
import com.tverts.shunts.service.SelfShuntsRefsSet;

/* com.tverts: support */

import com.tverts.support.SU;


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


	/* public: SelfShuntPoint (bean) interface */

	public SelfShuntService getService()
	{
		return service;
	}

	public void setService(SelfShuntService service)
	{
		this.service = service;
	}

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


	/* public: SelfShuntPoint (access Context) interface */

	/**
	 * Returns Self-Shunt Context assigned
	 * to the invoking thread.
	 */
	public SelfShuntCtx context()
	{
		return context.get();
	}

	/**
	 * Assigns or removes (when undefined)
	 * Self-Shunt Context to the invoking thread.
	 */
	public void setContext(SelfShuntCtx ctx)
	{
		if(ctx == null)
			context.remove();
		else
			context.set(ctx);
	}


	/* public: support interface */

	public Set<String> collectShuntsGroups()
	{
		TreeSet<String> groups = new TreeSet<String>();

		//c: for all the shunts registered
		for(SelfShunt shunt : getShuntsSet().listShunts())
			groups.addAll(Arrays.asList(SU.a2a(shunt.getGroups())));

		return groups;
	}


	/* private: the set of shunts */

	private SelfShuntService service;


	/* private: the set of shunts */

	private SelfShuntsSet shuntsSet =
	  new SelfShuntsRefsSet();


	/* private: thread local context */

	private ThreadLocal<SelfShuntCtx> context =
	  new ThreadLocal<SelfShuntCtx>();
}
