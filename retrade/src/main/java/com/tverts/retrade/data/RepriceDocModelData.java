package com.tverts.retrade.data;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.param;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade api (prices) */

import com.tverts.api.retrade.prices.FirmGoodPrice;

/* com.tverts: retrade domain (firms + goods + prices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.ContractorView;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceChangeView;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Data provider for price change document info.
 * Provides all the items of the price change.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = { "model", "changes", "contractors" })
public class RepriceDocModelData implements ModelData
{
	/* public: constructors */

	public RepriceDocModelData()
	{}

	public RepriceDocModelData(RepriceDocModelBean model)
	{
		this.model = model;
	}


	/* Price Change Document Model Data (data access) */

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
		//?: {requested the main changes}
		if(ModelRequest.isKey(null))
		{
			List sel = bean(GetPrices.class).
			  selectPriceChanges(getModel());

			List res = new ArrayList(sel.size());

			int i = 0; for(Object obj : sel)
				res.add(new PriceChangeView().init(obj).init(++i));

			return res;
		}

		//?: {requested the contractor changes}
		if(ModelRequest.isKey("contractor-changes"))
		{
			//~: primary key of the contractor
			String pk = EX.asserts(param("contractor"),
			  "Contractor parameter is not specified!"
			);

			//~: load the contractor
			Contractor co = EX.assertn(bean(GetContractor.class).
			  getContractor(Long.parseLong(pk)));

			//?: {contractor in the set}
			EX.assertx(model.repriceDoc().getContractors().contains(co));

			GetPrices get = bean(GetPrices.class);
			List      res = new ArrayList();

			//~: find entities for that contractor
			for(FirmGoodPrice p : model.repriceDoc().getOx().getFirmPrices())
				if(CMP.eq(co, p.getContractor()))
				{
					PriceChangeView v = new PriceChangeView();
					res.add(v);

					//~: good unit + measure
					GoodUnit gu = EX.assertn(get.getGoodUnit(p.getGood()));
					v.init(gu).init(gu.getMeasure());

					//~: old + new price
					v.setPriceOld(p.getOldPrice());
					v.setPriceNew(p.getNewPrice());

					//~: old price list
					if(p.getOldList() != null)
						v.initListOld(EX.assertn(get.getPriceList(p.getOldList())));

					//~: new price list
					if(p.getNewList() != null)
						v.initListNew(EX.assertn(get.getPriceList(p.getNewList())));
				}

			return res;
		}

		return null;
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