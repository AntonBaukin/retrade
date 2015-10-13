package com.tverts.jsx;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* Java Scripting */

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import com.tverts.support.misc.Hash;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Wrapper of JavaScript Engine bound with
 * the script previously compiled by it.
 * Engine works in the scripting mode!
 *
 * Warning: engine is not thread-safe!
 *
 * @author anton.baukin@gmail.com.
 */
public class JsEngine
{
	public JsEngine(NashornScriptEngineFactory f, JsFiles files, JsFile file)
	{
		//~: create Nashorn engine
		try
		{
			this.engine = f.getScriptEngine("-scripting");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error creating Nashorn engine instance!");
		}

		this.file    = EX.assertn(file);
		this.files   = EX.assertn(files);
		this.streams = new JsStreams().wrappers();

		//~: execute the root script
		this.prepare();
	}

	/**
	 * The root script file to execute.
	 */
	public final JsFile file;


	/* Engine Interface */

	/**
	 * The root script compiled and executed in the
	 * constructor leaves the engine in it's final
	 * state. This method allows to execute a global
	 * function of the root script.
	 *
	 * Note that the context is not closed here!
	 */
	public Object invoke(String function, JsCtx ctx, Object... args)
	{
		EX.assertn(ctx);
		EX.asserts(function);
		EX.assertx(this.stack == null);

		//~: scope for the root script
		Bindings scope = this.engine.getContext().
		  getBindings(ScriptContext.ENGINE_SCOPE);

		//~: wrap the context streams
		this.streams.wrap(ctx.getStreams());

		//~: assign the parameters
		Map<String, Object> prev = new HashMap<>();
		ctx.assign(scope, prev);

		//~: invoke the script's function
		try
		{
			this.stack = new Nested(this.file, ctx);
			return ((Invocable) this.engine).invokeFunction(function, args);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error during execution of script [", file.uri(),
			  "] function ", function, "() with arguments: ", args);
		}
		finally
		{
			//~: return back original scope
			scope.putAll(prev);

			//~: flush streams & clean
			try
			{
				this.streams.flush();
			}
			finally
			{
				this.cleanup(false);
			}
		}
	}

	public void   invalidate()
	{
		//~: create new empty scope
		Bindings scope = this.engine.createBindings();
		this.engine.setBindings(scope, ScriptContext.ENGINE_SCOPE);

		//~: remove all compiled scripts
		this.cleanup(true);

		//!: prepare again
		this.prepare();
	}

	/**
	 * Invalidates the engine if source
	 * file content was changed.
	 */
	public void   check()
	{
		this.checkTime = System.currentTimeMillis();

		//?: {main file differs}
		EX.assertn(fileHash);
		if(!fileHash.equals(file.hash()))
		{
			this.invalidate();
			return;
		}

		//c: search for changed script file
		for(Script s : this.scripts.values())
			if(!s.file.hash().equals(s.hash))
			{
				this.invalidate();
				return;
			}
	}

	protected long checkTime;

	public long   getCheckTime()
	{
		return checkTime;
	}


	/* protected: scripts execution */

	/**
	 * Item (and stack) of scripts invocation.
	 */
	protected static class Nested
	{
		/**
		 * The script file.
		 */
		public final JsFile file;

		public final JsCtx  ctx;

		public Nested(JsFile file, JsCtx ctx)
		{
			this.file = file;
			this.ctx  = ctx;
		}
	}

	/**
	 * Evaluates the root script. After this call
	 * all the compiled scripts would be cleared,
	 * but client code may require them again,
	 * what is not forbidden.
	 *
	 * Hint: the initial script is executed in
	 * VOID context, {@link JsCtx#VOID}.
	 */
	protected void   prepare()
	{
		//~: compile the root
		compile(this.file);

		//~: execute it in void context
		try(JsCtx ctx = new JsCtx().init(this.streams))
		{
			//~: create global variables
			prepareGlobalScope(engine.getContext().
				 getBindings(ScriptContext.ENGINE_SCOPE));

			//~: assign the wrapping streams
			ctx.assign(engine.getContext());

			//~: stack entry
			this.stack = new Nested(this.file, ctx);

			//~: evaluate the script
			EX.assertn(this.scripts.get(this.file)).script.eval();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error during the initial execution of script [",
			  file.uri(), "]!");
		}
		finally
		{
			this.cleanup(true);
		}
	}

	protected void   prepareGlobalScope(Bindings scope)
	{
		//=: JsX global variable
		scope.put(JsGlobal.NAME, new JsGlobal(new JsGlobal.Nesting()
		{
			public JsFile resolve(String path)
			{
				return JsEngine.this.resolve(path);
			}

			public Object nest(String script, Map<String, Object> vars)
			{
				return JsEngine.this.nest(script, vars);
			}

			public JsCtx ctx()
			{
				return EX.assertn(stack).ctx;
			}
		}));
	}

	protected JsFile resolve(String script)
	{
		EX.asserts(script);
		EX.assertn(this.stack);

		//?: {script is relative}
		if(script.startsWith("./"))
			return EX.assertn(this.files.relate(this.stack.file, script),
			  "Script [", script, "] relative to script [",
			  this.stack.file.uri(), "] is not found!");
		//~: script is global
		else
			return EX.assertn(this.files.cached(script),
			  "Script file [", script, "] is not found!");
	}

	/**
	 * On the first demand, loads and compiles the script given.
	 * Executes it in the context of existing engine thus affecting
	 * it's present state. Optional variables may be given to
	 * provide them to the script.
	 *
	 * If the path starts with './' it is assumed to be local and
	 * related to the script is being currently executed.
	 */
	protected Object nest(String script, Map<String, Object> vars)
	{
		JsFile file = this.resolve(script);

		//?: {script instance is not cached yet}
		Script cs = this.scripts.get(file);
		if((cs == null) || (cs.script == null))
		{
			compile(file);
			cs = EX.assertn(this.scripts.get(file));
		}

		//~: scope for the current script
		Bindings scope = this.engine.getContext().
		  getBindings(ScriptContext.ENGINE_SCOPE);

		//~: previous scope state
		Map<String, Object> prev = null;

		//~: create stack entry
		Nested current = this.stack;
		Nested nested  = new Nested(file, current.ctx);

		//?: {has variables} set them to scope
		if((vars != null) && !vars.isEmpty())
		{
			prev = new HashMap<>();
			for(Map.Entry<String, Object> e : vars.entrySet())
			{
				prev.put(e.getKey(), scope.get(e.getKey()));
				scope.put(e.getKey(), e.getValue());
			}
		}

		//~: execute the nested scope
		try
		{
			this.stack = nested;

			//~: invoke the compiled script
			return cs.script.eval();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error while executing script [", file.uri() ,"]!");
		}
		finally
		{
			//~: pop nested scope
			if(prev != null)
				scope.putAll(prev);
			this.stack = current;
		}
	}

	/**
	 * Pointer to the current execution context.
	 */
	protected Nested stack;

	protected void compile(JsFile file)
	{
		//~: access the file content
		Hash   hash = new Hash();
		String code = file.content(hash);
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
		Script s = new Script();
		scripts.put(file, s);

		s.file   = file;
		s.hash   = hash;
		s.script = script;

		if(this.file.equals(file))
			this.fileHash = new Hash(hash);
	}

	protected void cleanup(boolean initial)
	{
		//HINT: we clear the script compiled during
		// the initial execution as they will likely
		// will not be needed more
		if(initial)
			for(Script s : scripts.values())
				s.script = null;

		//~: invocation stack
		this.stack = null;

		//~: close the wrapped streams
		this.streams.close();
	}

	/**
	 * Scripting Engine private for this instance.
	 */
	protected final ScriptEngine engine;

	/**
	 * The collection of scripts.
	 */
	protected final JsFiles files;

	protected Hash fileHash;

	/**
	 * Collection of wrapping streams.
	 */
	protected final JsStreams streams;

	/**
	 * The root script file with all the included ones.
	 *
	 * TODO eliminate Script and safe hashes in else map!
	 */
	protected final Map<JsFile, Script> scripts =
	  new HashMap<>();

	protected static class Script
	{
		public JsFile         file;
		public Hash           hash;
		public CompiledScript script;
	}
}