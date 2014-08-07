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
		String[] sestr  = null;
		String   seprm  = request().getParameter("searchGoods");
		String   selset = request().getParameter("selset");

		//~: search names
		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");
		getModel().setSearchGoods(sestr);

		//~: selection set
		getModel().setSelSet(selset);

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