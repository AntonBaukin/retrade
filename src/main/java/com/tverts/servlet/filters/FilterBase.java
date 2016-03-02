package com.tverts.servlet.filters;

/* Java */

import java.util.Collections;
import java.util.List;


/**
 * Implementation base for Filters.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FilterBase implements Filter
{
	/* Filter */

	public void openFilter(FilterTask task)
	{}

	public void closeFilter(FilterTask task)
	{}
}