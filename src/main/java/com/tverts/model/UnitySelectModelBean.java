package com.tverts.model;

/* com.tverts: system */

import com.tverts.system.SystemConfig;


/**
 * Extends {@link UnityModelBean} adding
 * {@link DataSelectModel} support.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnitySelectModelBean
       extends        UnityModelBean
       implements     DataSelectModel
{
	public static final long serialVersionUID = 0L;


	/* public: DataSelectModel interface */

	public Integer  getDataStart()
	{
		return (dataStart != null)?(dataStart):(0);
	}

	public void     setDataStart(Integer dataStart)
	{
		this.dataStart = dataStart;
	}

	public Integer  getDataLimit()
	{
		return (dataLimit != null)?(dataLimit):
		  SystemConfig.getInstance().getGridSize();
	}

	public void     setDataLimit(Integer dataLimit)
	{
		this.dataLimit = dataLimit;
	}


	/* private: attributes */

	private Integer dataStart;
	private Integer dataLimit;
}