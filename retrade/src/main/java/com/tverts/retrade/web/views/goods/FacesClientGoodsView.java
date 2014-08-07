package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.data.goods.ClientGoodsModelData;


/**
 * The view of the Good Units table for
 * the current requesting client firm.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesClientGoodsView extends FacesGoodsView
{
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