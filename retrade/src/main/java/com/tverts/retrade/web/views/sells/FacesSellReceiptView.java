package com.tverts.retrade.web.views.sells;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.NumericModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.SellReceipt;
import com.tverts.retrade.domain.sells.SellReceiptModelBean;
import com.tverts.retrade.domain.sells.SellsSession;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The read-only view of a Sells Receipt.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSellReceiptView extends NumericModelView
{
	/* actions */

	public String gotoSellsSession()
	{
		getModel().setActive(false);
		return "session";
	}


	/* view interface */

	public SellReceiptModelBean getModel()
	{
		return (SellReceiptModelBean) super.getModel();
	}

	public SellReceipt  getSellReceipt()
	{
		return (SellReceipt) this.getNumeric();
	}

	public SellsSession getSellsSession()
	{
		return getSellReceipt().getSession();
	}

	public void         forceSecureSellsSession(String key)
	{
		if(!SecPoint.isSecure(getSellsSession().getPrimaryKey(), key))
			throw EX.forbid();
	}

	public String       getWinmainTitleInfo()
	{
		return SU.cats(
		  "Чек №", getSellReceipt().getCode(),
		  " кассы №", getSellsSession().getSellsDesk().getCode(),
		  " от ", DU.datetime2str(getSellReceipt().getTime())
		);
	}


	/* protected: NumericModelView interface */

	protected NumericModelBean createModelInstance(Long objectKey)
	{
		//~: load the sells receipt
		SellReceipt sr = bean(GetSells.class).getReceipt(objectKey);
		EX.assertn(sr, "No Sell Receipt with key [", objectKey, "] found");

		//sec: check the domain
		if(!getDomainKey().equals(sr.getSession().getDomain().getPrimaryKey()))
			throw EX.forbid();

		return new SellReceiptModelBean(sr);
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SellReceiptModelBean);
	}
}