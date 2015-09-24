package com.tverts.jsx;

/* Java */

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* Java Scripting */

import javax.script.ScriptEngineManager;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Collections of Engines related to the
 * scripts defined by their files.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsEngines
{
	public JsEngines(JsFiles files)
	{
		this.files   = EX.assertn(files);
		this.manager = new ScriptEngineManager();
	}


	/* Engines Collection */

	public JsEngine take(JsFile file)
	{
		Engines es = this.engines.computeIfAbsent(
		  EX.assertn(file), Engines::new);

		return es.take();
	}

	public void     free(JsEngine engine)
	{
		JsFile  file = EX.assertn(engine).file;
		Engines es   = this.engines.computeIfAbsent(
		  file, Engines::new);

		es.free(engine);
	}

	protected final Map<JsFile, Engines> engines =
	  new ConcurrentHashMap<>();


	/* Engines */

	protected class Engines
	{
		public Engines(JsFile file)
		{
			this.file = file;
		}

		public final JsFile file;


		/* Engines Access */

		public JsEngine take()
		{
			//~: search for existing engine
			synchronized(this)
			{
				JsEngine             e  = null;
				LinkedList<JsEngine> es = (engines == null)?(null):(engines.get());

				if((es != null) && !es.isEmpty())
					e = es.removeFirst();

				if(e != null)
					return e;
			}

			return createEngine(this.file);
		}

		public void     free(JsEngine engine)
		{
			EX.assertn(engine);
			EX.assertx(engine.file.equals(this.file));

			synchronized(this)
			{
				LinkedList<JsEngine> es = (engines == null)?(null):(engines.get());
				if(es == null)
					engines = new WeakReference<LinkedList<JsEngine>>(
					  es = new LinkedList<>());

				es.addLast(engine);
			}
		}

		protected Reference<LinkedList<JsEngine>> engines;
	}

	protected JsEngine createEngine(JsFile file)
	{
		return new JsEngine(manager, this.files, file);
	}

	protected final ScriptEngineManager manager;

	protected final JsFiles files;
}