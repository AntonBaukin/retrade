package com.tverts.servlet.filters;

/* Java */

import java.util.Collections;
import java.util.List;


/**
 * Implementation base for Filters.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FilterBase
       implements     Filter, FilterReference
{
	/* public: FilterReference interface */

	public List<Filter> dereferObjects()
	{
		return Collections.<Filter>singletonList(this);
	}


	/* Filter */

	public void openFilter(FilterTask task)
	{}

	public void closeFilter(FilterTask task)
	{}
}