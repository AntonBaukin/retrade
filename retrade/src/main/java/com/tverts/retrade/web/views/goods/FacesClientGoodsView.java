package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: request */

import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.servlet.RequestPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (goods + firms) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodsModelBean;
import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.data.goods.ClientGoodsModelData;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the Good Units table for
 * the current requesting client firm.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesClientGoodsView extends FacesGoodsView
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

	public boolean isClientFirmUser()
	{
		return (SecPoint.clientFirmKey() != null);
	}

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

	public String getWinmainTitle()
	{
		Contractor c = bean(GetContractor.class).
		  getContractor(EX.assertn(getModel().getContractor()));

		return SU.cats("Цены товаров для к-та :: ", c.getName());
	}


	/* protected: ModelView interface */

	protected GoodsModelBean createModel()
	{
		GoodsModelBean mb = new GoodsModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		//~: override the model data
		mb.setDataClass(ClientGoodsModelData.class);

		//?: {is local domain user} require the contractor
		if(!isClientFirmUser())
		{
			Contractor c = bean(GetContractor.class).getContractor(
			  obtainEntityKeyFromRequestStrict());

			//sec: contractor of the same domain
			EX.assertx(c.getDomain().getPrimaryKey().equals(getDomainKey()));

			//~: set the contractor to the model
			mb.setContractor(c.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodsModelBean);
	}
}