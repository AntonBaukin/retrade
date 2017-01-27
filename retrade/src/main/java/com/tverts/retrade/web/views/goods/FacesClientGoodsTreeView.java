package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: request */

import com.tverts.servlet.RequestPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.web.data.goods.ClientGoodsTreeModelData;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * The view of the Good Units tree for
 * the current requesting client firm.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesClientGoodsTreeView extends FacesGoodsTreeView
{
	/* Actions */

	public String doViewGoodInfo()
	{
		//~: load the good unit
		String   pk = EX.asserts(RequestPoint.param("goodUnit"));
		GoodUnit gu = bean(GetGoods.class).getGoodUnit(Long.parseLong(pk));

		//sec: good from the same domain
		if(!gu.getDomain().getPrimaryKey().equals(getDomainKey()))
			throw EX.forbid();

		//=: info good
		this.infoGood = gu;

		return null;
	}


	/* View */

	public GoodUnit getInfoGood()
	{
		return infoGood;
	}

	private GoodUnit infoGood;

	public GoodUnit getInfoSuperGood()
	{
		return (infoSuperGood != null)?(infoSuperGood):(infoSuperGood =
		  (getInfoGood() == null)?(null):(getInfoGood().getGoodCalc() == null)?(null):
		    (getInfoGood().getGoodCalc().getSuperGood()));
	}

	private GoodUnit infoSuperGood;


	/* protected: ModelView interface */

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsTreeModelBean);
	}

	protected GoodsTreeModelBean createModelInstance()
	{
		GoodsTreeModelBean mb = new GoodsTreeModelBean();

		//~: override model data
		mb.setDataClass(ClientGoodsTreeModelData.class);

		return mb;
	}
}