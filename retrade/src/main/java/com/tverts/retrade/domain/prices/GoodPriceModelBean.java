package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.ModelData;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.GoodPriceModelData;


/**
 * Model bean to display {@link GoodPrice}.
 *
 * Data selection model here is for displaying
 * history of the good prices of this position.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-price")
public class      GoodPriceModelBean
       extends    NumericModelBean
       implements DataSelectModel
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public GoodPriceModelBean()
	{}

	public GoodPriceModelBean(GoodPrice gp)
	{
		this.setInstance(gp);
	}


	/* public: GoodPriceModelBean (read) interface */

	public GoodPrice  goodPrice()
	{
		return (GoodPrice)accessNumeric();
	}

	@XmlElement
	public Long       getObjectKey()
	{
		return (goodPrice() == null)?(null):
		  (goodPrice().getPrimaryKey());
	}


	/* public: GoodPriceModelBean (bean) interface */

	public Date getMinDate()
	{
		return minDate;
	}

	public void setMinDate(Date minDate)
	{
		this.minDate = minDate;
	}

	public Date getMaxDate()
	{
		return maxDate;
	}

	public void setMaxDate(Date maxDate)
	{
		this.maxDate = maxDate;
	}


	/* public: DataSelectModel interface */

	public Integer    getDataStart()
	{
		if(dataStart != null)
			return dataStart;

		setDataStart(0);
		return dataStart;
	}

	public void       setDataStart(Integer dataStart)
	{
		this.dataStart = dataStart;
	}

	public Integer    getDataLimit()
	{
		if(dataLimit != null)
			return dataLimit;

		setDataLimit(SystemConfig.getInstance().getGridSize());
		return dataLimit;
	}

	public void       setDataLimit(Integer dataLimit)
	{
		this.dataLimit = dataLimit;
	}



	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return new GoodPriceModelData(this);
	}


	/* private: model attributes */

	private Integer   dataStart;
	private Integer   dataLimit;
	private Date      minDate;
	private Date      maxDate;
}