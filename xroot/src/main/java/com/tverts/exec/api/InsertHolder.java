package com.tverts.exec.api;

/* standard Java classes */

import java.io.Serializable;

/* com.tverts: api */

import com.tverts.api.core.Holder;


/**
 * Wraps {@link Holder} for insert operation.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertHolder implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: UpdateHolder interface */

	public Holder getHolder()
	{
		return holder;
	}

	public InsertHolder setHolder(Holder holder)
	{
		this.holder = holder;
		return this;
	}

	public Object getContext()
	{
		return context;
	}

	public InsertHolder setContext(Object context)
	{
		this.context = context;
		return this;
	}


	/* the holder & and the context */

	private Holder holder;
	private Object context;
}