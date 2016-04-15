package com.tverts.retrade.web.data.firms;

/* Java */

import java.util.ArrayList;
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

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.ContractorView;
import com.tverts.retrade.domain.firm.ContractorsModelBean;
import com.tverts.retrade.domain.firm.GetContractor;


/**
 * Model data provider for the table of all the
 * contractors of the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "contractorsNumber", "contractors"})
public class ContractorsModelData implements ModelData
{
	/* public: constructors */

	public ContractorsModelData()
	{}

	public ContractorsModelData(ContractorsModelBean model)
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
		return bean(GetContractor.class).countContractors(getModel());
	}

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors")
	@SuppressWarnings("unchecked")
	public List<ContractorView> getContractors()
	{
		List                 sel = bean(GetContractor.class).
		  selectContractors(getModel());
		List<ContractorView> res = new ArrayList<ContractorView>(sel.size());

		for(Object obj : sel)
			res.add(new ContractorView().init(obj));

		return res;
	}


	/* private: model */

	private ContractorsModelBean model;
}