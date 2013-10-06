package com.tverts.model;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Extends Data Select Model to support search
 * objects (of a known types) by the name field,
 * and by a Selection Set.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ObjectsSelectModelBean
       extends        DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String   getSearchNames()
	{
		return searchNames;
	}

	public void     setSearchNames(String searchNames)
	{
		this.searchNames = searchNames;
	}

	public String   getSelSet()
	{
		return selSet;
	}

	public void     setSelSet(String selSet)
	{
		this.selSet = selSet;
	}


	/* public: support interface */

	public String[] searchNames()
	{
		String[] res = SU.s2a(getSearchNames());
		return (res.length == 0)?(null):(res);
	}


	/* private: objects search attributes */

	private String searchNames;
	private String selSet;
}