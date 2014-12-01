package com.tverts.retrade.web.views.reprices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (prices) */

import com.tverts.model.UnityModelBean;
import com.tverts.retrade.domain.prices.Prices;
import com.tverts.retrade.domain.prices.RepriceDoc;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * The read-only view of price change document.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesRepriceDocView extends UnityModelView
{
	/* public: actions */

	public String gotoEditReprice()
	{
		return "edit-reprice";
	}

	public String doFixReprice()
	{
		actionRun(Prices.ACT_FIX_PRICES, getModel().repriceDoc());
		return "fixed";
	}


	/* Faces Price Change Document */

	public RepriceDoc          getEntity()
	{
		return (RepriceDoc) super.getEntity();
	}

	public RepriceDocModelBean getModel()
	{
		return (RepriceDocModelBean)super.getModel();
	}


	/* public: view [info] interface */

	public String  getWinmainTitleInfo()
	{
		StringBuilder s = new StringBuilder(128);

		s.append("Документ изм. цены №").
		  append(getEntity().getCode());

		if(getEntity().getChangeTime() != null)
			s.append(" от ").append(DU.datetime2str(
			  getEntity().getChangeTime()));

		return s.toString();
	}

	public boolean isDocumentFixed()
	{
		return (getEntity().getChangeTime() != null);
	}

	public String  getChangeTime()
	{
		return DU.datetime2str(
		  getEntity().getChangeTime());
	}

	public String  getPriceList()
	{
		return Prices.getPriceListFullName(
		  getEntity().getPriceList());
	}

	public boolean isContractorsAffected()
	{
		return !getEntity().getContractors().isEmpty();
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new RepriceDocModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof RepriceDocModelBean);
	}
}