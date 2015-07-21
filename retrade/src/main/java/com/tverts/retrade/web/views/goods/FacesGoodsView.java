package com.tverts.retrade.web.views.goods;

/* Java */

import java.util.List;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
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

	public String doAssignGroup()
	{
		String selset = request().getParameter("selset");
		String group  = SU.s2s(request().getParameter("goodsGroup"));
		EX.assertx((selset != null) & (group != null));

		//~: select the goods
		GetGoods   get = bean(GetGoods.class);
		List<Long> ids = get.getSelectedGoodsKeys(selset);

		for(Long id : ids)
		{
			GoodUnit gu = EX.assertn(get.getGoodUnit(id));

			//:= good group
			gu.getOx().setGroup(group);

			//!: update object extraction
			gu.updateOx();
		}

		return null;
	}


	/* public: FacesGoodsView (bean) interface */

	public GoodsModelBean    getModel()
	{
		return (GoodsModelBean)super.getModel();
	}


	/* protected: ModelView interface */

	protected GoodsModelBean createModel()
	{
		GoodsModelBean mb = new GoodsModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsModelBean);
	}
}