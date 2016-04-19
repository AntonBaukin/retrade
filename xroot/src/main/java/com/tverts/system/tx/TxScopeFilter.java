package com.tverts.system.tx;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* tverts.com: servlet filters */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.PickFilter;

/* tverts.com: spring */

import com.tverts.spring.SpringPoint;


/**
 * Filter that wraps the root web request with
 * transaction scopes nesting the filter processing.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 1000 })
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
			createTxScopesBean().execute(task::continueCycle);
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


	/* protected: access tx-scopes bean */

	protected TxBean createTxScopesBean()
	{
		return SpringPoint.bean(TxBean.class);
	}
}