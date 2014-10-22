package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.DataSelectDelegate;
import com.tverts.model.DataSortModel;
import com.tverts.model.ModelData;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListModelData;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Model bean to display {@link PriceListEntity}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-list")
public class      PriceListModelBean
       extends    NumericModelBean
       implements DataSelectModel, DataSortModel
{
	public static final long serialVersionUID = 0L;

	/* public: constructors */

	public PriceListModelBean()
	{}

	public PriceListModelBean(PriceListEntity pl)
	{
		this.setInstance(pl);
	}


	/* public: PriceListModelBean (read) interface */

	public PriceListEntity priceList()
	{
		return (PriceListEntity)accessNumeric();
	}

	@XmlElement
	public Long       getObjectKey()
	{
		return (priceList() == null)?(null):
		  (priceList().getPrimaryKey());
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
		return (searchGoods != null)?(searchGoods):
		  (searchGoods = SU.s2a(getSearchGoodsStr()));
	}

	public void       setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}

	public String     getSearchGoodsStr()
	{
		return searchGoodsStr;
	}

	public void       setSearchGoodsStr(String s)
	{
		this.searchGoodsStr = SU.s2s(s);
		if(s != null) this.searchGoods = null;
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
		return new PriceListModelData(this);
	}


	/* private: model attributes */

	private Integer       dataStart;
	private Integer       dataLimit;
	private String        searchGoodsStr;
	private String[]      searchGoods;

	private DataSortModel sortDelegate =
	  new DataSelectDelegate();
}