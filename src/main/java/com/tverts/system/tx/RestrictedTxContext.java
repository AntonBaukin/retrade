package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/* com.tverts: hybery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system tx */

import com.tverts.system.tx.TxPoint.TxContextCreator;


/**
 * Restricted transaction context shows Hibernate
 * session within @Transactional scope as a
 * {@link TxContext} instance.
 *
 * It always marks the transaction as to rollback only
 * via call to Spring' {@code TransactionAspectSupport}.
 *
 * Restricted contexts are used in the system components
 * not bound to an actual user request.
 *
 *
 * @author anton.baukin@gmail.com
 */
class RestrictedTxContext implements TxContext
{
	/* constructor */

	private RestrictedTxContext(SessionFactory sf)
	{
		if(sf == null) throw new IllegalArgumentException(
		  "Restricted Tx Context can't be cerated without valid " +
		  "Hibernate Session Factory provided!"
		);

		this.sessionFactory = sf;
	}

	static final TxContextCreator CREATOR = new TxContextCreator()
	{
		/* public: TxContextCreator interface */

		public TxContext createTxContext()
		{
			return new RestrictedTxContext(
			  HiberPoint.getInstance().getSessionFactory());
		}
	};

	/* public: TxContext interface */

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
	private boolean        rollbackOnly;
}