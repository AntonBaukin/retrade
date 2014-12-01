package com.tverts.retrade.data;

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
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (firms + prices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.ContractorView;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceChangeView;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;


/**
 * Data provider for price change document info.
 * Provides all the items of the price change.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data") @XmlType(propOrder = {
  "model", "changes", "contractors"
})
public class RepriceDocModelData implements ModelData
{
	/* public: constructors */

	public RepriceDocModelData()
	{}

	public RepriceDocModelData(RepriceDocModelBean model)
	{
		this.model = model;
	}


	/* public: RepriceDocModelData (data access) interface */

	@XmlElement
	public RepriceDocModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "price-change")
	@XmlElementWrapper(name = "price-changes")
	@SuppressWarnings("unchecked")
	public List<PriceChangeView> getChanges()
	{
		if(!ModelRequest.isKey(null))
			return null;

		List sel = bean(GetPrices.class).
		  selectPriceChanges(getModel());

		List res = new ArrayList(sel.size());

		int i = 0; for(Object obj : sel)
			res.add(new PriceChangeView().init(obj).init(++i));
		return res;
	}

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors")
	public List<ContractorView> getContractors()
	{
		if(!ModelRequest.isKey("contractors"))
			return null;

		List<Contractor>     cs = new ArrayList<>(model.repriceDoc().getContractors());
		List<ContractorView> rs = new ArrayList<>(cs.size());

		//~: sort by the name
		Contractors.sort(cs);

		//c: for each contractor
		for(Contractor c : cs)
			rs.add(new ContractorView().init((Object) c));

		return rs;
	}


	/* private: model */

	private RepriceDocModelBean model;
}