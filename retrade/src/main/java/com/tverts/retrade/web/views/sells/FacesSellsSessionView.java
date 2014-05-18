package com.tverts.retrade.web.views.sells;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelRequest;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (invoices, sells) */

import com.tverts.retrade.domain.invoice.InvoiceModelBean;
import com.tverts.retrade.domain.sells.SellsSession;
import com.tverts.retrade.domain.sells.SellsSessionModelBean;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Read-only view on the Sells Session.
 *
 * @author anton.baukin@gmail.com
 */
@Component @Scope("request")
public class FacesSellsSessionView extends UnityModelView
{
	/* actions */

	public String gotoSellsInvoice()
	{
		EX.assertx(isFromInvoice());
		getModel().setActive(false);
		return "invoice";
	}

	public String gotoSellReceipt()
	{
		return "receipt";
	}

	public String gotoSessionBack()
	{
		return "session";
	}

	public String gotoSessionInvoices()
	{
		return "session-invoices";
	}


	/* public: view interface */

	public SellsSession getEntity()
	{
		return (SellsSession) super.getEntity();
	}

	public SellsSessionModelBean getModel()
	{
		return (SellsSessionModelBean) super.getModel();
	}

	public String  getWinmainTitleInfo()
	{
		return SU.cats(
		  getEntity().getUnity().getUnityType().getTitleLo(),
		  " №", getEntity().getCode(),
		  " от ", DU.datetime2str(getEntity().getTime())
		);
	}

	public String getWinmainTitleInvoices()
	{
		return "Накладные, " + getWinmainTitleInfo();
	}

	public boolean isFromInvoice()
	{
		return (findRequestedModel(InvoiceModelBean.class) != null);
	}


	/* public: ModelView (access model) interface */

	public ModelBean provideModel()
	{
		//?: {sells sessions invoices}
		if(ModelRequest.isKey("invoices"))
			return getModel().getDocsSearch();

		throw EX.state("No valid model data key requested!");
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new SellsSessionModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SellsSessionModelBean);
	}
}