package com.tverts.faces;

/* com.tverts: endure */

import com.tverts.endure.United;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;


/**
 * The view on the entity (unity) model.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityModelView extends ModelView
{
	/* public: FacesInvoicePreView (bean) interface */

	public UnityModelBean    getModel()
	{
		return (UnityModelBean)super.getModel();
	}

	public United            getEntity()
	{
		return (entity != null)?(entity):
		  (entity = getModel().accessEntity());
	}

	public String            getCheckEntityRequested()
	{
		if(getEntity() == null) throw new IllegalStateException(
		  "The entity referred by model does not exist!");

		return "";
	}


	/* protected: ModelView interface */

	protected abstract UnityModelBean
	                  createModelInstance();

	protected UnityModelBean
	                  createModel()
	{
		UnityModelBean model = createModelInstance();
		Long           key   = obtainEntityKeyFromRequest();

		if(key == null) throw new IllegalStateException(
		  "Can't obtain primary key of entity to " +
		  "preview from the HTTP request!");

		model.setPrimaryKey(key);
		return model;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof UnityModelBean);
	}


	/* private: the entity (cached) */

	private United entity;
}