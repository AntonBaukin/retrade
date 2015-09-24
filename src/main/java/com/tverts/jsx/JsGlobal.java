package com.tverts.jsx;

/* Java */

import java.util.Map;


/**
 * This Java object is available for all
 * JavaScripts by the name 'JsX'.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsGlobal
{
	/**
	 * By this name the global objects are mapped.
	 */
	public static final String NAME = "JsX";

	public JsGlobal(JsEngine engine)
	{
		this.engine  = engine;
	}

	protected final JsEngine engine;


	/* Scripting Interface */

	/**
	 * The meaning of this method is the same
	 * as of Node.js require. Here the path uses
	 * URI's '/' separators. If the file name
	 * doesn't contain '.', '.js' suffix is added.
	 */
	public Object include(String script)
	{
		return engine.nest(script, null);
	}

	/**
	 * The same as {@link #include(String)}, but allows
	 * to temporary set the variables while executing.
	 */
	public Object invoke(String script, Map<String, Object> vars)
	{
		return engine.nest(script, vars);
	}
}