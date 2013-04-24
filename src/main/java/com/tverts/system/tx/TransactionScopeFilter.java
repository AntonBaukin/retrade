package com.tverts.system.tx;

/* tverts.com: servlet filters */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* tverts.com: spring */

import com.tverts.spring.SpringPoint;

/* tverts.com: support */

import static com.tverts.support.SU.s2s;

/**
 * Filter that wraps the root web request
 * with transaction scopes nesting the
 * filter processing.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TransactionScopeFilter extends FilterBase
{
	/* public: Filter interface */

	public void   openFilter(FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		try
		{
			//!: run tx-scopes bean
			accessBean().setFilterTask(task).run();
		}
		catch(RollbackTransaction e)
		{
			//?: {has nested error} don't ignore it
			if(e.getCause() != null)
				task.setError(e.getCause());
		}
		catch(Throwable e)
		{
			task.setError(e);
			task.setBreaked();
		}
	}

	public void   closeFilter(FilterTask task)
	{}

	/* public: TransactionScopeFilter interface */

	public String getScopeBeanName()
	{
		return scopeBeanName;
	}

	public void   setScopeBeanName(String scopeBeanName)
	{
		if((scopeBeanName = s2s(scopeBeanName)) == null)
			throw new IllegalArgumentException();

		this.scopeBeanName = scopeBeanName;
	}

	/* protected: access scope bean */

	protected TransactionScopeBean accessBean()
	{
		Object bean = SpringPoint.beanOrNull(getScopeBeanName());

		if(!(bean instanceof TransactionScopeBean))
			throw new IllegalStateException();
		return (TransactionScopeBean)bean;
	}

	/* private: scope bean name */

	private String scopeBeanName;
}