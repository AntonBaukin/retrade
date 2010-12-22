package com.tverts.servlet.filters;

public interface Filter
{
	/* public: Filter interface */

	public void openFilter(FilterTask task);

	public void closeFilter(FilterTask task);
}