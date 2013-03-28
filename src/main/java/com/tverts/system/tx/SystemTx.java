package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.system.HiberSystem;

/* com.tverts: system tx */

import com.tverts.system.tx.TxPoint.TxContextCreator;


/**
 * Initial transaction context shows Hibernate
 * session within @Transactional scope as a
 * {@link Tx} instance.
 *
 * It allows mark the transaction as to rollback only,
 * it calls Spring' {@code TransactionAspectSupport}.
 *
 * Initial transaction context is in the most cases
 * the first context created.
 *
 *
 * @author anton.baukin@gmail.com
 */
class SystemTx implements Tx
{
	/* constructor */

	private SystemTx(SessionFactory sf)
	{
		if(sf == null) throw new IllegalArgumentException(
		  "System Tx Context can't be created without valid " +
		  "Hibernate Session Factory provided!"
		);

		this.sessionFactory = sf;
	}

	static final TxContextCreator CREATOR = new TxContextCreator()
	{
		/* public: TxContextCreator interface */

		public Tx createTxContext()
		{
			return new SystemTx(
			  HiberPoint.getInstance().getSessionFactory());
		}
	};


	/* public: TxContext interface */

	public long           txn()
	{
		if(txn != null)
			return txn;

		return (txn = newTxn());
	}

	public long           newTxn()
	{
		return HiberSystem.getInstance().
		  createTxNumber(sessionFactory, this);
	}

	public void           free()
	{
		if(!isRollbackOnly())
		{
			Session session = TxPoint.txSession(this);

			session.flush();
			session.clear();
		}
	}

	public SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}

	public boolean        isRollbackOnly()
	{
		return this.rollbackOnly;
	}

	public void           setRollbackFlag()
	{
		setRollbackOnly();
	}

	public void           setRollbackOnly()
	{
		this.rollbackOnly = true;

		try
		{
			TransactionAspectSupport.currentTransactionStatus().
			  setRollbackOnly();
		}
		catch(NoTransactionException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/* private: the context state */

	private SessionFactory sessionFactory;
	private Long           txn;
	private boolean        rollbackOnly;
}