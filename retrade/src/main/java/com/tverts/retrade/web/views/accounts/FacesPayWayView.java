package com.tverts.retrade.web.views.accounts;

/* standard Java classes */

import java.math.BigDecimal;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (accounts + firms) */

import com.tverts.retrade.domain.account.GetAccount;
import com.tverts.retrade.domain.account.PayBank;
import com.tverts.retrade.domain.account.PayWay;
import com.tverts.retrade.domain.account.PayWayModelBean;
import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: support */

import com.tverts.support.SU;
import com.tverts.support.fmt.FmtCtx;
import com.tverts.support.fmt.FmtPoint;
import com.tverts.support.fmt.FmtUni;


/**
 * The main info view of the {@link PayWay}.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesPayWayView") @Scope("request")
public class FacesPayWayView extends UnityModelView
{
	/* public: view interface */

	public PayWayModelBean getModel()
	{
		return (PayWayModelBean) super.getModel();
	}

	public PayWay getEntity()
	{
		return (PayWay) super.getEntity();
	}

	public BigDecimal getIncome()
	{
		return loadPayWayBalance()[0];
	}

	public BigDecimal getExpense()
	{
		return loadPayWayBalance()[1];
	}

	public BigDecimal getBalance()
	{
		return loadPayWayBalance()[2];
	}

	public PayBank getPayBank()
	{
		PayWay w = getEntity();
		return !(w instanceof PayBank)?(null):((PayBank) w);
	}

	private Contractor contractor;
	private boolean    searchedCon;

	public Contractor getContractor()
	{
		if(searchedCon) return contractor;
		searchedCon = true;
		return contractor = bean(GetAccount.class).
		  getPayWayContractor(getModel().getPrimaryKey());
	}

	public String getWinmainTitleInfo()
	{
		FmtCtx ctx = new FmtCtx().obj(getEntity()).
		  put(Contractor.class, getContractor()).
		  set(Contractor.class).
		  set(FmtUni.TYPE).
		  set(FmtUni.CODE);

		return FmtPoint.format(ctx);
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new PayWayModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PayWayModelBean);
	}


	/* protected: support interface */

	private BigDecimal[] balance;

	protected BigDecimal[] loadPayWayBalance()
	{
		if(balance != null)
			return balance;

		return balance = bean(GetAccount.class).
		  getPayWayBalance(getModel().getPrimaryKey());
	}
}