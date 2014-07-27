package com.tverts.retrade.web.views.prices;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (goods + prices ) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodsModelBean;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.PriceListModelBean;
import com.tverts.retrade.domain.prices.PricesSearchModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Special Faces bean to search for the Goods,
 * Price Lists, and the Prices.
 *
 * @author anton.baukin@gmail.com
 */
@Component @Scope("request")
public class FacesPricesSearch extends ModelView
{
	/* public: ModelProvider interface */

	public ModelBean provideModel()
	{
		if(!ModelRequest.isKey("goods"))
			return super.provideModel();

		//~: create source model
		PricesSearchModelBean sm = createModel();
		ModelBeanBase         mb;

		//?: {price list is not provided} search plain goods
		if(sm.getPriceList() == null)
		{
			GoodsModelBean gm = new GoodsModelBean();
			mb = gm;

			//~: search words
			gm.setSearchGoods(sm.getSearchGoods());
		}
		//~: search with prices
		else
		{
			PriceListModelBean pm = new PriceListModelBean(
			  (PriceListEntity) bean(GetGoods.class).getPriceList(sm.getPriceList())
			);
			mb = pm;

			//~: search words
			pm.setSearchGoods(sm.getSearchGoods());
		}

		//~: domain
		mb.setDomain(sm.getDomain());

		return mb;
	}


	/* protected: ModelView interface */

	protected PricesSearchModelBean createModel()
	{
		PricesSearchModelBean mb = new PricesSearchModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: search words
		String searchGoods = SU.urld(request().getParameter("searchGoods"));
		if(!SU.sXe(searchGoods))
			mb.setSearchGoods(SU.s2s(searchGoods).split("\\s+"));

		//~: price list
		String priceList = SU.s2s(request().getParameter("priceList"));
		if(priceList != null)
		{
			PriceListEntity pl = EX.assertn(bean(GetGoods.class).
			  getPriceList(Long.parseLong(priceList)),
			  "Price List [", priceList, "] not found!"
			);

			//~: sec
			if(!pl.getDomain().getPrimaryKey().equals(mb.getDomain()))
				throw EX.forbid();

			mb.setPriceList(pl.getPrimaryKey());
		}

		//~: good unit selected
		String goodUnit = SU.s2s(request().getParameter("goodUnit"));
		if(goodUnit != null)
		{
			GoodUnit gu = EX.assertn(bean(GetGoods.class).
			  getGoodUnit(Long.parseLong(goodUnit)),
			  "Good Unit [", goodUnit, "] not found!"
			);

			//~: sec
			if(!gu.getDomain().getPrimaryKey().equals(mb.getDomain()))
				throw EX.forbid();

			mb.setGoodUnit(gu.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PricesSearchModelBean);
	}
}