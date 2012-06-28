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