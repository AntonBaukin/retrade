package com.tverts.retrade.web.views.payments;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.PaymentsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of the Payments table having various projections.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesPaymentsView extends ModelView
{
	/* public: view interface */

	public PaymentsModelBean getModel()
	{
		return (PaymentsModelBean) super.getModel();
	}


	/* protected: ModelView interface */

	protected PaymentsModelBean createModel()
	{
		PaymentsModelBean mb = new PaymentsModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: default projection
		mb.setProjection("gen");

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PaymentsModelBean);
	}
}