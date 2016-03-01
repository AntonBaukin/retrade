package com.tverts.servlet.filters;

/**
 * Implements a cycle of {@link Filter} invocations
 * that may be invoked recursively without repeating
 * the already passed filters, an not loosing a one.
 *
 * Terminal filters mostly stands for Servlet Filter
 * Chain got by {@link FilterBridge}. (To call the next
 * Servlet Filters of the Web application.)
 *
 * @author anton.baukin@gmail.com
 */
public final class FilterCycle
{
	/* public: constructor */

	public FilterCycle(FilterTask task, Filter[] filters)
	{
		this.task    = task;
		this.filters = filters;
	}

	/* public: FilterCycle (entry point) */

	public void continueCycle()
	{
		//~: 'first' stores the position of the first
		//   cycle invoked.
		int first = position;

		//~: 'last' points to the last filter invoked.
		//   Note that all the filters in the range
		//   [first; last] are invoked in this call,
		//   but the filters after 'last' (if any) are
		//   invoked when filter recursively continues
		//   the cycle.
		int last  = position;

		//open the filters left to call
		while(position < filters.length)
		{
			//?: {we need to break the cycle}
			if(task.isBreaked())
				break;

			//save the position of last invoked filter
			last = position++;

			try
			{
				filters[last].openFilter(task);
			}
			catch(Throwable e)
			{
				//!: this error is not pleased, we need to
				//   break the cycle manually.

				//?: {got no previous error}
				if(task.getError() == null)
					task.setError(e);
			}

			//?: {has error} do break
			if(task.getError() != null)
				task.doBreak();
		}

		//?: {have terminal & not breaked & got the tail} invoke terminal
		if(!task.isBreaked() && (terminal != null) &&
		   (last + 1 >= filters.length)
		  )
		try
		{
			terminal.openFilter(task);
		}
		catch(Exception e)
		{
			task.setError(e);
		}
		finally
		{
			try
			{
				terminal.closeFilter(task);
			}
			catch(Exception e)
			{
				if(task.getError() == null)
					task.setError(e);
			}
		}

		//close the filters of our range [first; last]
		for(int i = last;(i >= first);i--) try
		{
			filters[i].closeFilter(task);
		}
		catch(Throwable e)
		{
			//!: error here must not occur, but we have
			//   nothing to do, but to save it

			//?: {got no previous error}
			if(task.getError() == null)
				task.setError(e);
		}
	}

	/* public: FilterCycle (support) */

	/**
	 * Terminal filter is invoked in the cycle after passing
	 * through all other filters. It is invoked if the
	 * cycle was not breaked, and there were no error.
	 */
	public Filter getTerminal()
	{
		return terminal;
	}

	public void   setTerminal(Filter terminal)
	{
		this.terminal = terminal;
	}

	/* private: the state of the cycle */

	private FilterTask task;
	private Filter[]   filters;
	private Filter     terminal;
	private int        position;
}