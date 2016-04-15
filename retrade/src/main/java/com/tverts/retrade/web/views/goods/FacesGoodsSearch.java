package com.tverts.retrade.web.views.goods;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Special Faces bean to search for the Goods.
 *
 * @author anton.baukin@gmail.com
 */
@Component @Scope("request")
public class FacesGoodsSearch extends ModelView
{
	/* protected: ModelView interface */

	protected GoodsModelBean createModel()
	{
		GoodsModelBean mb = new GoodsModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: search words
		mb.setSearchNames(SU.urld(request().getParameter("searchGoods")));

		//~: additional restriction
		mb.setRestriction(SU.s2s(request().getParameter("restriction")));

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsModelBean);
	}
}