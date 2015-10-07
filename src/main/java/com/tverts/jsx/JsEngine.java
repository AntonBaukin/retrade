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
import javax.script.ScriptEngineManager;

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
		this.stack = new Nested(this.file, ctx);
		this.stack.current = this.engine.getContext().
		  getBindings(ScriptContext.ENGINE_SCOPE);

		//~: wrap the context streams
		this.streams.wrap(ctx.getStreams());

		//~: invoke the script's function
		try
		{
			return ((Invocable) this.engine).invokeFunction(function, args);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error during execution of script [", file.uri(),
			  "] function ", function, "() with arguments: ", args);
		}
		finally
		{
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

		/**
		 * Previous scope variables.
		 */
		public Bindings outer;

		/**
		 * Current scope variables.
		 */
		public Bindings current;
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

			//~: evaluate the script
			EX.assertn(this.scripts.get(this.file)).eval();
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
		CompiledScript cs = this.scripts.get(file);
		if(cs == null)
		{
			compile(file);
			cs = EX.assertn(this.scripts.get(file));
		}

		//~: create stack entry
		Nested current = this.stack;
		Nested nested  = new Nested(file, current.ctx);
		nested.outer = current.current;

		//?: {has no variables} no scope
		if(vars == null)
			nested.current = nested.outer;
		//~: copy nested scope
		else
		{
			nested.current = this.engine.createBindings();
			nested.current.putAll(nested.outer);
			nested.current.putAll(vars);
		}

		//~: execute the nested scope
		this.stack = nested;
		try
		{
			//~: assign nested scope
			if(nested.current != nested.outer)
				this.engine.setBindings(nested.current, ScriptContext.ENGINE_SCOPE);

			//~: invoke the compiled script
			return cs.eval();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error while executing script [", file.uri() ,"]!");
		}
		finally
		{
			//~: pop nested scope
			if(nested.current != nested.outer)
				this.engine.setBindings(nested.outer, ScriptContext.ENGINE_SCOPE);

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

	protected void cleanup(boolean initial)
	{
		//HINT: we clear the script compiled during
		// the initial execution as they will likely
		// will not be needed more
		if(initial)
			this.scripts.clear();

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

	/**
	 * Collection of wrapping streams.
	 */
	protected final JsStreams streams;

	/**
	 * The root script file with all the included ones.
	 */
	protected final Map<JsFile, CompiledScript> scripts =
	  new HashMap<>();
}