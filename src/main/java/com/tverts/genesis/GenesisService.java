package com.tverts.genesis;

/* com.tverts: system services */

import com.tverts.support.SU;
import com.tverts.system.zservices.Event;
import com.tverts.system.zservices.ServiceBase;
import com.tverts.system.zservices.events.SystemReady;
import com.tverts.system.zservices.events.ServiceEventBase;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * When Genesis Service receives {@link SystemReady}
 * event it sends GenesisEvent to itself. Upon that
 * event it collects all the {@link GenesisSphere}s
 * referenced and sequentially runs them.
 *
 * When all the Genesis Spheres are done, the service
 * broadcasts {@link GenesisDone} event.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisService extends ServiceBase
{
	/* public: GenesisService (bean) interface */

	public GenesisSphereReference getGenesisSpheres()
	{
		return genesisSpheres;
	}

	public void    setGenesisSpheres(GenesisSphereReference spheres)
	{
		this.genesisSpheres = spheres;
	}


	/* public: Service interface */

	public void service(Event event)
	{
		if(event instanceof SystemReady)
			scheduler();

		if(event instanceof StartGenesis)
			generate();
	}


	/* protected: service work steps */

	public static class StartGenesis extends ServiceEventBase
	{
		public static final long serialVersionUID = 0L;
	}

	protected void scheduler()
	{
		self(new StartGenesis());

		LU.I(getLog(), uid(), " scheduled genesis...");
	}

	protected void generate()
	{
		GenesisSphereReference spheres = getGenesisSpheres();
		if(spheres == null) return;

		Throwable error = null;

		LU.I(getLog(), uid(), " starting genesis...");
		for(GenesisSphere sphere : spheres.dereferObjects()) try
		{
			generate(sphere);
		}
		catch(BreakGenesis e)
		{
			logGenesisBreak();
			error = e.getCause();
			break;
		}

		if(error == null)
			success();
		else
			throw new RuntimeException(
			  "Genesis failed! Service aborts generation!", error);
	}

	protected void generate(GenesisSphere sphere)
	{
		try
		{
			sphere.run();
		}
		catch(Throwable e)
		{
			handleGenesisError(sphere, e);
		}
	}

	protected void success()
	{
		LU.I(getLog(), uid(), " genesis successfully completed!");
		broadcast(new GenesisDone());
	}

	public static class BreakGenesis extends RuntimeException
	{
		public BreakGenesis(Throwable cause)
		{
			super(cause);
		}
	}

	protected void handleGenesisError(GenesisSphere sphere, Throwable e)
	{
		logSphereError(sphere, EX.xrt(e));
		throw new BreakGenesis(e);
	}


	/* protected: logging */

	protected String getLog()
	{
		return GenesisPoint.LOG_SERVICE;
	}

	protected void   logSphereError(GenesisSphere sphere, Throwable e)
	{
		LU.E(getLog(), e, uid(), ": error ocurred in Genesis Sphere Unit [",
		  SU.sXe(sphere.getName())?("not named"):(sphere.getName()), "]!");
	}

	protected void   logGenesisBreak()
	{
		LU.E(getLog(), "genesis spheres execution ",
		 "was breaked due to the previous errors!");
	}


	/* private: default genesis references */

	private GenesisSphereReference genesisSpheres;
}