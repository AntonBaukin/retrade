package com.tverts.retrade.data.prices;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (firms + goods + prices) */

import com.tverts.retrade.domain.firm.ContractorView;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.FirmsPricesEditModelBean;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceView;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Model data to fill tables of Contractors,
 * Price Lists, and the Goods prices of the
 * Contractor associated Price Lists edit form.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "contractors", "priceLists", "goods"
})
public class FirmsPricesEditModelData implements ModelData
{
	/* public: constructors */

	public FirmsPricesEditModelData()
	{}

	public FirmsPricesEditModelData(FirmsPricesEditModelBean model)
	{
		this.model = model;
	}


	/* Firms Prices Edit Model Data (bean) */

	@XmlElement
	public FirmsPricesEditModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors")
	public List<ContractorView> getContractors()
	{
		if(!ModelRequest.isKey("contractors"))
			return null;

		GetContractor        get = bean(GetContractor.class);
		List<ContractorView> res = new ArrayList<>(getModel().getContractors().size());

		for(Long pk : getModel().getContractors())
			res.add(new ContractorView().init(EX.assertn(
			  get.getContractorAndFirm(pk),
			  "Contractor [", pk, "] is not found!"
			)));

		//~: sort by the processed name
		Collections.sort(res, new Comparator<ContractorView>()
		{
			public int compare(ContractorView l, ContractorView r)
			{
				return CMP.cmpic(l.getNameProc(), r.getNameProc());
			}
		});

		return res;
	}

	@XmlElement(name = "price-list")
	@XmlElementWrapper(name = "price-lists")
	public List<CatItemView> getPriceLists()
	{
		if(!ModelRequest.isKey("price-lists"))
			return null;

		GetGoods          get = bean(GetGoods.class);
		List<CatItemView> res = new ArrayList<>(getModel().getPriceLists().size());

		for(Long pk : getModel().getContractors())
			res.add(new CatItemView().init(EX.assertn(
			  get.getPriceList(pk),
			  "Price List [", pk, "] is not found!"
			)));

		return res;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	@SuppressWarnings("unchecked")
	public List<GoodPriceView> getGoods()
	{
		if(!ModelRequest.isKey("goods"))
			return null;

		//?: {has no price lists selected}
		if(getModel().getPriceLists().isEmpty())
			return null;

		GetPrices      get   = bean(GetPrices.class);
		List           res   = new ArrayList(getModel().getDataLimit());
		List<GoodUnit> goods = get.searchGoodUnits(getModel());

		for(GoodUnit gu : goods)
		{
			GoodPriceView v; res.add(v = new GoodPriceView());

			//~: assign the good
			v.init(gu);

			//~: find the price
			for(int i = model.getPriceLists().size() - 1;(i >= 0);i++)
			{
				GoodPrice gp = get.getGoodPrice(
				  model.getPriceLists().get(i),
				  gu.getPrimaryKey()
				);

				if(gp != null)
				{
					//~: assign the price
					v.init(gp);

					//~: assign the price list
					v.init(gp.getPriceList());

					break;
				}
			}
		}

		return (List<GoodPriceView>) res;
	}


	/* private: the model */

	private FirmsPricesEditModelBean model;
}