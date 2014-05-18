package com.tverts.retrade.domain.store;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.DataSortDelegate;
import com.tverts.model.DataSortModel;
import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.TradeStoreModelData;


/**
 * Model bean to display {@link TradeStore}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "trade-store")
public class      TradeStoreModelBean
       extends    UnityModelBean
       implements DataSelectModel, DataSortModel
{
	public static final long serialVersionUID = 0L;

	/* public: constructors */

	public TradeStoreModelBean()
	{}

	public TradeStoreModelBean(TradeStore ts)
	{
		this.setInstance(ts);
	}


	/* public: TradeStoreModelBean (read) interface */

	public TradeStore store()
	{
		return (TradeStore)accessEntity();
	}

	@XmlElement
	public Long       getObjectKey()
	{
		return (store() == null)?(null):(store().getPrimaryKey());
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


	/* public: TradeStoreModelBean (bean) interface */

	public String[]   getSearchGoods()
	{
		return searchGoods;
	}

	public void       setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}


	/* public: DataSelectModel interface */

	public String[]   getSortProps()
	{
		return sortDelegate.getSortProps();
	}

	public void       setSortProps(String[] sortProps)
	{
		sortDelegate.setSortProps(sortProps);
	}

	public void       addSort(String prop, boolean desc)
	{
		sortDelegate.addSort(prop, desc);
	}

	public void       clearSort()
	{
		sortDelegate.clearSort();
	}

	public boolean[]  getSortDesc()
	{
		return sortDelegate.getSortDesc();
	}

	public int        sortSize()
	{
		return sortDelegate.sortSize();
	}

	public void       setSortDesc(boolean[] sortDesc)
	{
		sortDelegate.setSortDesc(sortDesc);
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new TradeStoreModelData(this);
	}


	/* private: model attributes */

	private Integer       dataStart;
	private Integer       dataLimit;
	private String[]      searchGoods;

	private DataSortModel sortDelegate =
	  new DataSortDelegate();
}