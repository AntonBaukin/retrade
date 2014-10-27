package com.tverts.retrade.web.views.prices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.NumericModelView;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.PriceListModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The read-only view of Price List.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesPriceListView extends NumericModelView
{
	/* public: actions */

	public String doSearchGoods()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("searchGoods")));

		return null;
	}

	public String gotoPricesHistory()
	{
		GetGoods  gg = bean(GetGoods.class);
		GoodPrice gp = gg.getGoodPrice(getNumeric(),
		  gg.getGoodUnit(obtainEntityKeyFromRequestStrict()));

		return (gp == null)?(null):("prices-history");
	}


	/* public: FacesPriceListView (bean) interface */

	public PriceListEntity    getNumeric()
	{
		return (PriceListEntity)super.getNumeric();
	}

	public PriceListModelBean getModel()
	{
		return (PriceListModelBean)super.getModel();
	}


	/* public: view [info] interface */

	public String  getWinmainTitleInfo()
	{
		return String.format("Прайс лист №%s (%s)",
		  getNumeric().getCode(), getNumeric().getName()
		);
	}


	/* protected: UnityModelView interface */

	protected PriceListModelBean createModelInstance(Long objectKey)
	{
		PriceListModelBean mb = new PriceListModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: entity key
		mb.setObjectClass(PriceListEntity.class);

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PriceListModelBean);
	}
}