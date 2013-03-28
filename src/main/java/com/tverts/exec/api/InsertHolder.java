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


	/* constructors */

	public InsertHolder()
	{}

	public InsertHolder(Holder holder)
	{
		this.holder = holder;
	}


	/* public: UpdateHolder interface */

	public Holder getHolder()
	{
		return holder;
	}

	public void   setHolder(Holder holder)
	{
		this.holder = holder;
	}

	/* the holder */

	private Holder holder;
}