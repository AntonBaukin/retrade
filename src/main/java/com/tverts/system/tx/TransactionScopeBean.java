package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/* com.tverts: servlet filters */

import com.tverts.servlet.filters.FilterTask;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/**
 * TODO comment TransactionScopeBean
 *
 * @author anton.baukin@gmail.com
 */
@Scope("prototype")
public class TransactionScopeBean implements Runnable
{
	/* public: TransactionScopeBean interface */

	public FilterTask           getFilterTask()
	{
		return filterTask;
	}

	public TransactionScopeBean setFilterTask(FilterTask ft)
	{
		this.filterTask = ft;
		return this;
	}

	/* public: Runnable interface */

	@Transactional(rollbackFor = Throwable.class)
	public void run()
	{
		Throwable error = null;

		checkBeforeScope();

		//~: create the transactional scope structures
		prepareScope();

		try
		{
			//!: continue the request processing
			invokeScope();
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			//~: handle the error & do cleanups
			closeScope(error);
		}
	}

	/* protected: trensaction scope invocation */

	protected void checkBeforeScope()
	{
		if(getFilterTask() == null)
			throw new IllegalStateException();
	}

	protected void prepareScope()
	{
		TxPoint.getInstance().
		  setTxContext(createTxContext());
	}

	protected void invokeScope()
	{
		getFilterTask().continueCycle();
	}

	protected void closeScope(Throwable error)
	{
		TxContext tx = TxPoint.getInstance().getTxContext();

		//!: clear the global transaction context
		TxPoint.getInstance().setTxContext(null);

		//?: {has rollback invocation error}
		if(error instanceof RollbackTransaction)
		{
			RollbackTransaction rbe = (RollbackTransaction)error;

			//?: {has no specific context set} set the global one
			if(rbe.getTxContext() == null)
				rbe.setTxContext(tx);

			throw rbe;
		}

		//?: {has uncaught invocation error}
		if(error != null)
			throw new RollbackTransaction(error).setTxContext(tx);

		//?: {is marked to rollback only}
		if(tx.isRollbackOnly())
			throw new RollbackTransaction().setTxContext(tx);
	}

	/* protected: transactional context implementation */

	protected TxContext createTxContext()
	{
		TxContextScope res = new TxContextScope();

		res.setSessionFactory(
		  HiberPoint.getInstance().getSessionFactory());
		return res;
	}

	protected class TxContextScope implements TxContext
	{
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
			this.rollbackOnly = true;
		}

		public void           setRollbackOnly()
		{
			setRollbackFlag();

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

		/* public: TxContextScope interface */

		public void setSessionFactory(SessionFactory sf)
		{
			this.sessionFactory = sf;
		}

		/* private: the context state */

		private SessionFactory sessionFactory;
		private boolean        rollbackOnly;
	}

	/* private: nested invocation cycles */

	private FilterTask filterTask;
}