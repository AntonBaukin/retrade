package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.events.SystemReady;
import com.tverts.system.services.events.ServiceEventBase;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


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

		LU.I(getLog(), logsig(), " scheduled genesis...");
	}

	protected void generate()
	{
		GenesisSphereReference spheres = getGenesisSpheres();
		if(spheres == null) return;

		Throwable error = null;
		GenCtx    ctx   = new GenCtxBase();

		LU.I(getLog(), logsig(), " starting genesis...");
		for(GenesisSphere sphere : spheres.dereferObjects()) try
		{
			generate(sphere, ctx);
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

	protected void generate(GenesisSphere sphere, GenCtx ctx)
	{
		//!: clone the prototype sphere
		GenesisSphere clone = sphere.clone();

		//~: assign the parameters
		assignParameters(clone);

		//!: do generate
		try
		{
			clone.generate(ctx);
		}
		catch(Throwable e)
		{
			handleGenesisError(clone, e);
		}
	}

	protected void assignParameters(GenesisSphere sphere)
	{
		ArrayList<ObjectParam> params =
		  new ArrayList<ObjectParam>(16);

		//~: collect the parameters
		sphere.parameters(params);

		//~: assign & log them
		StringBuilder sb = new StringBuilder(64);
		for(ObjectParam p : params)
		{
			//~: describe the parameter
			sb.delete(0, sb.length());
			sb.append(" [");

			if(!p.isWrite())
				sb.append("Read-Only ");
			if(!p.isRead())
				sb.append("Write-Only ");
			if(p.isRequired())
				sb.append("Required ");
			sb.append("Parameter] ");

			//~: log the value existing | assigned
			LU.I(getLog(), logsig(), sb,
			  p.getName(), " = [", p.getString(), "]");
		}
	}

	protected void success()
	{
		LU.I(getLog(), logsig(), " genesis successfully completed!");
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
		LU.E(getLog(), e, logsig(), ": error ocurred in Genesis Sphere Unit [",
		  SU.sXe(sphere.getName())?("not named"):(sphere.getName()), "]!");
	}

	protected void   logGenesisBreak()
	{
		LU.E(getLog(), logsig(), ": genesis spheres execution ",
		 "was breaked due to the previous errors!");
	}


	/* private: default genesis references */

	private GenesisSphereReference genesisSpheres;
}