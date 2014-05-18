package com.tverts.retrade.data.system;

/* standard Java classes */

import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (code) */

import com.tverts.retrade.domain.core.DomainsModelBean;
import com.tverts.retrade.domain.core.GetDomainViews;


/**
 * Data provider for view all the Domains model bean.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "domainsNumber", "domains"})
public class DomainsModelData implements ModelData
{
	/* public: constructors */

	public DomainsModelData()
	{}

	public DomainsModelData(DomainsModelBean model)
	{
		this.model = model;
	}


	/* public: DomainsModelData (bean) interface */

	@XmlElement
	public DomainsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getDomainsNumber()
	{
		return bean(GetDomainViews.class).
		  countDomains(getModel());
	}

	@XmlElement(name = "domain")
	@XmlElementWrapper(name = "domains")
	public List<CatItemView> getDomains()
	{
		return bean(GetDomainViews.class).
		  selectDomainViews(getModel());
	}


	/* private: model */

	private DomainsModelBean model;
}
