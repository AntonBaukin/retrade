package com.tverts.servlet.filters;

/* com.tverts: objects */

import com.tverts.objects.ObjectsRedirector;

/**
 * Intermediate class to store references to real filters
 * or nested collections of filters.
 *
 * @author anton.baukin@gmail.com
 */
public class      FiltersList
       extends    ObjectsRedirector<Filter>
       implements FilterReference
{}