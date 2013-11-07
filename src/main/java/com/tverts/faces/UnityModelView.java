package com.tverts.faces;

/* com.tverts: endure */

import com.tverts.endure.United;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * The view on the entity (unity) model.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityModelView extends ModelView
{
	/* public: FacesInvoicePreView (bean) interface */

	public UnityModelBean getModel()
	{
		return (UnityModelBean)super.getModel();
	}

	public United         getEntity()
	{
		return (entity != null)?(entity):
		  (entity = getModel().accessEntity());
	}

	public String         getCheckEntityRequested()
	{
		if(getEntity() == null) throw EX.state(
		  "The entity referred by model does not exist!");

		return "";
	}


	/* public: security issues */

	public boolean isSecureModelEntity(String key)
	{
		return SecPoint.isSecure(getModel().getPrimaryKey(), key);
	}

	public void    forceSecureModelEntity(String key)
	{
		if(!SecPoint.isSecure(getModel().getPrimaryKey(), key))
			throw EX.forbid();
	}


	/* protected: ModelView interface */

	protected abstract UnityModelBean createModelInstance();

	protected UnityModelBean createModel()
	{
		UnityModelBean model = createModelInstance();
		Long           key   = obtainEntityKeyFromRequest();

		if(key == null) throw EX.state(
		  "Can't obtain primary key of entity to ",
		  "preview from the HTTP request!");

		//~: primary key
		model.setPrimaryKey(key);

		//~: domain
		if(model.getDomain() == null)
			model.setDomain(getDomainKey());

		return model;
	}


	/* private: the entity (cached) */

	private United entity;
}