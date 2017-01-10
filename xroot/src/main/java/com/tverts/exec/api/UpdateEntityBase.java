package com.tverts.exec.api;

/* com.tverts: execution */

import com.tverts.exec.ExecError;
import com.tverts.exec.ExecutorBase;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.core.TwoKeysObject;
import com.tverts.api.core.UpdateEntities;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;


/**
 * Helper superclass for {@link UpdateEntities} executors.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UpdateEntityBase extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object request)
	  throws ExecError
	{
		//?: {is not an update holder operation}
		if(!(request instanceof UpdateHolder))
			return null;

		Holder h = ((UpdateHolder)request).getHolder();

		//?: {is a not known entity}
		if((h.getEntity() == null) || !isKnown(h))
			return null;

		//~: load the entity
		Object obj = loadEntity(h);

		//~: check the entity
		checkEntity(obj);

		//!: update it
		update(obj, h.getEntity());

		return Boolean.TRUE;
	}


	/* protected: update execution */

	/**
	 * Tells whether this update executor knows
	 * how to deal with the holder given.
	 */
	protected abstract boolean isKnown(Holder holder);

	/**
	 * Based on the API entity class must return the
	 * Unity Class of the entity to load and update.
	 */
	protected abstract Class   getUnityClass(Holder holder);

	/**
	 * Updates the entity loaded from the database
	 * with the API source object passed.
	 */
	protected abstract void    update(Object entity, Object source);

	@SuppressWarnings("unchecked")
	protected Object           loadEntity(Holder h)
	{
		//?: {API class is not a standard}
		if(!(h.getEntity() instanceof TwoKeysObject))
			return null;

		//~: get the primary key
		Long pk = ((TwoKeysObject)h.getEntity()).getPkey();
		if(pk == null) throw new IllegalArgumentException(String.format(
		  "Executor %s got API instance to update of type %s without " +
		  "the primary key specified!",

		  getClass().getSimpleName(), h.getEntity().getClass().getName()
		));

		//~: setup the tx-context
		setupTxContext(tx());

		//~: get the instance
		Object object = session().get(getUnityClass(h), pk);

		//?: {not in the database}
		if(object == null) throw new IllegalArgumentException(String.format(
		  "Executor %s got API instance to update of type %s with pkey = [%d] " +
		  "and entity of class %s is not in the database!",

		  getClass().getSimpleName(), h.getEntity().getClass().getName(),
		  pk, h.getTypeClass().getSimpleName()
		));

		return object;
	}

	protected void             setupTxContext(Tx tx)
	{}

	protected void             checkEntity(Object obj)
	{
		//sec: check the domain
		checkDomain(obj);
	}

	protected String           getLog()
	{
		return getClass().getName();
	}
}