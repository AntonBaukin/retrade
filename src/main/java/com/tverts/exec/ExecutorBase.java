package com.tverts.exec;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * A basic implementation of {@link Executor}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ExecutorBase
       implements     Executor, ExecutorReference
{
	/* public: ObjectsReference interface */

	public List<Executor> dereferObjects()
	{
		return Collections.<Executor> singletonList(this);
	}


	/* public: ExecutorBase interface */

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		this.name = name;
	}


	/* private: executor configuration */

	private String name;
}