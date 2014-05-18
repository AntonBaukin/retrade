package com.tverts.retrade.data.other;

/* standard Java classes */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelData;

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
@XmlType(propOrder = {"size", "items"})
public class AggrVolumeData implements ModelData, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public AggrVolumeData()
	{}

	public AggrVolumeData(ModelBean model, List<DatePeriodVolumeCalcItem> items)
	{
		this.model = model;
		this.items = items;
	}


	/* public: bean interface */

	public ModelBean getModel()
	{
		return model;
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

	private ModelBean                      model;
	private List<DatePeriodVolumeCalcItem> items;
}