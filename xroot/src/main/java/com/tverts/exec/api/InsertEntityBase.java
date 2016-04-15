package com.tverts.exec.api;

/* com.tverts: execution */

import com.tverts.exec.ExecError;
import com.tverts.exec.ExecutorBase;

/* com.tverts: api */

import com.tverts.api.core.Holder;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Helper superclass for {@link InsertEntityBase} executors.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class InsertEntityBase extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object request)
	  throws ExecError
	{
		//?: {is not an insert holder operation}
		if(!(request instanceof InsertHolder))
			return null;

		Holder h = ((InsertHolder)request).getHolder();

		//?: {is a not known entity}
		if((h.getEntity() == null) || !isKnown(h))
			return null;

		//~: insert the entity
		return insert((InsertHolder)request);
	}


	/* protected: update execution */

	/**
	 * Tells whether this update executor knows
	 * how to deal with the holder given.
	 */
	protected abstract boolean isKnown(Holder holder);

	protected Long   insert(Object source)
	{
		throw EX.unop();
	}

	protected Long   insert(InsertHolder h)
	{
		return insert(h.getHolder().getEntity());
	}

	protected String getLog()
	{
		return getClass().getName();
	}
}