package com.tverts.retrade.web.views.reprices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.NumericModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade domain (prices) */

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
public class FacesRepriceDocView extends NumericModelView
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


	/* public: FacesRepriceDocView (bean) interface */

	public RepriceDoc          getNumeric()
	{
		return (RepriceDoc)super.getNumeric();
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
		  append(getNumeric().getCode());

		if(getNumeric().getChangeTime() != null)
			s.append(" от ").append(
			  DU.datetime2str(getNumeric().getChangeTime()));

		return s.toString();
	}

	public boolean isDocumentFixed()
	{
		return (getNumeric().getChangeTime() != null);
	}

	public String  getChangeTime()
	{
		return DU.datetime2str(
		  getNumeric().getChangeTime());
	}

	public String  getPriceList()
	{
		return Prices.getPriceListFullName(
		  getNumeric().getPriceList());
	}


	/* protected: UnityModelView interface */

	protected NumericModelBean createModelInstance(Long objectKey)
	{
		RepriceDocModelBean mb = new RepriceDocModelBean();

		mb.setObjectClass(RepriceDoc.class);
		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean          isRequestModelMatch(ModelBean model)
	{
		return (model instanceof RepriceDocModelBean);
	}
}