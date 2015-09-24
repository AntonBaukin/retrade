package com.tverts.jsx;

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

	public JsGlobal(JsEngine engine, JsCtx context)
	{
		this.engine  = engine;
		this.context = context;
	}

	protected final JsEngine engine;

	protected final JsCtx    context;


	/* Scripting Interface */

	/**
	 * The meaning of this method is the same
	 * as of Node.js require. Here the path uses
	 * URI's '/' separators. If the file name
	 * doesn't contain '.', '.js' suffix is added.
	 */
	public Object include(String path)
	{
		return null;
	}
}