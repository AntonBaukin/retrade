package com.tverts.servlet.filters;

/* standard Java classes */

import java.util.List;

/**
 * A reference to a list of filters. Needed to
 * build a list of filters from Spring configuration.
 *
 * This is a composite (tree) structure: the aggregated
 * references are invoked in natural in-depth sequence.
 * (As they are nested in Spring XML file.)
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface FilterReference
{
	/* public: FilterReference interface */

	public List<Filter> dereferFilters();
}