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

	public Long     domain()
	{
		if(getDomain() == null) throw new IllegalStateException(
		  "Domain primary keys is not defined in the bean model " +
		  getClass().getSimpleName() + "!"
		);

		return getDomain();
	}

	public Long     getDomain()
	{
		return domain;
	}

	public void     setDomain(Long domain)
	{
		this.domain = domain;
	}

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

		setDataLimit(SystemConfig.getInstance().getGridSize());
		return dataLimit;
	}

	public void     setDataLimit(Integer dataLimit)
	{
		if(!updateq(this.dataLimit, dataLimit))
			markUpdated();

		this.dataLimit = dataLimit;
	}


	/* private: attributes */

	private Long    domain;
	private Integer dataStart;
	private Integer dataLimit;
}