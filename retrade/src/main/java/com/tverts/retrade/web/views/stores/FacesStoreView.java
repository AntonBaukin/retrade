package com.tverts.retrade.web.views.stores;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.TradeStore;
import com.tverts.retrade.domain.store.TradeStoreModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The read-only view of Trade Store.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesStoreView extends UnityModelView
{
	/* public: actions */

	public String doSearchGoods()
	{
		//~: search goods
		getModel().setSearchNames(SU.s2s(request().getParameter("searchGoods")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}


	/* public: FacesInvoiceView (bean) interface */

	public TradeStore          getEntity()
	{
		return (TradeStore)super.getEntity();
	}

	public TradeStoreModelBean getModel()
	{
		return (TradeStoreModelBean)super.getModel();
	}


	/* public: view [info] interface */

	public String  getWinmainTitleInfo()
	{
		return formatTitle("Товары склада",
		  getEntity().getCode(), getEntity().getName()
		);
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new TradeStoreModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof TradeStoreModelBean);
	}
}