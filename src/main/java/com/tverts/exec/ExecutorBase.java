package com.tverts.exec;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: execution */

import com.tverts.exec.service.ExecTx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

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
		return (name != null)?(name):
		  this.getClass().getSimpleName();
	}

	public void   setName(String name)
	{
		this.name = name;
	}


	/* protected: execution support */

	protected ExecTx  tx()
	{
		return (ExecTx) TxPoint.txContext();
	}

	protected Session session()
	{
		return TxPoint.txSession(tx());
	}

	protected Domain  domain()
	{
		return tx().getDomain();
	}


	/* private: executor configuration */

	private String name;
}