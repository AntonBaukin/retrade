package com.tverts.faces;

/* standard Java classes */

import java.util.Set;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelProvider;
import com.tverts.model.NoModelException;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Extends Face supporting Models: this requires
 * to have own Model instance.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelView
       extends        ModelViewBase
       implements     ModelProvider
{
	/* public: ModelProvider interface */

	public ModelBean   provideModel()
	{
		return getModel();
	}


	/* public: ModelView (access model) interface */

	public ModelBean   getModel()
	{
		//?: {got the model reference}
		if(model != null) return model;

		//~: obtain the model referred by the HTTP parameter
		model = obtainModel();
		if(model != null) return model;

		//~: create the model
		model = createModel();

		//?: {had created it} save into the point
		if(model != null)
			modelPoint().addBean(model);

		//?: {the model is not defined}
		return EX.assertn(model, "User data Model bean instance can't be created ",
		  "for the view accessed by the URL [", request().getRequestURL(), "]!"
		);
	}

	public Domain      loadModelDomain()
	{
		Long k = getModel().getDomain();

		return EX.assertn(
		  (k == null)?(null):(bean(GetDomain.class).getDomain(k)),
		  "Can't obtain Model Domain!"
		);
	}

	public String      getModelKey()
	{
		return getModel().getModelKey();
	}

	/**
	 * Extends super implementation appending the key
	 * of the model owned (if it is active and the key
	 * is not written yet).
	 */
	public String      getModelKeys()
	{
		String    r = super.getModelKeys();
		ModelBean m = getModel();
		String    k = m.getModelKey();

		//?: {owned model is inactive | has the key}
		if((k == null) || r.contains(k) || !m.isActive())
			return r;

		return SU.scat(",", r, k);
	}


	/* public: ModelView (checks) interface */

	/**
	 * Tells that the model requested in the same model
	 * (with the same key) that is currently set for the view.
	 */
	public boolean     isModelRequested()
	{
		Set<String> keys = obtainRequestedModelKeys();
		return keys.contains(getModel().getModelKey());
	}

	public String      getCheckModelRequestedWithRedirect()
	{
		if(!isModelRequested())
			throw new NoModelException(getModel()).
			  setModelKeys(getModelKeys());

		return "";
	}

	public String      getCheckModelRequested()
	{
		EX.assertx( isModelRequested(),
		  "Requested user interface model is gone or not actual!"
		);

		return "";
	}


	/* protected: view (model) interface */

	protected abstract ModelBean createModel();

	/**
	 * In many cases there is only one view (and model)
	 * per a request. When there are redirects, or in
	 * complex interactions (such as wizards) the model
	 * referred in the request differ from the model of
	 * previous views.
	 *
	 * This check is to help in the case of redirects.
	 * Note that the check is done in the reverse order
	 * of the models provided, and you may stack several
	 * model of the same type: the latest would be taken.
	 */
	protected abstract boolean   isRequestModelMatch(ModelBean model);

	protected ModelBean          obtainModel()
	{
		ModelBean[] mbs = getRequestedModels();

		for(int i = mbs.length - 1;(i >= 0);i--)
			if(isRequestModelMatch(mbs[i]))
				return mbs[i];

		return null;
	}


	/* private: the view state */

	private ModelBean   model;
	private ModelBean[] models;
}