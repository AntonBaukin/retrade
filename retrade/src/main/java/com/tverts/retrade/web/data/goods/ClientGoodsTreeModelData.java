package com.tverts.retrade.web.data.goods;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: retrade domain (firms + goods + prices ) */

import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.GoodPriceView;


/**
 * Model data provider to display the Goods Tree
 * for the current requesting client firm.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
public class ClientGoodsTreeModelData extends GoodsTreeModelData
{
	/* public: constructors */

	public ClientGoodsTreeModelData()
	{}

	public ClientGoodsTreeModelData(GoodsTreeModelBean model)
	{
		super(model);
	}


	/* Goods Tree Model Data */

	@XmlElement
	public Long getContractor()
	{
		return (contractor != null)?(contractor):(contractor =
		  bean(GetContractor.class).getContractorByFirmKey(SecPoint.clientFirmKeyStrict()));
	}

	private Long contractor;

	public Integer getGoodsNumber()
	{
		return (!isGoodsRequest())?(null):
		  bean(GetPrices.class).countContractorGoodUnits(getModel(), getContractor());
	}

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
