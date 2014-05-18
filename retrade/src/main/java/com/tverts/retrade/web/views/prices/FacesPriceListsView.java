package com.tverts.retrade.web.views.prices;

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

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.PriceList;
import com.tverts.retrade.domain.prices.PriceListsModelBean;


/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the Price Lists table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesPriceListsView extends ModelView
{
	/* actions */

	public String gotoEditPriceList()
	{
		//~: load the measure unit
		Long      pk = obtainEntityKeyFromRequestStrict();
		PriceList pl = bean(GetGoods.class).getPriceList(pk);
		if(pl == null) throw EX.state(
		  "Price List [", pk, "] is not found!");

		//sec: entity of else domain
		if(!pl.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid("Price List of else Domain!");

		//~: create and init the view
		getModel().setView(new CatItemView().init(pl));

		return "edit";
	}

	public String gotoCreatePriceList()
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
		if(SU.sXe(getPriceListView().getCode()))
			throw EX.arg("Price List code must be defined!");
		if(SU.sXe(getPriceListView().getName()))
			throw EX.arg("Price List name must be defined!");

		//~: check the code exists
		formValid = !checkCodeExists(getPriceListView().getCode());
		if(!formValid) return null;

		PriceList pl;

		//?: {create new measure}
		if(getPriceListView().getObjectKey() == null)
		{
			pl = new PriceList();

			//~: domain
			pl.setDomain(loadModelDomain());
		}
		//!: load it
		else
		{
			pl = bean(GetGoods.class).
			  getPriceList(getPriceListView().getObjectKey());

			if(pl == null) throw EX.state();
		}

		//~: code
		pl.setCode(getPriceListView().getCode());

		//~: name
		pl.setName(getPriceListView().getName());


		//!: save | update it
		if(getPriceListView().getObjectKey() == null)
			actionRun(ActionType.SAVE, pl);
		else
			actionRun(ActionType.UPDATE, pl);

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

	public PriceListsModelBean getModel()
	{
		return (PriceListsModelBean) super.getModel();
	}

	public CatItemView getPriceListView()
	{
		if(getModel().getView() == null)
			throw EX.state("Edit view of Price List not present!");
		return getModel().getView();
	}

	public String getEditWindowTitle()
	{
		if(getPriceListView().getObjectKey() == null)
			return "Создание Прайс-листа";

		return SU.cats(
		  "Прайс-лист [",
		  getPriceListView().getCode(), "] ",
		  getPriceListView().getName()
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
		PriceList pl = bean(GetGoods.class).
		  getPriceList(getModel().domain(), code);

		return codeExists = (pl != null) && (
		  (getPriceListView().getObjectKey() == null) || // <-- creating
		  !getPriceListView().getObjectKey().equals(pl.getPrimaryKey())
		);
	}


	/* protected: ModelView interface */

	protected PriceListsModelBean createModel()
	{
		PriceListsModelBean mb = new PriceListsModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PriceListsModelBean);
	}
}