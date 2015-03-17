package com.tverts.retrade.web.views.stores;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.StoresModelBean;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the Trade Stores table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesStoresView extends ModelView
{
	/* actions */

	public String gotoEditStore()
	{
		//~: load the measure unit
		Long       pk = obtainEntityKeyFromRequestStrict();
		TradeStore ts = bean(GetTradeStore.class).getTradeStore(pk);
		if(ts == null) throw EX.state(
		  "Trade Store [", pk, "] is not found!");

		//sec: entity of else domain
		if(!ts.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid("Trade Store of else Domain!");

		//~: create and init the view
		getModel().setView(new CatItemView().init(ts));

		return "edit";
	}

	public String gotoCreateStore()
	{
		//~: create empty view
		getModel().setView(new CatItemView());

		return "create";
	}

	public String gotoCancelEdit()
	{
		getModel().setView(null);
		return "cancel";
	}

	public String doCommitEdit()
	{
		if(SU.sXe(getStoreView().getCode()))
			throw EX.arg("Trade Store code must be defined!");
		if(SU.sXe(getStoreView().getName()))
			throw EX.arg("Trade Store name must be defined!");

		//~: check the code exists
		formValid = !checkCodeExists(getStoreView().getCode());
		if(!formValid) return null;

		TradeStore ts;

		//?: {create new measure}
		if(getStoreView().getObjectKey() == null)
		{
			ts = new TradeStore();

			//~: domain
			ts.setDomain(loadModelDomain());
		}
		//!: load it
		else
		{
			ts = bean(GetTradeStore.class).
			  getTradeStore(getStoreView().getObjectKey());

			if(ts == null) throw EX.state();
		}

		//=: code
		ts.setCode(getStoreView().getCode());

		//=: name
		ts.setName(getStoreView().getName());

		//=: remarks
		ts.setRemarks(getStoreView().getRemarks());


		//!: save | update it
		if(getStoreView().getObjectKey() == null)
			actionRun(ActionType.SAVE, ts);
		else
			actionRun(ActionType.UPDATE, ts);

		return null;
	}

	public String doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		formValid = (code == null) || !checkCodeExists(code);
		return null;
	}


	/* public: view interface */

	public StoresModelBean getModel()
	{
		return (StoresModelBean)super.getModel();
	}

	public CatItemView getStoreView()
	{
		if(getModel().getView() == null)
			throw EX.state("Edit view of Trade Store not present!");
		return getModel().getView();
	}

	public String getEditWindowTitle()
	{
		if(getStoreView().getObjectKey() == null)
			return "Создание Склада";

		return SU.cats(
		  "Склад [",
		  getStoreView().getCode(), "] ",
		  getStoreView().getName()
		);
	}

	private boolean formValid;

	public boolean isFormValid()
	{
		return formValid;
	}

	private boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}


	/* protected: actions support */

	protected boolean checkCodeExists(String code)
	{
		TradeStore ts = bean(GetTradeStore.class).
		  getTradeStore(getModel().domain(), code);

		return codeExists = (ts != null) && (
		  (getStoreView().getObjectKey() == null) || // <-- creating
		  !getStoreView().getObjectKey().equals(ts.getPrimaryKey())
		);
	}


	/* protected: ModelView interface */

	protected StoresModelBean createModel()
	{
		StoresModelBean mb = new StoresModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof StoresModelBean);
	}
}