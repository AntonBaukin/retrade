package com.tverts.retrade.web.views;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (documents + sells) */

import com.tverts.retrade.domain.doc.DocsSearchModelBean;
import com.tverts.retrade.domain.sells.Sells;


/**
 * The view of the documents table page.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesDocumentsView extends ModelView
{
	/* public: FacesDocumentsView (bean) interface */

	public DocsSearchModelBean getModel()
	{
		return (DocsSearchModelBean) super.getModel();
	}


	/* protected: ModelView interface */

	protected DocsSearchModelBean createModel()
	{
		DocsSearchModelBean mb = new DocsSearchModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: off some types by default
		mb.offDocTypes(
		  Sells.typeSellsSession(),
		  Sells.typeSellsInvoice()
		);

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof DocsSearchModelBean);
	}
}