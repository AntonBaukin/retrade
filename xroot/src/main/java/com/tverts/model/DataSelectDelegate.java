package com.tverts.model;

/* Java */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: support */

import com.tverts.support.IO;
import com.tverts.support.SU;


/**
 * Incapsulates parameters of Data Select,
 * Search, and Sort model interfaces.
 *
 * @author anton.baukin@gmail.com
 */
public class      DataSelectDelegate
       implements DataSelectModel, DataSortModel, DataSearchModel,
                  Externalizable
{
	/* Data Select Model */

	public Integer   getDataStart()
	{
		return dataStart;
	}

	public void      setDataStart(Integer dataStart)
	{
		this.dataStart = (dataStart == null)?(0):(dataStart);
	}

	public Integer   getDataLimit()
	{
		return (dataLimit != 0)?(dataLimit):
		  SystemConfig.getInstance().getGridSize();
	}

	public void      setDataLimit(Integer dataLimit)
	{
		this.dataLimit = (dataLimit == null)?(0):(dataLimit);
	}


	/* Data Sort Model */

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


	/* Data Search Model */

	public String    getSearchNames()
	{
		return searchNames;
	}

	public void      setSearchNames(String searchNames)
	{
		this.searchNames = searchNames;
	}

	public String    getSelSet()
	{
		return selSet;
	}

	public void      setSelSet(String selSet)
	{
		this.selSet = selSet;
	}

	public boolean   isWithSelSet()
	{
		return withSelSet;
	}

	public void      setWithSelSet(boolean withSelSet)
	{
		this.withSelSet = withSelSet;
	}

	public String[]  searchNames()
	{
		String[] res = SU.s2aw(getSearchNames());
		return (res.length == 0)?(null):(res);
	}


	/* private: encapsulated data */

	private int       dataStart;
	private int       dataLimit;
	private String[]  sortProps;
	private boolean[] sortDesc;
	private String    searchNames;
	private String    selSet;
	private boolean   withSelSet;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		o.writeInt(dataStart);
		o.writeInt(dataLimit);

		IO.obj(o, sortProps);
		IO.obj(o, sortDesc);

		IO.str(o, searchNames);
		IO.str(o, selSet);
		o.writeBoolean(withSelSet);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		dataStart = i.readInt();
		dataLimit = i.readInt();

		sortProps = IO.obj(i, String[].class);
		sortDesc  = IO.obj(i, boolean[].class);

		searchNames = IO.str(i);
		selSet      = IO.str(i);
		withSelSet  = i.readBoolean();
	}
}