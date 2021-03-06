package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: scripting */

import com.tverts.jsx.JsX;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of the Good Units table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesGoodsView extends ModelView
{
	/* public: actions */

	public String doSearchGoods()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("searchGoods")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}


	/* public: view interface */

	public GoodsModelBean getModel()
	{
		return (GoodsModelBean)super.getModel();
	}

	public String getGoodFoldersTree()
	{
		return (String) JsX.apply("web/views/goods/Goods", "getGoodFoldersTree");
	}


	/* protected: ModelView interface */

	protected GoodsModelBean createModel()
	{
		GoodsModelBean mb = new GoodsModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		//=: select aggregated values
		mb.setAggrValues(true);

		//=: lists-only restriction
		mb.setRestriction("goods.lists");

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsModelBean);
	}
}