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
import com.tverts.support.misc.HiddenError;


/**
 * Being a Servlet Filter connects web application
 * with the application-lever {@link Filter}s.
 *
 * @author anton.baukin@gmail.com
 */
public class      FiltersBridge
       implements javax.servlet.Filter
{
	/* Constants */

	/**
	 * Constant that defines atribute of a Hidden
	 * Error ocurred during the processing of
	 * a nested request (forwarded, included)
	 * and reported to the outer level.
	 */
	public static final String HIDDEN_ERROR =
	  FiltersBridge.class.getName() + ": Hidden Error";


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
		//?: {hidden}
		HiddenError he = EX.search(e, HiddenError.class);
		if(he != null)
		{
			handleError(task, req, res, he);
			return;
		}

		//?: {forbidden}
		ForbiddenException fe =
		  EX.search(e, ForbiddenException.class);

		if(fe != null)
		{
			handleError(task, req, res, fe);
			return;
		}

		//~: unwrap and throw
		fallbackError(task, req, res, e);
	}

	protected void        handleError (
	                        FilterTask      task,
	                        ServletRequest  req,
	                        ServletResponse res,
	                        HiddenError     he
	                      )
	  throws IOException, ServletException
	{
		//~: get filter stage
		FilterStage stage = (task == null)?(null):(task.getFilterStage());
		if(stage == null)
			stage = getStage((HttpServletRequest)req);

		//?: {has outer request}
		if(FilterStage.REQUEST.equals(stage))
			fallbackError(task, req, res, (Throwable)he);

		//~: assign the attribute
		req.setAttribute(HIDDEN_ERROR, he);
	}

	protected void        handleError (
	                        FilterTask         task,
	                        ServletRequest     req,
	                        ServletResponse    res,
	                        ForbiddenException fe
	                      )
	  throws IOException, ServletException
	{
		//?: {committed the response}
		if(res.isCommitted())
			throw new ServletException(fe);

		res.reset(); //<-- reset the buffer

		//~: send 403 HTTP error
		if(res instanceof HttpServletResponse)
			((HttpServletResponse)res).sendError(
			  HttpServletResponse.SC_FORBIDDEN, fe.getMessage()
			);
	}

	protected void        fallbackError (
	                        FilterTask      task,
	                        ServletRequest  req,
	                        ServletResponse res,
	                        Throwable       e
	                      )
	  throws IOException, ServletException
	{
		e = EX.xrt(e);

		if(e instanceof ServletException)
			throw (ServletException)e;
		else if(e instanceof IOException)
			throw (IOException)e;
		else if(e instanceof RuntimeException)
			throw (RuntimeException)e;
		else
			throw new ServletException(e);
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