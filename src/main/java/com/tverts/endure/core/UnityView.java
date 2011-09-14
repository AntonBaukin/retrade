package com.tverts.endure.core;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * TODO comment UnityView
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityView implements United
{
	/* public: NumericIdentity interface */

	public Long  getPrimaryKey()
	{
		return primaryKey;
	}

	public void  setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: United interface */

	public Unity getUnity()
	{
		return unity;
	}

	public void  setUnity(Unity unity)
	{
		this.unity = unity;
	}


	/* public: UnityView (bean) interface */

	public Unity     getViewOwner()
	{
		return viewOwner;
	}

	public void      setViewOwner(Unity viewOwner)
	{
		this.viewOwner = viewOwner;
	}

	/**
	 * The type of the view. It is allowed the same owner
	 * to have several views of the same type. It is the
	 * task of the implementation to distinguish them.
	 *
	 * If this view has unity, the view type is allowed
	 * to differ from the unity' one.
	 */
	public UnityType getViewType()
	{
		return viewType;
	}

	public void      setViewType(UnityType viewType)
	{
		this.viewType = viewType;
	}

	/**
	 * The timestamp of the view creation or last update.
	 * (Depends on the view use case.) Is not assigned
	 * by default to the present time.
	 */
	public Date      getViewDate()
	{
		return viewDate;
	}

	public void      setViewDate(Date viewDate)
	{
		this.viewDate = viewDate;
	}

	/**
	 * Additional user comment on the view.
	 */
	public String    getViewText()
	{
		return viewText;
	}

	public void      setViewText(String viewText)
	{
		this.viewText = viewText;
	}


	/* persisted attributes  */

	private Long      primaryKey;
	private Unity     unity;

	private Unity     viewOwner;
	private UnityType viewType;
	private Date      viewDate;
	private String    viewText;
}