package com.tverts.jsx;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* Java Scripting */

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Wrapper of JavaScript Engine bound with
 * the script previously compiled by it.
 *
 * Warning: engine is not thread-safe!
 *
 * @author anton.baukin@gmail.com.
 */
public class JsEngine
{
	public JsEngine(ScriptEngineManager em, JsFiles files, JsFile file)
	{
		//~: create Nashorn engine
		try
		{
			this.engine = em.getEngineByName("Nashorn");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error creating Nashorn engine instance!");
		}

		//~: compile the initial script
		this.files = files;
		this.file = file;
		this.compile(file);
	}


	/* Engine */

	public ScriptContext createContext()
	{
		Bindings b = engine.createBindings();

		//=: JsX
		b.put(JsGlobal.NAME, new JsGlobal(this));

		ScriptContext ctx = new SimpleScriptContext();

		//~: global bindings
		ctx.setBindings(engine.getBindings(ScriptContext.GLOBAL_SCOPE),
		  ScriptContext.GLOBAL_SCOPE);

		//~: temporary bindings for the engine
		ctx.setBindings(b, ScriptContext.ENGINE_SCOPE);

		return ctx;
	}

	/**
	 * Executes the compiled script by the context given.
	 */
	public void          execute(JsCtx ctx)
	{
		//~: create the context
		ScriptContext sc = ctx.create(this);

		//~: evaluate the script
		try
		{
			EX.assertn(this.scripts.get(this.file)).eval(sc);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error during execution of script [", file.uri(), "]!");
		}
	}

	/* protected: scripts execution */

	protected void       compile(JsFile file)
	{
		//~: access the file content
		String code = file.content();
		EX.assertn(code, "File [", file.uri(), "] has no content!");

		CompiledScript script; try
		{
			script = ((Compilable) engine).compile(code);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error during compilation of script [", file.uri(), "]!");
		}

		//~: remember the script
		scripts.put(file, script);
	}

	/**
	 * Scripting Engine private for this instance.
	 */
	protected final ScriptEngine engine;

	/**
	 * The collection of scripts.
	 */
	protected final JsFiles files;

	protected final JsFile file;

	protected final Map<JsFile, CompiledScript> scripts =
	  new HashMap<>();
}