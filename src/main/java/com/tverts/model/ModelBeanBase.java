package com.tverts.model;

/* standard Java classes */

import java.util.Date;


/**
 * Implementation base for model beans. Note that
 * this class is also serializable what is not
 * required for model beans as XML mapping is
 * frequently adopted.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelBeanBase implements ModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: ModelBean (Java Bean) interface */

	public String  getModelKey()
	{
		return modelKey;
	}

	public void    setModelKey(String key)
	{
		this.modelKey = key;
	}

	public Date    getUpdateTime()
	{
		return updateTime;
	}

	public void    setUpdateTime(Date updateTime)
	{
		this.updateTime = updateTime;
	}


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return null;
	}


	/* protected: support interface */

	protected void    markUpdated()
	{
		Date ut = getUpdateTime();

		//?: {the time is changed} update the attribute
		if((ut == null) || (ut.getTime() != System.currentTimeMillis()))
			this.setUpdateTime(new Date());
	}

	protected boolean updateq(Object cur, Object tst)
	{
		return (cur != null) && !cur.equals(tst);
	}


	/* private: attributes */

	private String modelKey;
	private Date   updateTime;
}