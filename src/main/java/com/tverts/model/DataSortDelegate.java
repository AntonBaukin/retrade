package com.tverts.model;

/**
 * Incapsulates data sorting parameters for model.
 *
 * @author anton.baukin@gmail.com
 */
public class DataSortDelegate implements DataSortModel
{
	/* public: DataSortModel (bean) interface */

	public String[]  getSortProps()
	{
		return sortProps;
	}

	public void      setSortProps(String[] sortProps)
	{
		this.sortProps = sortProps;
	}

	public boolean[] getSortDesc()
	{
		return sortDesc;
	}

	public void      setSortDesc(boolean[] sortDesc)
	{
		this.sortDesc = sortDesc;
	}


	/* public: DataSortModel (support) interface */

	public int       sortSize()
	{
		return (sortProps == null)?(0):(sortProps.length);
	}

	public void      clearSort()
	{
		sortProps = null;
		sortDesc  = null;
	}

	public void      addSort(String prop, boolean desc)
	{
		if(sortProps == null)
		{
			sortProps = new String[]  { prop };
			sortDesc  = new boolean[] { desc };
		}
		else
		{
			String[]  ps = new String[sortProps.length + 1];
			boolean[] ds = new boolean[ps.length];

			System.arraycopy(sortProps, 0, ps, 0, sortProps.length);
			System.arraycopy(sortDesc,  0, ds, 0, sortProps.length);

			ps[sortProps.length] = prop;
			ds[sortProps.length] = desc;

			sortProps = ps;
			sortDesc  = ds;
		}
	}


	/* private: sort data */

	private String[]  sortProps;
	private boolean[] sortDesc;
}