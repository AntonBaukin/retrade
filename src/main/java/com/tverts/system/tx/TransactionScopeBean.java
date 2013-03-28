package com.tverts.system.tx;

/* Spring framework */

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: servlet filters */

import com.tverts.servlet.filters.FilterTask;


/**
 * When HTTP request comes to the system,
 * it creates an instance of these bean and
 * invokes it, thus creating transactional
 * scope, with the callback (filter) given.
 *
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
			//?: {has no unexpected error} check the task error
			if(error == null)
				error = getFilterTask().getError();

			//~: handle the error & do cleanups
			closeScope(error);
		}
	}


	/* protected: transaction scope invocation */

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
		Tx tx = TxPoint.getInstance().getTxContext();

		//!: clear the global transaction contexts
		TxPoint.getInstance().clearTxContexts();

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

	protected Tx createTxContext()
	{
		return TxPoint.getInstance().
		  getTxCreator().createTxContext();
	}


	/* private: nested invocation cycles */

	private FilterTask filterTask;
}