package com.tverts.retrade.web.views.firms;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.List;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.endure.aggr.volume.GetAggrVolume;
import com.tverts.faces.UnityModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelRequest;
import com.tverts.model.SimpleModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: endure (aggregated values and calculations) */

import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;
import com.tverts.endure.aggr.volume.DatePeriodVolumeCalcItem;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.firm.ContractorModelBean;

/* com.tverts: retrade data */

import com.tverts.retrade.web.data.other.AggrVolumeData;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * The main info view of the {@link Contractor}.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesContractorView") @Scope("request")
public class FacesContractorView extends UnityModelView
{
	/* actions */

	public String gotoInvoices()
	{
		return "invoices";
	}

	public String gotoInfoMain()
	{
		return "info-main";
	}


	/* public: FacesContractorView (bean) interface */

	public ContractorModelBean getModel()
	{
		return (ContractorModelBean) super.getModel();
	}

	public Contractor          getEntity()
	{
		return (Contractor) super.getEntity();
	}


	/* public: view [info] interface */

	public String getWinmainTitleInfo()
	{
		return formatTitle("Контрагент",
		  getEntity().getCode(), getEntity().getName());
	}

	public String getWinmainTitleInvoices()
	{
		return formatTitle("Накладные к-та",
		  getEntity().getCode(), getEntity().getName());
	}

	public String getContractorType()
	{
		if(getEntity().getFirm() != null)
			return "Организация";
		return null;
	}

	public BigDecimal getBalance()
	{
		AggrValue  v = getFirmDebtAggrValue();
		BigDecimal e = v.getAggrNegative();
		BigDecimal i = v.getAggrPositive();

		if(e == null) e = BigDecimal.ZERO;
		if(i == null) i = BigDecimal.ZERO;

		return i.subtract(e).setScale(2, BigDecimal.ROUND_HALF_UP);
	}


	/* public: ModelView (access model) interface */

	public ModelBean provideModel()
	{
		//~: {monthly debts requested}
		if(ModelRequest.isKey("debts-monthly"))
			return provideDebtsModel();

		//~: {weekly debts requested}
		if(ModelRequest.isKey("debts-weekly"))
			return provideDebtsModel();

		//?: {contractor invoices}
		if(ModelRequest.isKey("invoices"))
			return getModel().getDocsSearch();

		throw EX.state("No valid model data key requested!");
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		ContractorModelBean mb = new ContractorModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ContractorModelBean);
	}


	/* protected: support routines */

	protected AggrValue getFirmDebtAggrValue()
	{
		AggrValue av = bean(GetAggrValue.class).getAggrValue(
		  getEntity().getPrimaryKey(), Contractors.AGGRVAL_CONTRACTOR_DEBT, null);

		if(av == null) throw EX.state(
		  "No Firm Debt Aggegated Value found for Contractor [",
		  getEntity().getPrimaryKey(), "]!"
		);

		return av;
	}

	protected ModelBean provideDebtsModel()
	{
		SimpleModelBean mb = new SimpleModelBean();

		//~: domain
		mb.setDomain(getModel().domain());

		//~: debt aggregated value
		List<DatePeriodVolumeCalcItem> items = null;

		//~: select calculation items
		if(ModelRequest.isKey("debts-monthly"))
			items = bean(GetAggrVolume.class).
			  getMonthVolumeCalcItems(getFirmDebtAggrValue());

		if(ModelRequest.isKey("debts-weekly"))
			items = bean(GetAggrVolume.class).
			  getWeekVolumeCalcItems(getFirmDebtAggrValue());

		//~: assign the data model
		if(items == null) throw EX.state();
		mb.setData(new AggrVolumeData(mb, items));

		return mb;
	}
}