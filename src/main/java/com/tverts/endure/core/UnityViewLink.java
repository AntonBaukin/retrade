package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;


/**
 * TODO comment UnityViewLink
 *
 * @author anton.baukin@gmail.com
 */
public class UnityViewLink implements NumericIdentity
{
	/* public: PrimaryIdentity interface */

	public Long      getPrimaryKey()
	{
		return primaryKey;
	}

	public void      setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: UnityViewLink (bean) interface */

	public UnityView getView()
	{
		return view;
	}

	public void      setView(UnityView view)
	{
		this.view = view;
	}

	public Unity     getLinked()
	{
		return linked;
	}

	public void      setLinked(Unity linked)
	{
		this.linked = linked;
	}


	/* private: persistent attributes */

	private Long      primaryKey;
	private UnityView view;
	private Unity     linked;
}