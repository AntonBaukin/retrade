package com.tverts.retrade.web.data.other;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.SimpleModelBean;
import com.tverts.model.SimpleModelData;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.volume.AggrVolumeView;
import com.tverts.endure.aggr.volume.DatePeriodVolumeCalcItem;


/**
 * Simplified Model Data for the given
 * {@link DatePeriodVolumeCalcItem}s.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "size", "items"})
public class AggrVolumeData implements SimpleModelData
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public AggrVolumeData()
	{}

	public AggrVolumeData(SimpleModelBean model, List<DatePeriodVolumeCalcItem> items)
	{
		this.model = model;
		this.items = items;
	}


	/* public: bean interface */

	public SimpleModelBean getModel()
	{
		return model;
	}

	public void setModel(SimpleModelBean model)
	{
		this.model = model;
	}

	@XmlElement(name = "size")
	public int getSize()
	{
		return items.size();
	}

	@XmlElement(name = "aggr-volume")
	@XmlElementWrapper(name = "aggr-volumes")
	public List<AggrVolumeView> getItems()
	{
		List<AggrVolumeView> res =
		  new ArrayList<AggrVolumeView>(items.size());

		for(DatePeriodVolumeCalcItem i : items)
			res.add(new AggrVolumeView().init(i));

		return res;
	}


	/* private: items */

	private SimpleModelBean                model;
	private List<DatePeriodVolumeCalcItem> items;
}