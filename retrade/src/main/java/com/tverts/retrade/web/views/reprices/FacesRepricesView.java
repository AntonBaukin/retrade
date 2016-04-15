package com.tverts.retrade.web.views.reprices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.RepriceDocsModelBean;


/**
 * The view of the Reprice Documents table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesRepricesView extends ModelView
{
	/* public: FacesRepricesView (bean) interface */

	public RepriceDocsModelBean getModel()
	{
		return (RepriceDocsModelBean)super.getModel();
	}


	/* protected: ModelView interface */

	protected RepriceDocsModelBean createModel()
	{
		RepriceDocsModelBean mb = new RepriceDocsModelBean();

		mb.setDomain(getDomainKey());
		mb.setMaxDate(new java.util.Date());
		return mb;
	}

	protected boolean              isRequestModelMatch(ModelBean model)
	{
		return (model instanceof RepriceDocsModelBean);
	}
}