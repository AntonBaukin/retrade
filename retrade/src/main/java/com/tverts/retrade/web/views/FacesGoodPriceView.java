package com.tverts.retrade.web.views;

/* standard Java classes */

import java.util.Date;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.NumericModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceModelBean;
import com.tverts.retrade.domain.prices.PriceListModelBean;


/**
 * The read-only view of Price List entry (Good Price).
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesGoodPriceView extends NumericModelView
{
	/* public: actions */

	public String gotoPriceList()
	{
		getModel().setActive(false);
		return "price-list";
	}


	/* public: bean read interface */

	public GoodPrice          getNumeric()
	{
		return (GoodPrice)super.getNumeric();
	}

	public GoodUnit           getGood()
	{
		return getNumeric().getGoodUnit();
	}

	public GoodPriceModelBean getModel()
	{
		return (GoodPriceModelBean)super.getModel();
	}


	/* public: view [history] interface */

	public String  getWinmainTitleHistory()
	{
		return String.format("История цен товара п/л №%s (%s)",
		  getNumeric().getPriceList().getCode(),
		  getNumeric().getPriceList().getName()
		);
	}

	public String  getGoodPrice()
	{
		return (getNumeric().getPrice() == null)?(null):
		  (getNumeric().getPrice().toString());
	}


	/* protected: NumericModelView interface */

	protected NumericModelBean createModelInstance(Long objectKey)
	{
		//~: get the price list model
		PriceListModelBean plmb =
		  findRequestedModel(PriceListModelBean.class);
		if(plmb == null) throw new IllegalStateException();

		//~: load the good price
		GetGoods  gg = bean(GetGoods.class);
		GoodPrice gp = gg.getGoodPrice(
		  plmb.priceList(), gg.getGoodUnit(objectKey));
		if(gp == null) throw new IllegalStateException();

		//~: create the model bean
		GoodPriceModelBean mb = new GoodPriceModelBean(gp);

		//~: the domain
		mb.setDomain(getDomainKey());

		//~: max search date (present time)
		mb.setMaxDate(new Date());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodPriceModelBean);
	}
}