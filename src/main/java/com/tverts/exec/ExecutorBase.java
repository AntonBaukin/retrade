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

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;

/* com.tverts: support */

import com.tverts.support.EX;


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
		return TxPoint.txContext(ExecTx.class);
	}

	protected Session session()
	{
		//HINT: a context may be nested into exec-tx!
		return TxPoint.txSession();
	}

	protected Domain  domain()
	{
		return tx().getDomain();
	}

	protected void    checkDomain(Object obj)
	{
		if(!(obj instanceof DomainEntity))
			return;

		EX.assertx(
		  domain().equals(((DomainEntity)obj).getDomain()),

		  "Wrong domain! The entity of class [",
		  obj.getClass().getSimpleName(), "] with primary key [",
		  ((NumericIdentity)obj).getPrimaryKey(),
		  "] is not of the client domain!"
		);
	}


	/* private: executor configuration */

	private String name;
}