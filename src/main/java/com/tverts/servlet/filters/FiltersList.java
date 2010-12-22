package com.tverts.servlet.filters;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

public class FiltersList implements FilterReference
{
	/* public: FilterReference interface */

	public List<Filter> dereferFilters()
	{
		List<Filter> res = new ArrayList<Filter>(filters.size());

		for(FilterReference fr : getFilters())
			res.addAll(fr.dereferFilters());
		return res;
	}

	/* public: FiltersList bean */

	public List<FilterReference>
	            getFilters()
	{
		return filters;
	}

	public void setFilters(List<FilterReference> filters)
	{
		this.filters = new ArrayList<FilterReference>(filters);
	}

	/* private: own list */

	private List<FilterReference> filters;
}