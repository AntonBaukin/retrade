package com.tverts.retrade.web.views.reprices;

/* Java */

import java.math.BigDecimal;
import java.util.Map;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (firms + prices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.prices.Prices;
import com.tverts.retrade.domain.prices.RepriceDoc;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.SU;


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

		s.append("Документ изм. цен №").
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
		return (getEntity().getChangeTime() == null)?(null):
		  DU.datetime2str(getEntity().getChangeTime());
	}

	public String  getPriceList()
	{
		return (getEntity().getPriceList() == null)?(null):
		  Prices.getPriceListFullName(getEntity().getPriceList());
	}

	public boolean isHasChanges()
	{
		return !getEntity().getChanges().isEmpty();
	}

	public boolean isHasContractors()
	{
		return !getEntity().getContractors().isEmpty();
	}

	public boolean isOneContractor()
	{
		return (getEntity().getContractors().size() == 1);
	}

	public Contractor getFirstContractor()
	{
		return (getEntity().getContractors().size() != 1)?(null):
		  getEntity().getContractors().iterator().next();
	}

	public boolean isHasGroups()
	{
		return (getEntity().getOx().getGroupChanges() != null);
	}

	public String getGroupsData()
	{
		Map<String, BigDecimal> gs = getEntity().getOx().getGroupChanges();
		StringBuilder            s = new StringBuilder(128);

		if(gs != null) for(String g : gs.keySet())
		{
			if(s.length() != 0) s.append(", ");
			s.append("{key: '").append(SU.jss(g)).
			  append("', value: '").append(gs.get(g)).append("'}");
		}

		return s.toString();
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