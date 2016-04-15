package com.tverts.faces;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.NumericModelBean;


/**
 * The view on the persistent instances
 * defined by the class and Long key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class NumericModelView extends ModelView
{
	/* public: FacesInvoicePreView (bean) interface */

	public NumericModelBean getModel()
	{
		return (NumericModelBean)super.getModel();
	}

	public NumericIdentity  getNumeric()
	{
		return (instance != null)?(instance):
		  (instance = getModel().accessNumeric());
	}

	public String           getCheckEntityRequested()
	{
		if(getNumeric() == null) throw new IllegalStateException(
		  "The entity referred by model does not exist!");

		return "";
	}


	/* protected: ModelView interface */

	protected abstract NumericModelBean
	                  createModelInstance(Long objectKey);

	protected NumericModelBean
	                  createModel()
	{
		Long             key   = obtainEntityKeyFromRequestStrict();
		NumericModelBean model = createModelInstance(key);

		if(model.getPrimaryKey() == null)
			model.setPrimaryKey(key);

		if(model.getDomain() == null)
			model.setDomain(getDomainKey());

		return model;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof NumericModelBean);
	}


	/* private: the instance (cached) */

	private NumericIdentity instance;
}