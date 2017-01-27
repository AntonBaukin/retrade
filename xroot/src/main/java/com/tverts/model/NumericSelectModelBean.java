package com.tverts.model;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * Extends {@link NumericModelBean} adding
 * Data display interfaces support.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericSelectModelBean
       extends        NumericModelBean
       implements     DataSelectModel, DataSortModel, DataSearchModel
{
	/* Data Select Model */

	public Integer getDataStart()
	{
		return dsd.getDataStart();
	}

	public void setDataStart(Integer dataStart)
	{
		dsd.setDataStart(dataStart);
	}

	public Integer getDataLimit()
	{
		return dsd.getDataLimit();
	}

	public void setDataLimit(Integer dataLimit)
	{
		dsd.setDataLimit(dataLimit);
	}


	/* Data Sort Model */

	public String[] getSortProps()
	{
		return dsd.getSortProps();
	}

	public void setSortProps(String[] sortProps)
	{
		dsd.setSortProps(sortProps);
	}

	public boolean[] getSortDesc()
	{
		return dsd.getSortDesc();
	}

	public void setSortDesc(boolean[] sortDesc)
	{
		dsd.setSortDesc(sortDesc);
	}

	public int sortSize()
	{
		return dsd.sortSize();
	}

	public void clearSort()
	{
		dsd.clearSort();
	}

	public void addSort(String prop, boolean desc)
	{
		dsd.addSort(prop, desc);
	}


	/* Data Search Model */

	public String getSearchNames()
	{
		return dsd.getSearchNames();
	}

	public void setSearchNames(String searchNames)
	{
		dsd.setSearchNames(searchNames);
	}

	public String getSelSet()
	{
		return dsd.getSelSet();
	}

	public void setSelSet(String selSet)
	{
		dsd.setSelSet(selSet);
	}

	public boolean isWithSelSet()
	{
		return dsd.isWithSelSet();
	}

	public void setWithSelSet(boolean withSelSet)
	{
		dsd.setWithSelSet(withSelSet);
	}

	public String[] searchNames()
	{
		return dsd.searchNames();
	}


	/**
	 * Data selection delegate.
	 */
	private final DataSelectDelegate dsd = new DataSelectDelegate();


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		dsd.writeExternal(o);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		dsd.readExternal(i);
	}
}