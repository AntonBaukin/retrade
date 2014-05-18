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
		String[] sestr = null;
		String   seprm = request().getParameter("searchGoods");

		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");

		getModel().setSearchGoods(sestr);
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
		return String.format("Товары склада №%s (%s)",
		  getEntity().getCode(), getEntity().getName()
		);
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		TradeStoreModelBean mb = new TradeStoreModelBean();

		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean        isRequestModelMatch(ModelBean model)
	{
		return (model instanceof TradeStoreModelBean);
	}
}