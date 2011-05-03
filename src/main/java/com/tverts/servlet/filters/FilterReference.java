package com.tverts.servlet.filters;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;

/**
 * A reference to a list of filters. Needed to
 * build a list of filters from Spring configuration.
 *
 * This is a composite (tree) structure: the aggregated
 * references are invoked in natural in-depth sequence.
 * (As they are nested in Spring XML file.)
 *
 * @author anton.baukin@gmail.com
 */
public interface FilterReference
       extends   ObjectsReference<Filter>
{}