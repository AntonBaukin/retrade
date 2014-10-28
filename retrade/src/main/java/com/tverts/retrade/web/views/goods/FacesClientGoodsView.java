package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: request */

import com.tverts.servlet.RequestPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.data.goods.ClientGoodsModelData;

/* com.tverts: support */

import com.tverts.support.EX;


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

	protected GoodsModelBean createModel()
	{
		GoodsModelBean mb = new ClientGoodsModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ClientGoodsModelBean);
	}


	/* Model Bean */

	@XmlRootElement(name = "model")
	@XmlType(name = "client-goods-model")
	public static class ClientGoodsModelBean extends GoodsModelBean
	{
		public static final long serialVersionUID = 0L;


		/* Model Bean Data Access */

		public ModelData modelData()
		{
			return new ClientGoodsModelData(this);
		}
	}
}