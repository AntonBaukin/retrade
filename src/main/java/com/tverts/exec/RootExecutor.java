package com.tverts.exec;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Composite executor that invokes the executors
 * is being referenced.
 *
 * Also provides registration interface.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class RootExecutor implements Executor
{
	/* public: RootExecutor (registration) interface */

	public void     registerExecutors()
	{
		List<Executor> exs = (getReference() == null)
		  ?(Collections.<Executor> emptyList())
		  :(getReference().dereferObjects());

		synchronized(registry)
		{
			registry.clear();

			for(Executor ex : exs)
			{
				if(SU.sXe(ex.getName()))
					throw new IllegalStateException(String.format(
					  "Executor of class %s has empty name!",
					  ex.getClass().getSimpleName()
					));

				if(registry.containsKey(ex.getName()))
					throw new IllegalStateException(String.format(
					  "Executor with name '%s' is already registered " +
					  "in the Root Executor ('%s')!", ex.getName(), getName()
					));

				registry.put(ex.getName(), ex);
			}
		}

		executors = exs.toArray(new Executor[exs.size()]);
	}

	public Executor getExecutor(String name)
	{
		synchronized(registry)
		{
			return registry.get(name);
		}
	}


	/* public: RootExecutor (bean) interface */

	public void setName(String name)
	{
		this.name = name;
	}

	public ExecutorReference getReference()
	{
		return reference;
	}

	public void setReference(ExecutorReference reference)
	{
		this.reference = reference;
	}



	/* public: Executor interface */

	public String getName()
	{
		return name;
	}

	public Object execute(Object request)
	{
		Object result;

		for(Executor ex : executors)
			if((result = ex.execute(request)) != null)
				return result;

		return null;
	}


	/* private: executors reference  */

	private String name = getClass().getSimpleName();
	private ExecutorReference reference;


	/* private: registration state */

	private final Map<String, Executor> registry =
	  new HashMap<String, Executor>(17);

	private volatile Executor[]         executors =
	  new Executor[0];
}