package com.tverts.servlet.filters;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Intermediate class to store references to real filters
 * or nested collections of filters.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class FiltersList implements FilterReference
{
	/* public: default constructor */

	public FiltersList()
	{
		this.filters = Collections.emptyList();
	}

	/* public: FilterReference interface */

	public List<Filter> dereferFilters()
	{
		List<Filter> res = new ArrayList<Filter>(filters.size());

		for(FilterReference fr : getFilters())
			res.addAll(fr.dereferFilters());
		return res;
	}

	/* public: FiltersList bean */

	/**
	 * Returns a read-only list of the filters registered.
	 */
	public List<FilterReference>
	            getFilters()
	{
		return this.filters;
	}

	/**
	 * Saves a copy of the filters list provided.
	 */
	public void setFilters(List<FilterReference> filters)
	{
		if((filters == null) || filters.isEmpty())
			this.filters = Collections.emptyList();
		else
			this.filters = Collections.unmodifiableList(
			  new ArrayList<FilterReference>(filters));
	}

	/* private: own list */

	private List<FilterReference> filters;
}