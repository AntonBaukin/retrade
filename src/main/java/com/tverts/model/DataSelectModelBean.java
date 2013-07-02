package com.tverts.model;

/* com.tverts: system */

import com.tverts.system.SystemConfig;


/**
 * Contains data select model attributes.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class DataSelectModelBean
       extends        ModelBeanBase
       implements     DataSelectModel, DataSortModel
{
	public static final long serialVersionUID = 0L;


	/* public: DataSelectModel interface */

	public Integer   getDataStart()
	{
		return (dataStart != null)?(dataStart):(0);
	}

	public void      setDataStart(Integer dataStart)
	{
		this.dataStart = dataStart;
	}

	public Integer   getDataLimit()
	{
		return (dataLimit != null)?(dataLimit):
		  SystemConfig.getInstance().getGridSize();
	}

	public void      setDataLimit(Integer dataLimit)
	{
		this.dataLimit = dataLimit;
	}


	/* public: DataSelectModel interface */

	public String[]  getSortProps()
	{
		return sortDelegate.getSortProps();
	}

	public void      setSortProps(String[] sortProps)
	{
		sortDelegate.setSortProps(sortProps);
	}

	public void      addSort(String prop, boolean desc)
	{
		sortDelegate.addSort(prop, desc);
	}

	public void      clearSort()
	{
		sortDelegate.clearSort();
	}

	public boolean[] getSortDesc()
	{
		return sortDelegate.getSortDesc();
	}

	public int       sortSize()
	{
		return sortDelegate.sortSize();
	}

	public void      setSortDesc(boolean[] sortDesc)
	{
		sortDelegate.setSortDesc(sortDesc);
	}


	/* private: attributes */

	private Integer       dataStart;
	private Integer       dataLimit;

	private DataSortModel sortDelegate =
	  new DataSortDelegate();
}