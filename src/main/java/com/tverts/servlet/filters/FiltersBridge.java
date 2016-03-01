package com.tverts.servlet.filters;

/* Java */

import java.io.IOException;

/* Java Servlet */

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: secure */

import com.tverts.secure.ForbiddenException;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Being a Servlet Filter connects web application
 * with the application-lever {@link Filter}s.
 *
 * @author anton.baukin@gmail.com
 */
public class      FiltersBridge
       implements javax.servlet.Filter
{
	/* Servlet Filter */

	public void doFilter (
	              ServletRequest  req,
	              ServletResponse res,
	              FilterChain     chain
	            )
	  throws IOException, ServletException
	{
		FilterTask task = null;

		try
		{
			EX.assertx(req instanceof HttpServletRequest);
			EX.assertx(res instanceof HttpServletResponse);

			//~: create the filter task
			task = createTask(
			  (HttpServletRequest)req,
			  (HttpServletResponse)res,
			  chain
			);

			//~: invoke the default
			if(task == null)
				chain.doFilter(req, res);
			else
			{
				processTask(task);

				//?: {has error}
				if(task.getError() != null)
					throw task.getError();
			}
		}
		catch(Throwable e)
		{
			handleError(task, req, res, e);
		}
	}

	public void init(FilterConfig cfg)
	{}

	public void destroy()
	{}


	/* protected: filter processing */

	protected void        processTask(FilterTask task)
	{
		try
		{
			task.continueCycle();
		}
		catch(Throwable e)
		{
			task.setError(e);
		}
	}

	protected FilterStage getStage(HttpServletRequest req)
	{
		switch(req.getDispatcherType())
		{
			case REQUEST : return FilterStage.REQUEST;
			case INCLUDE : return FilterStage.INCLUDE;
			case FORWARD : return FilterStage.FORWARD;
			case ERROR   : return FilterStage.ERROR;
		}

		return null;
	}

	protected Filter[]    getFilters(FilterStage stage)
	{
		return bean(FiltersPoint.class).getFilters(stage);
	}

	protected FilterTask  createTask (
	                        HttpServletRequest  req,
	                        HttpServletResponse res,
	                        FilterChain         chain
	                      )
	{
		//~: get the filter stage
		FilterStage stage = getStage(req);
		if(stage == null) return null;

		//~: get the filters
		Filter[] filters = getFilters(stage);
		if(filters == null) return null;

		//~: create the task
		Task task = new Task(stage);

		task.setRequest(req);
		task.setResponse(res);

		//~: create the cycle strategy
		task.setFilterCycle(createFilterCycle(task, chain, filters));

		return task;
	}

	protected FilterCycle createFilterCycle (
	                        FilterTask  task,
	                        FilterChain chain,
	                        Filter[]    filters
	                      )
	{
		FilterCycle result = new FilterCycle(task, filters);

		//~: install default terminal filter
		result.setTerminal(new FilterChainInvoker(chain));

		return result;
	}

	protected void        handleError (
	                        FilterTask      task,
	                        ServletRequest  req,
	                        ServletResponse res,
	                        Throwable       e
	                      )
	  throws IOException, ServletException
	{
		//?: {is transparent error}
		//if(EX.isTransparent(e))
		//	return;

		ForbiddenException fe = null;

		//~: unwrap the error
		e = EX.xrt(e);

		//?: {forbidden | servlet wrapper}
		if(e instanceof ForbiddenException)
			fe = (ForbiddenException)e;
		if(e instanceof ServletException)
		{
			Throwable x = EX.xrt(e.getCause());
			if(x instanceof ForbiddenException)
				fe = (ForbiddenException)x;
		}

		//?: {not a forbidden | unable to reset}
		if((fe == null) || res.isCommitted())
			if(e instanceof ServletException)
				throw (ServletException)e;
			else if(e instanceof IOException)
				throw (IOException)e;
			else if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else
				throw new ServletException(e);

		res.reset(); //<-- reset the buffer

		//~: send 403 HTTP error
		if(res instanceof HttpServletResponse)
			((HttpServletResponse)res).sendError(
			  HttpServletResponse.SC_FORBIDDEN, fe.getMessage()
			);
	}


	/* Filter Task */

	protected static class Task extends FilterTaskBase
	{
		/* public: constructor */

		public Task(FilterStage filterStage)
		{
			super(filterStage);
		}


		/* Filter Task */

		public void continueCycle()
		{
			cycle.continueCycle();
		}

		protected FilterCycle cycle;

		public void setFilterCycle(FilterCycle filterCycle)
		{
			EX.assertx(this.cycle == null);
			this.cycle = filterCycle;
		}
	}
}