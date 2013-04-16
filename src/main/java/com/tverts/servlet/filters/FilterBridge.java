package com.tverts.servlet.filters;

/* standard Java classes */

import java.io.IOException;
import java.util.List;

/* Java Servlet api */

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.beanOrNull;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Being a Servlet Filter connects Web application
 * with the application-lever {@link Filter}s.
 *
 * Each bridge may be registered for one stage of
 * servlet filters invocation: request, forward,
 * include, or exception. This constant MUST be
 * defined in 'for' init parameter of the filter.
 *
 * Bridge finds the filters via root {@link FilterReference}.
 * Optional parameter 'filters' defines a Spring configured
 * bean name of the reference. By default it refers
 * {@link FilterPoint}.
 *
 * Note that the same {@link FilterReference} may be
 * defined for several bridges. (Each {@link Filter})
 * gets the stage of the filter invocation as a value
 * of {@link FilterTask#getFilterStage()}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      FilterBridge
       implements javax.servlet.Filter
{
	/* public: (servlet) Filter interface */

	public void doFilter (
	              ServletRequest  request,
	              ServletResponse response,
	              FilterChain     chain
	            )
	  throws IOException, ServletException
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//?: {wrong call context} skip it
			if(!(request  instanceof HttpServletRequest ) ||
			   !(response instanceof HttpServletResponse)
			  )
			{
				chain.doFilter(request, response);
				return;
			}

			//create the filter task
			FilterTask task = createTask(
			  (HttpServletRequest)request,
			  (HttpServletResponse)response,
			  chain);

			//!: process the task
			processTask(task);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void init(FilterConfig cfg)
	  throws ServletException
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			filterStage     = configFilterStage(cfg);
			filterReference = configFilterReference(cfg);
			filters         = collectFilters();
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void destroy()
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			filterStage     = null;
			filterReference = null;
			filters         = null;
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* protected: filter processing */

	protected void            processTask(FilterTask task)
	  throws IOException, ServletException
	{
		//begin the cycle
		try
		{
			task.continueCycle();
		}
		catch(Throwable e)
		{
			if(task.getError() == null)
				task.setError(e);
		}

		//?: {has no error} success
		if(task.getError() == null)
			return;

		if(task.getError() instanceof IOException)
			throw (IOException)task.getError();

		if(task.getError() instanceof ServletException)
			throw (ServletException)task.getError();

		throw new ServletException(task.getError());
	}

	protected static final String PARAM_STAGE     = "for";

	protected FilterStage     getFilterStage()
	{
		return filterStage;
	}

	protected FilterStage     configFilterStage(FilterConfig cfg)
	{
		String stage = s2s(cfg.getInitParameter(PARAM_STAGE));

		if(stage == null)
			throw new IllegalStateException(String.format(
			  "FilterBridge has nor required parameter '%s' set!",
			  PARAM_STAGE));

		try
		{
			return FilterStage.valueOf(stage.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			throw new IllegalStateException(
			  "FilterBridge: parameter '%s' has illegal value!");
		}
	}

	protected static final String PARAM_REFERENCE = "filters";

	protected FilterReference getFilterReference()
	{
		return filterReference;
	}

	protected FilterReference configFilterReference(FilterConfig cfg)
	{
		String name = s2s(cfg.getInitParameter(PARAM_REFERENCE));

		//?: {the bean name is not set} use FilterPoint
		if(name == null)
			return FilterPoint.getInstance();

		Object bean = beanOrNull(name);

		//?: {wrong bean instance}
		if(!(bean instanceof FilterReference))
			throw new IllegalStateException(String.format(
			  "FilterBridge: parameter '%s' defines a bean being " +
			  "not a %s, or the bean is not controlled by Spring.",

			  PARAM_REFERENCE, FilterReference.class.getSimpleName()
			));

		return (FilterReference)bean;
	}

	protected Filter[]        getFilters()
	{
		return filters;
	}

	protected Filter[]        collectFilters()
	{
		List<Filter> filters = getFilterReference().dereferObjects();
		return filters.toArray(new Filter[filters.size()]);
	}

	/* protected: FilterTask implementation */

	protected FilterTask  createTask (
	                        HttpServletRequest  request,
	                        HttpServletResponse response,
	                        FilterChain         chain
	                      )
	{
		Task task = new Task(getFilterStage());

		task.setRequest(request);
		task.setResponse(response);
		task.setFilterCycle(createFilterCycle(task, chain));
		return task;
	}

	protected FilterCycle createFilterCycle (
	                         FilterTask  task,
	                         FilterChain chain
	                      )
	{
		FilterCycle result = new FilterCycle(task, filters);
		result.setTerminal(new FilterChainInvoker(chain));
		return result;
	}

	protected static class Task extends FilterTaskBase
	{
		/* public: constructor */

		public Task(FilterStage filterStage)
		{
			super(filterStage);
		}

		/* public: FilterTask interface */

		public void continueCycle()
		{
			filterCycle.continueCycle();
		}

		/* public: Task interface */

		public void setFilterCycle(FilterCycle filterCycle)
		{
			if(this.filterCycle != null)
				throw new IllegalStateException();
			this.filterCycle = filterCycle;
		}

	/* protected: the cycle */

		private FilterCycle filterCycle;
	}

	/* private: parameters of the bridge */

	private FilterStage     filterStage;
	private FilterReference filterReference;
	private Filter[]        filters;
}