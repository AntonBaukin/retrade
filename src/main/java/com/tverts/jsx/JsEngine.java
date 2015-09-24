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
		this.file = file;
		this.files = files;
		this.compile(file);
	}

	/**
	 * The root script file to execute.
	 */
	public final JsFile file;


	/* Engine */

	public ScriptContext createContext()
	{
		Bindings      b = engine.createBindings();
		ScriptContext c = new SimpleScriptContext();

		//~: temporary bindings for the engine
		c.setBindings(b, ScriptContext.ENGINE_SCOPE);

		return c;
	}

	/**
	 * Executes the compiled root script by the context given.
	 * Note that the context is not closed within this operation!
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

	/**
	 * The root script file with all the included ones.
	 */
	protected final Map<JsFile, CompiledScript> scripts =
	  new HashMap<>();
}