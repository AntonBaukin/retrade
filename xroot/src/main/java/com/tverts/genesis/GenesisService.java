package com.tverts.genesis;

/* Java */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: secure */

import static com.tverts.secure.SecPoint.loadSystemDomain;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.ObjectParamView;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;

/* com.tverts: support (logging) */

import com.tverts.support.logs.InfoLogBuffer;
import com.tverts.support.logs.LogPoint;
import com.tverts.support.logs.LogStrategy;


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


	/* public: GenesisService (main) interface */

	/**
	 * Collects parameters views on the spheres named.
	 */
	public ObjectParamView[] parameters(Collection<String> spheres)
	{
		//~: select the spheres
		List<GenesisSphere> sps = new ArrayList<>(4);
		selectSpheres(spheres, sps);

		//~: collect the parameters of spheres selected
		List<ObjectParam> ps = new ArrayList<>(16);
		for(GenesisSphere sp : sps)
			sp.parameters(ps);

		//~: translate into views
		ObjectParamView[] res = new ObjectParamView[ps.size()];
		for(int i = 0;(i < res.length);i++)
			res[i] = new ObjectParamView().init(ps.get(i));

		return res;
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
		if(!(event instanceof GenesisEvent))
			return;

		//~: create the context
		GenCtx ctx = createContext((GenesisEvent) event);

		//~: open genesis
		openGenesis((GenesisEvent) event, ctx);

		//!: generate
		try
		{
			generate((GenesisEvent) event, ctx);
		}
		finally
		{
			//~: close the genesis
			closeGenesis((GenesisEvent) event, ctx);
		}
	}


	/* protected: service work steps */

	protected GenCtx createContext(GenesisEvent event)
	{
		return new GenCtxBase();
	}

	protected void   openGenesis(GenesisEvent event, GenCtx ctx)
	{
		//?: {has log parameter} tee the log
		if(event.getLogParam() != null)
		{
			LogPoint.getInstance().getLogStrategy().
			  tee(new InfoLogBuffer());
		}

		//~: open log
		LU.I(getLog(), logsig(), " starting genesis for Spheres [",
		  SU.scats("; ", event.getSpheres()), "]");
	}

	protected void   closeGenesis(GenesisEvent event, GenCtx ctx)
	{
		//?: {has no log parameter} do nothing
		if(event.getLogParam() == null)
			return;

		LogStrategy   ls = LogPoint.getInstance().getLogStrategy().tee();
		StringBuilder bf;

		//!: clear local log tee
		LogPoint.getInstance().getLogStrategy().tee(null);

		//~: get the log buffer
		bf = ((InfoLogBuffer)ls).getBuffer();

		//~: get domain from context
		Domain domain = ctx.get(Domain.class);

		//?: {has no domain} take the system
		if(domain == null)
			domain = loadSystemDomain();

		//~: get log property
		GetProps g = bean(GetProps.class);
		Property p = g.goc(domain, "Genesis", event.getLogParam());

		//~: set the log text
		g.set(p, bf.toString());
	}

	protected void   generate(GenesisEvent event, GenCtx ctx)
	{
		//~: select the spheres
		List<GenesisSphere> spheres = new ArrayList<>(4);
		selectSpheres(event, spheres);

		//?: {has no spheres selected}
		EX.assertx(!spheres.isEmpty(),
		  "Genesis Event has no Spheres named!");

		Throwable er = null;
		long      ts = System.currentTimeMillis();

		//~: invoke the spheres
		for(GenesisSphere sphere : spheres) try
		{
			generate(event, sphere, ctx);
		}
		catch(BreakGenesis e)
		{
			logGenesisBreak();
			er = e.getCause();
			break;
		}

		//?: {has no error}
		if(er == null)
			success(ctx, event, ts);
		else
			error(ctx, event, er);
	}

	protected void   selectSpheres(GenesisEvent event, List<GenesisSphere> spheres)
	{
		selectSpheres(event.getSpheres(), spheres);
	}

	protected void   selectSpheres(Collection<String> names, List<GenesisSphere> spheres)
	{
		GenesisSphereReference ref = getGenesisSpheres();
		List<GenesisSphere>    gss = (spheres == null)?(null):
		  ref.dereferObjects();

		//?: {no spheres configured}
		EX.assertx(gss != null && !gss.isEmpty(),
		  logsig(), " has no Spheres configured!");

		//~: map the spheres by the names
		Map<String, GenesisSphere> smap =
		  new HashMap<>(names.size());

		for(GenesisSphere gs : gss)
		{
			//?: {has no name}
			EX.asserts(gs.getName(),
			  logsig(), " Genesis Sphere of class ",
			  gs.getName(), " has Name undefined!"
			);

			//?: {already has this sphere}
			EX.assertx(!smap.containsKey(gs.getName()), logsig(),
			  " already has Genesis Sphere named [", gs.getName(), "]!");

			//~: put
			smap.put(SU.s2s(gs.getName()), gs);
		}

		//~: select the spheres
		for(String name : names)
		{
			//?: {the name is empty}
			EX.asserts(name);

			//?: {has no such name}
			EX.assertx(smap.containsKey(SU.s2s(name)), logsig(),
			  " has no Genesis Sphere named [", name, "]!");

			//!: add  to the result
			spheres.add(smap.get(SU.s2s(name)));
		}
	}

	protected void   generate(GenesisEvent event, GenesisSphere sphere, GenCtx ctx)
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

	protected void   assignParameters(GenesisEvent event, GenesisSphere sphere)
	{
		ArrayList<ObjectParam> params =
		  new ArrayList<>(16);

		//~: trim the event parameters
		{
			Map<String, String> ps = new HashMap<>(1);
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
			sb.append("[");

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
			LU.I(getLog(), sb, p.getName(), (rw)?(" [SET]"):(" [DEF]"),
			  " = [", p.getString().replaceAll("\\s+", " "), "]");

			//?: {required parameter has no value}
			EX.assertx(!p.isRequired() || p.getValue() != null,
			  "Required parameter [", p.getName(), "] has no value!"
			);
		}
	}

	@SuppressWarnings("unchecked")
	protected void   success(GenCtx ctx, GenesisEvent event, long ts)
	{
		//~: log the success with the time taken
		LU.I(getLog(), logsig(), " genesis completed in ", LU.td(ts));

		//~: create & send done event
		GenesisDone done = new GenesisDone();

		//~: source generation event
		done.setEvent(event);

		//~: domain
		if(ctx.get(Domain.class) != null)
			done.setDomain(ctx.get(Domain.class).getPrimaryKey());

		//~: context state (as the event parameters)
		Map params = new HashMap(11);
		collectGenExportedState(ctx, params);
		done.setGenCtx(params);

		main(done);
	}

	protected void   error(GenCtx ctx, GenesisEvent event, Throwable error)
	{
		//~: save error in the context
		ctx.set(Throwable.class, error);

		//~: create & send failure event
		GenesisFailed failed = new GenesisFailed();

		//~: source generation event
		failed.setEvent(event);

		//~: the error
		failed.setError(error);

		main(failed);
	}

	protected void   handleGenesisError(GenesisSphere sphere, Throwable e)
	{
		logSphereError(sphere, EX.xrt(e));
		throw new BreakGenesis(e);
	}

	@SuppressWarnings("unchecked")
	protected void   collectGenExportedState(GenCtx ctx, Map map)
	{
		Set keys = ctx.exported();
		Set all  = ctx.params();

		for(Object key : keys) if(key instanceof Serializable)
		{
			Object val = ctx.get(key);

			//?: {the key does not exist now}
			if((val == null) && !all.contains(key))
				continue;

			//~: {value must be also serializable}
			if((val != null) && !(val instanceof Serializable))
				continue;

			map.put(key, val);
		}
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