package com.tverts.system.tx;

/* Spring framework */

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: servlet filters */

import com.tverts.servlet.filters.FilterTask;

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
		checkBeforeScope();
		prepareScope();

		try
		{
			invokeScope();
			closeScope();
		}
		catch(RollbackTransaction e)
		{
			throw e;
		}
		catch(Throwable e)
		{
			throw new RollbackTransaction(e);
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
		TxPoint.getInstance().clearRollbackOnly();
	}

	protected void invokeScope()
	{
		getFilterTask().continueCycle();
	}

	protected void closeScope()
	{
		if(TxPoint.getInstance().isRollbackOnly())
		{
			TxPoint.getInstance().clearRollbackOnly();
			throw new RollbackTransaction();
		}
	}

	/* private: nested invocation cycles */

	private FilterTask filterTask;
}