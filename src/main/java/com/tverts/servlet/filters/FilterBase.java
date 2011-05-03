package com.tverts.servlet.filters;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/**
 * Represents a {@link Filter} as a
 * {@link FilterReference} returning this instance.
 * It is convenient when registering filters in Spring.
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
}