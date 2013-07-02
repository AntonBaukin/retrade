package com.tverts.model;

/**
 * Defines model of selecting data with
 * sorting on one or more properties.
 *
 * @author anton.baukin@gmail.com
 */
public interface DataSortModel
{
	/* public: DataSortModel (bean) interface */

	public String[]  getSortProps();

	public void      setSortProps(String[] ps);

	public boolean[] getSortDesc();

	public void      setSortDesc(boolean[] sd);


	/* public: DataSortModel (support) interface */

	public int       sortSize();

	public void      clearSort();

	public void      addSort(String prop, boolean desc);
}