package com.tverts.retrade.data.goods;

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

/* com.tverts: secure */

import com.tverts.retrade.domain.prices.GoodPriceView;
import com.tverts.secure.SecPoint;

/* com.tverts: retrade domain (firms + goods + prices) */

import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.goods.GoodsModelBean;
import com.tverts.retrade.domain.prices.GetPrices;


/**
 * Goods with their prices specific for
 * the current requesting client firm.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = { "contractor", "goodsNumber", "goods" })
public class ClientGoodsModelData extends GoodsModelData
{
	/* public: constructors */

	public ClientGoodsModelData()
	{}

	public ClientGoodsModelData(GoodsModelBean model)
	{
		super(model);
	}


	/* Goods Model Data */

	@XmlElement
	public Long getContractor()
	{
		return (contractor != null)?(contractor):(contractor =
		  bean(GetContractor.class).getContractorByFirmKey(SecPoint.clientFirmKeyStrict()));
	}

	private Long contractor;

	@XmlElement
	public int getGoodsNumber()
	{
		return bean(GetPrices.class).
		  countContractorGoodUnits(getModel(), getContractor());
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodUnitView> getGoods()
	{
		List sel = bean(GetPrices.class).
		  selectContractorGoodUnits(getModel(), getContractor());
		List res = new ArrayList<GoodUnitView>(sel.size());

		for(Object[] o : (List<Object[]>)sel)
		{
			GoodPriceView v = new GoodPriceView().init(o);
			res.add(v);

			v.setPriceList((Long) o[2]);
			v.setPriceListCode((String) o[3]);
			v.setPriceListName((String) o[4]);
		}

		return res;
	}
}