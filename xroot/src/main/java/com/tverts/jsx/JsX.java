package com.tverts.jsx;

/* com.tverts: system (services) */

import com.tverts.system.SystemConfig;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.events.SystemReady;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Entry point to JavaScript execution layer
 * based on Oracle Nashorn implementation.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsX extends ServiceBase
{
	/* Singleton  */

	public static JsX getInstance()
	{
		return INSTANCE;
	}

	public static final JsX INSTANCE =
	  new JsX();

	protected JsX()
	{
		this.files   = new JsFiles(new String[]{
		  this.getClass().getPackage().getName()
		});

		this.engines = new JsEngines(this.files);
	}


	/* Static Interface */

	public static Object invoke(String script, String function, Object... perks)
	{
		EX.asserts(script);
		EX.asserts(function);

		try(JsCtx ctx = new JsCtx().init(perks))
		{
			return JsX.INSTANCE.execute(script, function, ctx);
		}
	}

	public static Object apply(String script, String function, Object... args)
	{
		return JsX.apply(script, function, null, args);
	}

	public static Object apply(String script, String function, JsCtx ctx, Object... args)
	{
		//~: create local context on demand
		JsCtx local = (ctx != null)?(null):(ctx = new JsCtx().init());

		try //!: run the script
		{
			return JsX.INSTANCE.execute(script, function, ctx, args);
		}
		finally
		{
			if(local != null)
				local.close();
		}
	}

	public static Object debug(Object... args)
	{
		return null;
	}


	/* Java Bean (configuration) */

	protected JsFiles files;

	/**
	 * Configures the root packages (directories)
	 * to search for JavaScript files from using
	 * the application Class Loader.
	 *
	 * The names may be presented in Java package
	 * dot-separator notation, or with URI's '/'.
	 */
	public void setRoots(String roots)
	{
		String[] rs = SU.s2a(roots);
		EX.asserte(rs, "No JavaScript roots are given!");

		for(int i = 0;(i < rs.length);i++)
		{
			String r = rs[i];

			if(r.indexOf('/') == -1)
				r = r.replace('.', '/');
			if(r.startsWith("/"))
				r = r.substring(1);
			while(r.endsWith("/"))
				r = r.substring(0, r.length() - 1);

			rs[i] = EX.asserts(r);
		}

		LU.I(LU.cls(this), "using the following roots: ",
		  SU.scats(", ", (Object)rs));

		this.files   = new JsFiles(rs);
		this.engines = new JsEngines(this.files);
	}


	/* Scripts Execution */

	public boolean exists(String script)
	{
		EX.asserts(script);
		return (files.cached(script) != null);
	}

	/**
	 * Executes script by it's path related to
	 * one of the roots configured.
	 */
	public Object  execute(String script, String function, JsCtx ctx, Object... args)
	{
		EX.assertn(ctx);
		EX.asserts(function);

		//~: search for the scripting file
		JsFile file = files.cached(script);

		//?: {found it not}
		EX.assertn(file, "No script script file is found by the path [", script, "]!");

		//~: allocate the engine
		JsEngine engine = this.engines.take(file);

		//~: execute the script
		try
		{
			return engine.invoke(function, ctx, args);
		}
		finally
		{
			//!: free the engine
			this.engines.free(engine);
		}
	}

	protected JsEngines engines;


	/* Service */

	public void    service(Event event)
	{
		//?: {startup}
		if(event instanceof SystemReady)
			startupPlan();
		//?: {own event}
		else if((event instanceof JsCheckEvent) && mine(event))
			doCheck();
	}


	/* protected: events processing */

	protected void startupPlan()
	{
		JsCheckEvent e = new JsCheckEvent();

		//~: delay [1; 11) seconds
		delay(e, 1000L + System.currentTimeMillis() % 10000L);

		//~: sent to self
		self(e);

		LU.I(getLog(), logsig(), ": initiated JsX checks");
	}

	protected void doCheck()
	{
		try
		{
			engines.check();
		}
		finally
		{
			planNextCheck();
		}
	}

	protected void planNextCheck()
	{
		JsCheckEvent e = new JsCheckEvent();

		//~: delay to system check timeout
		delay(e, SystemConfig.INSTANCE.getCheckInterval());

		self(e);
	}

}