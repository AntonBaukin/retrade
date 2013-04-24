package com.tverts.servlet.filters;

/* com.tverts: servlets */

import static com.tverts.servlet.filters.FilterStage.FORWARD;
import static com.tverts.servlet.filters.FilterStage.INCLUDE;
import static com.tverts.servlet.filters.FilterStage.REQUEST;


/**
 * Implementation base for go-filters.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GoFilterBase extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		//?: {this is exact include-forward}
		if(isExactRequest(task))
		{
			runExactMatch(task);
			return;
		}

		//?: {this is not a go-request}
		String page = isGoRequest(task);
		if(page == null) return;

		//~: try forward
		tryForward(task, page);
	}

	public void closeFilter(FilterTask task)
	{}


	/* public: GoFilterBase (bean) interface */

	public GoDisper getDisper()
	{
		return disper;
	}

	public void     setDisper(GoDisper disper)
	{
		if(disper == null) throw new IllegalArgumentException();
		this.disper = disper;
	}


	/* protected: request processing (abstractions) */

	/**
	 * Tells that the URI given matches the target
	 * servlet (faces, pages, etc.) exactly.
	 */
	protected abstract boolean isExactURI(String uri);

	/**
	 * Variates forwarding of not-certain requests.
	 * See {@link #tryForward(FilterTask, String)}.
	 *
	 * Note that here page name doesn't start with '?'.
	 */
	protected abstract boolean varForward(FilterTask task, String page);


	/* protected: request processing */

	protected boolean isExactRequest(FilterTask task)
	{
		FilterStage fs = task.getFilterStage();

		//?: {forward-include + name match}
		return (FORWARD.equals(fs) || INCLUDE.equals(fs)) &&
		  isExactURI(task.getRequest().getRequestURI());
	}

	/**
	 * See {@link GoDisper#isGoRequest(FilterTask)}
	 * for the result format.
	 */
	protected String  isGoRequest(FilterTask task)
	{
		if(!REQUEST.equals(task.getFilterStage()))
			return null;

		return getDisper().isGoRequest(task);
	}

	/**
	 * Attempts to forward the page given changing
	 * it's name when not certain (starts with '?').
	 * Returns true when actually forwarded.
	 */
	protected boolean tryForward(FilterTask task, String page)
	{
		//?: {got certain page name}
		if(page.charAt(0) != '?')
		{
			if(!runForward(task, page))
				throw new IllegalStateException();

			return true;
		}

		//~: variate the forwarding
		return varForward(task, page.substring(1));
	}

	/**
	 * Tries to forward the page given returning
	 * true on the success. Implementation may
	 * try to forward several pages for no-certain
	 * {@link #isGoRequest(FilterTask)}}.
	 */
	protected boolean runForward(FilterTask task, String page)
	{
		if(getDisper().dispatch(task, page))
		{
			//~: finish go-filtering
			task.setBreaked();

			return true;
		}

		return false;
	}

	protected void    runExactMatch(FilterTask task)
	{}


	/* private: the dispatcher */

	private GoDisper disper = new GoDisperCached();
}