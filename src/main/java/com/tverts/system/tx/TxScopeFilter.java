package com.tverts.system.tx;

/* tverts.com: servlet filters */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* tverts.com: spring */

import com.tverts.spring.SpringPoint;


/**
 * Filter that wraps the root web request with
 * transaction scopes nesting the filter processing.
 *
 * @author anton.baukin@gmail.com
 */
public class TxScopeFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(final FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		//~: run in tx-scopes
		try
		{
			//!: run tx-scopes bean
			createTxScopesBean().execute(new Runnable()
			{
				public void run()
				{
					task.continueCycle();
				}
			});
		}
		catch(Throwable e)
		{
			//~: do break the filters invocation
			task.setError(e);
			task.doBreak();
		}
		finally
		{
			//!: remove all the transactional contexts
			TxPoint.getInstance().clearTxContexts();
		}
	}

	public void closeFilter(FilterTask task)
	{}


	/* protected: access tx-scopes bean */

	protected TxBean createTxScopesBean()
	{
		return SpringPoint.bean(TxBean.class);
	}
}