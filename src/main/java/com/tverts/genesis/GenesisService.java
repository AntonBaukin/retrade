package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: system services */

import com.tverts.endure.core.Domain;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * When Genesis Service receives {@link GenesisEvent}
 * event, it collects all the {@link GenesisSphere}s
 * referenced, select those specified in the event,
 * and runs them.
 *
 * When all the Genesis Spheres are done, the service
 * notifies MainService with {@link GenesisDone} event.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisService extends ServiceBase
{
	/* Genesis Break Error */

	public static class BreakGenesis extends RuntimeException
	{
		public BreakGenesis(Throwable cause)
		{
			super(cause);
		}
	}


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
		if(event instanceof GenesisEvent)
			generate((GenesisEvent) event);
	}


	/* protected: service work steps */

	protected void generate(GenesisEvent event)
	{
		//~: select the spheres
		List<GenesisSphere> spheres =
		  new ArrayList<GenesisSphere>(4);
		selectSpheres(event, spheres);

		//?: {has no spheres selected}
		if(spheres.isEmpty())
			throw new IllegalStateException(
			  "Genesis Event has no Spheres named!");

		LU.I(getLog(), logsig(), " starting genesis for Spheres ",
		  SU.scat("; ", event.getSpheres()));

		//~: invoke the spheres
		Throwable error = null;
		GenCtx    ctx   = new GenCtxBase();

		for(GenesisSphere sphere : spheres) try
		{
			generate(event, sphere, ctx);
		}
		catch(BreakGenesis e)
		{
			logGenesisBreak();
			error = e.getCause();
			break;
		}

		//?: {has no error}
		if(error == null)
			success(ctx, event);
		else
			throw new RuntimeException(
			  "Genesis failed! Service aborts generation!", error);
	}

	protected void selectSpheres(GenesisEvent event, List<GenesisSphere> spheres)
	{
		GenesisSphereReference ref = getGenesisSpheres();
		List<GenesisSphere>    gss = (spheres == null)?(null):
		  ref.dereferObjects();

		//?: {no spheres configured}
		if((gss == null) || gss.isEmpty())
			throw new IllegalStateException(logsig() +
			  " has no Spheres configured!");

		//~: map the spheres by the names
		Map<String, GenesisSphere> smap =
		  new HashMap<String, GenesisSphere>(
		    event.getSpheres().size());

		for(GenesisSphere gs : gss)
		{
			//?: {has no name}
			if(SU.sXe(gs.getName()))
				throw new IllegalStateException(String.format(
				  "%s: Genesis Sphere of class '%s' has Name undefined!",
				  logsig(), gs.getName()
				));

			//?: {already has this sphere}
			if(smap.containsKey(SU.s2s(gs.getName())))
				throw new IllegalStateException(String.format(
				  "%s already has Genesis Sphere named [%s]!",
				  logsig(), SU.s2s(gs.getName())
				));

			//~: put
			smap.put(SU.s2s(gs.getName()), gs);
		}

		//~: select the spheres
		for(String name : event.getSpheres())
		{
			//?: {the name is empty}
			if(SU.sXe(name))
				throw new IllegalStateException();

			//?: {has no such name}
			if(!smap.containsKey(SU.s2s(name)))
				throw new IllegalStateException(String.format(
				  "%s has no Genesis Sphere named [%s]!",
				  logsig(), SU.s2s(name)
				));

			//!: add  to the result
			spheres.add(smap.get(SU.s2s(name)));
		}
	}

	protected void generate(GenesisEvent event, GenesisSphere sphere, GenCtx ctx)
	{
		//!: clone the prototype sphere
		GenesisSphere clone = sphere.clone();

		//~: assign the parameters
		assignParameters(event, clone);

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

	protected void assignParameters(GenesisEvent event, GenesisSphere sphere)
	{
		ArrayList<ObjectParam> params =
		  new ArrayList<ObjectParam>(16);

		//~: trim the event parameters
		{
			Map<String, String> ps = new HashMap<String, String>(1);
			for(String pn : event.getParams().keySet())
				if(!pn.equals(SU.s2s(pn)))
					ps.put(SU.s2s(pn), event.getParams().get(pn));
			event.getParams().putAll(ps);
		}

		//~: collect the parameters
		sphere.parameters(params);

		//~: assign & log them
		StringBuilder sb = new StringBuilder(64);
		boolean       rw;

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

			//?: {has this parameter in the event} assign it
			if(rw = event.getParams().containsKey(p.getName()))
				p.setString(event.getParams().get(p.getName()));

			//~: log the value existing | assigned
			LU.I(getLog(), sb, p.getName(),
			  (rw)?(" [SET]"):(" [DEF]"), " = [", p.getString(), "]");

			//?: {required parameter has no value}
			if(p.isRequired() && (p.getValue() == null))
				throw new IllegalStateException(String.format(
				  "Required parameter [%s] has no value!", p.getName()
				));
		}
	}

	protected void success(GenCtx ctx, GenesisEvent event)
	{
		LU.I(getLog(), logsig(), " genesis successfully completed!");

		//~: create & send the event
		GenesisDone done = new GenesisDone();
		done.setEvent(event);

		//~: domain
		if(ctx.get(Domain.class) != null)
			done.setDomain(ctx.get(Domain.class).getPrimaryKey());

		main(done);
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