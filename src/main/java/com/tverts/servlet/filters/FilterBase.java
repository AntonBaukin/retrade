package com.tverts.servlet.filters;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

public abstract class FilterBase
       implements     Filter, FilterReference
{
	/* public: FilterReference interface */

	public List<Filter> dereferFilters()
	{
		return Collections.<Filter>singletonList(this);
	}
}