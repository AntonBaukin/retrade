package com.tverts.retrade.web.views.goods;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: models */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodAttrsModelBean;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * View on Good Unit Attributes.
 *
 * @author anton.baukin@gmail.com.
 */
@Component @Scope("request")
public class FacesGoodAttrsView extends ModelView
{
	/* public: view interface */

	public GoodAttrsModelBean getModel()
	{
		return (GoodAttrsModelBean)super.getModel();
	}


	/* protected: ModelView interface */

	protected GoodAttrsModelBean createModel()
	{
		GoodAttrsModelBean mb = new GoodAttrsModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		//?: {has good specified}
		Long pk = obtainEntityKeyFromRequest();
		if(pk != null)
		{
			GoodUnit gu = EX.assertn(bean(GetGoods.class).getGoodUnit(pk));

			//sec: good of this domain
			forceSameDomain(gu);

			//=: good unit
			mb.setGoodUnit(gu.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodAttrsModelBean);
	}
}