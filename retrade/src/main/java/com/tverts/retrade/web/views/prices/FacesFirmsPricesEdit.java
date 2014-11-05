package com.tverts.retrade.web.views.prices;

/* Java */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.endure.core.GetUnity;
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

/* com.tverts: api */

import com.tverts.api.retrade.prices.PriceList;

/* com.tverts: retrade domain (firms + goods + prices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.FirmsPricesEditModelBean;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.PriceListsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * A controller of form to edit the Price Lists
 * associated with one or more selected firms.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesFirmsPricesEdit extends ModelView
{
	/* actions */

	public String doSearchGoods()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("searchGoods")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}

	public String doRemoveContractors()
	{
		String[] ids = request().getParameterValues("contractors");

		if(ids != null) for(String id : ids)
			getModel().getContractors().remove(Long.parseLong(id));

		return null;
	}

	@SuppressWarnings("unchecked")
	public String doAddContractors()
	{
		//~: find the contractors of the selection set
		List<Long> ids = bean(GetUnity.class).selectIds(
		  Contractor.class, Contractors.TYPE_CONTRACTOR,
		  request().getParameter("selset")
		);

		//~: add the items
		Set<Long> got = new HashSet<>(getModel().getContractors());
		for(Long id : ids) if(!got.contains(id))
		{
			got.add(id);
			getModel().getContractors().add(id);
		}

		return null;
	}

	public String doRemovePriceLists()
	{
		return null;
	}

	public String doAddPriceLists()
	{
		return null;
	}


	/* View */

	public FirmsPricesEditModelBean getModel()
	{
		return (FirmsPricesEditModelBean) super.getModel();
	}


	/* protected: ModelView interface */

	protected FirmsPricesEditModelBean createModel()
	{
		FirmsPricesEditModelBean mb = new FirmsPricesEditModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: lookup initial firms
		String[] cs = SU.s2a(request().getParameter("contractors"));
		if(cs.length != 0) for(String pk : cs)
		{
			Contractor c = EX.assertn(bean(GetContractor.class).
			  getContractor(Long.parseLong(pk)),
			  "Contractor [", pk, "] is not found!"
			);

			//sec: contractor domain
			EX.assertx(c.getDomain().getPrimaryKey().equals(getDomainKey()));

			//~: add it
			mb.getContractors().add(c.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof FirmsPricesEditModelBean);
	}
}