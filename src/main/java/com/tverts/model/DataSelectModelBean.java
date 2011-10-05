package com.tverts.model;

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
		if(dataStart != null)
			return dataStart;

		setDataStart(0);
		return dataStart;
	}

	public void     setDataStart(Integer dataStart)
	{
		if(!updateq(this.dataStart, dataStart))
			markUpdated();

		this.dataStart = dataStart;
	}

	public Integer  getDataLimit()
	{
		if(dataLimit != null)
			return dataLimit;

		setDataLimit(20);
		return dataLimit;
	}

	public void     setDataLimit(Integer dataLimit)
	{
		if(!updateq(this.dataLimit, dataLimit))
			markUpdated();

		this.dataLimit = dataLimit;
	}


	/* private: attributes */

	private Integer dataStart;
	private Integer dataLimit;
}