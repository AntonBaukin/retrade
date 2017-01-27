package com.tverts.servlet.go;

/* Java */

import java.util.function.Predicate;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterTask;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Structure being a dispatch request.
 *
 * @author anton.baukin@gmail.com
 */
public class GoDispatch
{
	public final FilterTask task;

	public final String     page;

	public GoDispatch(FilterTask task, String page)
	{
		this.task = EX.assertn(task);
		this.page = EX.asserts(page);
	}


	/* Request Options */

	/**
	 * This predicate provides the strategy given
	 * by the callee to check whether the page
	 * file actually exists.
	 */
	public Predicate<GoDispatch> exists;

	public GoDispatch setExists(Predicate<GoDispatch> exists)
	{
		this.exists = exists;
		return this;
	}
}