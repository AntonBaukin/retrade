package com.tverts.retrade.web.data.firms;

/* Java */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (firms, prices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.ContractorsModelBean;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.prices.FirmPrices;
import com.tverts.retrade.domain.prices.FirmPricesView;


/**
 * Model data provider for the table of all the Contractors
 * of the domain with the associated Price Lists.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "contractorsNumber", "contractors"})
public class ContractorsListsModelData implements ModelData
{
	/* public: constructors */

	public ContractorsListsModelData()
	{}

	public ContractorsListsModelData(ContractorsModelBean model)
	{
		this.model = model;
	}


	/* public: ContractorsModelData (bean) interface */

	@XmlElement
	public ContractorsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getContractorsNumber()
	{
		return bean(GetContractor.class).countContractorsLists(getModel());
	}

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors")
	@SuppressWarnings("unchecked")
	public List<FirmPricesView> getContractors()
	{
		//~: load the contractors
		Map<Contractor, List<FirmPrices>> prices =
		  new LinkedHashMap<Contractor, List<FirmPrices>>(17);
		bean(GetContractor.class).selectContractorsLists(getModel(), prices);

		//~: proceed to the result
		List res = new ArrayList(prices.size());
		for(Object c : prices.keySet())
			res.add(new FirmPricesView().initPrices(prices.get(c)).init(c));

		return (List<FirmPricesView>) res;
	}


	/* private: model */

	private ContractorsModelBean model;
}