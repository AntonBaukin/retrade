package com.tverts.faces;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: endure */

import com.tverts.endure.United;

/* com.tverts: model */

import com.tverts.model.UnityModelBean;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


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
		  (entity = getModel().loadEntity());
	}

	public String            getCheckEntityRequested()
	{
		if(getEntity() == null) throw new IllegalStateException(
		  "The entity referred by model does not exist!");
		return "";
	}


	/* protected: ModelView interface */

	protected UnityModelBean createModel()
	{
		UnityModelBean model = createModelInstance();
		Long           key   = obtainPrimaryKeyFromRequest();

		if(key == null) throw new IllegalStateException(
		  "Can't obtain primary key of entity to " +
		  "preview from the HTTP request!");

		model.setPrimaryKey(key);
		return model;
	}

	protected UnityModelBean createModelInstance()
	{
		return new UnityModelBean();
	}


	/* protected: implementation details */

	protected Long           obtainPrimaryKeyFromRequest()
	{
		String param = s2s(request().getParameter(ENTITY_PARAM));
		return (param == null)?(null):(Long.parseLong(param));
	}


	/* private: the entity (cached) */

	private United entity;
}