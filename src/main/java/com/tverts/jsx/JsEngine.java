package com.tverts.jsx;

/* Java Scripting */

import javax.script.ScriptEngine;


/**
 * Wrapper of JavaScript Engine.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsEngine
{
	public JsEngine(ScriptEngine engine)
	{
		this.engine = engine;
		this.global = new JsGlobal(this);
	}

	protected final ScriptEngine engine;
	protected final JsGlobal     global;

}