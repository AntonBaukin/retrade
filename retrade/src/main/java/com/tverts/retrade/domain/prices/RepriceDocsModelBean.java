package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: models */

import com.tverts.model.DataSelectModel;
import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.RepriceDocsModelData;


/**
 * Model bean for table with views on all
 * price change documents of the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class      RepriceDocsModelBean
       extends    ModelBeanBase
       implements DataSelectModel
{
	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new RepriceDocsModelData(this);
	}


	/* public: RepriceDocsModelBean (filters & limits) interface */

	public Integer getDataStart()
	{
		if(dataStart != null)
			return dataStart;

		setDataStart(0);
		return dataStart;
	}

	public void    setDataStart(Integer dataStart)
	{
		this.dataStart = dataStart;
	}

	public Integer getDataLimit()
	{
		if(dataLimit != null)
			return dataLimit;

		setDataLimit(SystemConfig.getInstance().getGridSize());
		return dataLimit;
	}

	public void    setDataLimit(Integer dataLimit)
	{
		this.dataLimit = dataLimit;
	}

	public Date    getMinDate()
	{
		return minDate;
	}

	public void    setMinDate(Date minDate)
	{
		this.minDate = minDate;
	}

	public Date    getMaxDate()
	{
		return maxDate;
	}

	public void    setMaxDate(Date maxDate)
	{
		this.maxDate = maxDate;
	}

	public boolean isFixedOnly()
	{
		return fixedOnly;
	}

	public void    setFixedOnly(boolean fixedOnly)
	{
		this.fixedOnly = fixedOnly;
	}


	/* private: data selection filters & limits */

	private Integer   dataStart;
	private Integer   dataLimit;
	private Date      minDate;
	private Date      maxDate;
	private boolean   fixedOnly = true;
}