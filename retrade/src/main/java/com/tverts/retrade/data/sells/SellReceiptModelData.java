package com.tverts.retrade.data.sells;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods, sells) */

import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.GoodSell;
import com.tverts.retrade.domain.sells.SellReceipt;
import com.tverts.retrade.domain.sells.SellReceiptModelBean;


/**
 * Model data to list the goods of a Sell Receipt.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goods"})
public class SellReceiptModelData implements ModelData
{
	/* public: constructors */

	public SellReceiptModelData()
	{}

	public SellReceiptModelData(SellReceiptModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public SellReceiptModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "good-sell")
	@XmlElementWrapper(name = "good-sells")
	public List<GoodUnitView> getGoods()
	{
		SellReceipt        rec = getModel().sellReceipt();
		List<GoodUnitView> res =
		  new ArrayList<GoodUnitView>(rec.getGoods().size());
		GoodUnitView       guv;

		for(GoodSell gs : rec.getGoods())
		{
			res.add(guv = new GoodUnitView().
			  init(gs.getGoodUnit()).
			  init(gs.getStore()).
			  initVolume(gs.getVolume()).
			  initCost(gs.getCost())
			);

			if(gs.getGoodPrice() != null)
				guv.init(gs.getGoodPrice());
		}

		return res;
	}


	/* the model */

	private SellReceiptModelBean model;
}