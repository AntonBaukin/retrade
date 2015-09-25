package com.tverts.jsx;

/* Java Scripting */

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
public class JsX
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

	public static Object invoke(String script, String function, JsCtx ctx, Object... args)
	{
		return JsX.INSTANCE.execute(script, function, ctx, args);
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
		String[] rs = SU.s2aws(roots);
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

		LU.I(LU.cls(this), "Using the wollowing roots: ", rs);

		this.files   = new JsFiles(rs);
		this.engines = new JsEngines(this.files);
	}


	/* Scripts Execution */

	/**
	 * Executes script by it's path related to
	 * one of the roots configured.
	 */
	public Object execute(String script, String function, JsCtx ctx, Object... args)
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
}