package com.tverts.faces;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelAccessPoint;
import com.tverts.model.ModelBean;
import com.tverts.model.ModelPoint;
import com.tverts.model.NoModelException;

/* com.tverts: genesis */

import com.tverts.endure.core.GenTestDomain; //!: TEMP

/* com.tverts: support */

import static com.tverts.support.SU.cat;
import static com.tverts.support.SU.s2a;
import static com.tverts.support.SU.s2s;


/**
 * An implementation base for JavaServer Faces views.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelView extends ViewWithModes
{
	/* constants */

	/**
	 * Model key parameter in the HTTP request
	 */
	public static final String MODEL_PARAM      = "model";

	public static final String MODEL_REQ_PARAM  = "model-request";

	/**
	 * Parameter used to refer database entities
	 * by their primary key.
	 */
	public static final String ENTITY_PARAM     = "entity";


	/* public: ModelView (access model) interface */

	/**
	 * Finds the domain the current request is for.
	 *
	 * TODO return non-test domain in model views!
	 */
	public Long        getDomainKey()
	{
		return GenTestDomain.testDomain().getPrimaryKey();
	}

	public String      getId()
	{
		return (this.id != null)?(this.id):
		  (this.id = obtainViewId());
	}

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
		if(model == null) throw new IllegalStateException(String.format(
		  "User data Model bean instance can't be created for the view " +
		  "accessed by the URL [%s]!", request().getRequestURL().toString()));

		return model;
	}

	public String      getModelKey()
	{
		return getModel().getModelKey();
	}

	/**
	 * Returns all the models got by the keys list
	 * given in the HTTP request. The current model
	 * may be not in this list.
	 */
	public ModelBean[] getRequestedModels()
	{
		return (models != null)?(models):
		  (models = obtainRequestedModels());
	}

	public ModelBean[] getRequestedActiveModels()
	{
		ModelBean[]          rm = getRequestedModels();
		if(rm == null) return null;

		ArrayList<ModelBean> am = new ArrayList<ModelBean>(Arrays.asList(rm));

		for(Iterator<ModelBean> i = am.iterator();(i.hasNext());)
			if(!i.next().isActive())
				i.remove();

		return am.toArray(new ModelBean[am.size()]);
	}

	/**
	 * Combines all the model keys requested with
	 * the key of the current model. Allows to remember
	 * the state in complex multi-view conversations.
	 *
	 * Inactive models are excluded.
	 */
	public String      getModelKeys()
	{
		ModelBean[] am = getRequestedActiveModels();
		Set<String> ks = new LinkedHashSet<String>(am.length);

		for(ModelBean m : am) if(m.isActive())
			ks.add(m.getModelKey());

		if(getModel().isActive())
			ks.add(getModel().getModelKey());

		return cat(null, ",", ks).toString();
	}

	@SuppressWarnings("unchecked")
	public <B extends ModelBean> B
	                   findRequestedModel(Class<B> beanClass)
	{
		for(ModelBean m : getRequestedModels())
			if(beanClass.equals(m.getClass()))
				return (B)m;
		return null;
	}

	@SuppressWarnings("unchecked")
	public <B extends ModelBean> B
	                   findRequestedModelAssignable(Class<B> beanClass)
	{
		for(ModelBean m : getRequestedModels())
			if(beanClass.isAssignableFrom(m.getClass()))
				return (B)m;
		return null;
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
		if(!isModelRequested()) throw new IllegalStateException(
		  "Requested user interface model is gone or not actual!");
		return "";
	}


	/* public: ModelView (parameter names) interface */

	public String getModelParam()
	{
		return MODEL_PARAM;
	}

	public String getModelRequestParam()
	{
		return MODEL_REQ_PARAM;
	}

	public String getEntityParam()
	{
		return ENTITY_PARAM;
	}


	/* protected: view support interface */

	protected abstract ModelBean createModel();

	protected String             obtainViewId()
	{
		//~: take value from the request
		String id = obtainRequestedViewId();
		if(id != null) return id;

		//~: ask for effective id
		id = bean(RootView.class).getEffectiveViewId();
		if(id == null) throw new IllegalStateException(
		  "No effective Faces View ID was generated!");

		return id;
	}



	protected ModelBean          obtainModel()
	{
		for(ModelBean model : getRequestedModels())
			if(isRequestModelMatch(model))
				return model;
		return null;
	}

	/**
	 * In the most cases there is only one view (and model)
	 * per a request. When there are redirects, or in more
	 * complex pages, the model referred in the request differ
	 * from the model of secondary view. This check may help
	 * in the case of redirects.
	 */
	protected boolean            isRequestModelMatch(ModelBean model)
	{
		return true;
	}

	protected ModelBean[]        obtainRequestedModels()
	{
		Set<String>     keys = obtainRequestedModelKeys();
		List<ModelBean> res  = new ArrayList<ModelBean>(keys.size());

		for(String key : keys) if((key = s2s(key)) != null)
		{
			ModelBean mb = modelPoint().readBean(key);
			if(mb != null) res.add(mb);
		}

		return res.toArray(new ModelBean[res.size()]);
	}

	public Set<String>           obtainRequestedModelKeys()
	{
		//~: get the keys parameter
		String[]        prms = request().getParameterValues(getModelParam());
		if(prms == null) prms = new String[0];

		//~: split the keys
		Set<String>     keys = new LinkedHashSet<String>(prms.length);
		for(String s : prms) if((s = s2s(s)) != null)
			keys.addAll(Arrays.asList(s2a(s)));

		return keys;
	}

	protected Long               obtainEntityKeyFromRequest()
	{
		String param = s2s(request().getParameter(getEntityParam()));
		return (param == null)?(null):(Long.parseLong(param));
	}

	protected Long               obtainEntityKeyFromRequestStrict()
	{
		Long res = obtainEntityKeyFromRequest();

		if(res == null) throw new IllegalStateException(
		  "Can't obtain primary key of entity for the model " +
		  "bean instance from the HTTP request!");

		return res;
	}


	protected ModelPoint         modelPoint()
	{
		return ModelAccessPoint.model();
	}


	/* private: the view state */

	private String      id;
	private ModelBean   model;
	private ModelBean[] models;
}