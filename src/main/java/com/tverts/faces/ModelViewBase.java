package com.tverts.faces;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelsAccessPoint;
import com.tverts.model.ModelsStore;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * An implementation base for JavaServer Faces views
 * supporting Models. This intermediate bean does
 * not require to possess own model.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelViewBase extends ViewWithModes
{
	/* Constants */

	/**
	 * HTTP request parameter with the model key.
	 */
	public static final String MODEL_PARAM      = "model";

	/**
	 * HTTP request parameter with the name of
	 * Spring Bean that provides the data.
	 */
	public static final String MODEL_PROVIDER   = "model-provider";

	public static final String MODEL_REQ_PARAM  = "model-request";


	/* public: ModelView (access model) interface */

	/**
	 * Finds the Domain the current request is for.
	 */
	public Long        getDomainKey()
	{
		return SecPoint.domain();
	}

	public Domain      loadDomain()
	{
		Long   k = getDomainKey();
		Domain d = (k == null)?(null):
		  bean(GetDomain.class).getDomain(k);

		return EX.assertn(d, "Can't obtain Model View working Domain!");
	}

	/**
	 * Returns all the models got by the keys list
	 * given in the HTTP request.
	 */
	public ModelBean[] getRequestedModels()
	{
		return (models != null)?(models):
		  (models = obtainRequestedModels());
	}

	public ModelBean[] getRequestedActiveModels()
	{
		//~: get all the requested models
		ModelBean[] rm = getRequestedModels();

		//?: {all the models are active}
		int ac = 0; for(ModelBean m : rm)
			if(m.isActive()) ac++;
		if(ac == rm.length)
			return rm;

		//~: select the active models
		ModelBean[] rs = new ModelBean[ac];
		for(int r = 0, i = 0;(i < rm.length);i++)
			if(rm[i].isActive())
				rs[r++] = rm[i];
		return rs;
	}

	/**
	 * Combines all the model keys requested with
	 * the key of the current model. Allows to remember
	 * the state in complex multi-view conversations.
	 *
	 * Inactive models are excluded.
	 * The result is always not null.
	 */
	public String      getModelKeys()
	{
		ModelBean[] am = getRequestedActiveModels();
		Set<String> ks = new LinkedHashSet<String>(am.length);

		for(ModelBean m : am) if(m.isActive())
			ks.add(m.getModelKey());

		return SU.scats(",", ks);
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


	/* public: ModelView (parameter names) interface */

	public String getModelParam()
	{
		return MODEL_PARAM;
	}

	public String getModelProviderParam()
	{
		return MODEL_PROVIDER;
	}

	public String getModelRequestParam()
	{
		return MODEL_REQ_PARAM;
	}


	/* protected: view support interface */

	protected ModelBean[] obtainRequestedModels()
	{
		Set<String>     keys = obtainRequestedModelKeys();
		List<ModelBean> res  = new ArrayList<ModelBean>(keys.size());

		for(String key : keys) if((key = SU.s2s(key)) != null)
		{
			ModelBean mb = modelsStore().read(key);
			if(mb != null) res.add(mb);
		}

		return res.toArray(new ModelBean[res.size()]);
	}

	public Set<String>    obtainRequestedModelKeys()
	{
		//~: get the keys parameter
		String[]        prms = request().getParameterValues(getModelParam());
		if(prms == null) prms = new String[0];

		//~: split the keys
		Set<String>     keys = new LinkedHashSet<String>(prms.length);
		for(String s : prms) if((s = SU.s2s(s)) != null)
			keys.addAll(Arrays.asList(SU.s2a(s)));

		return keys;
	}

	protected Long        obtainEntityKeyFromRequest()
	{
		String param = SU.s2s(request().getParameter(getEntityParam()));
		return (param == null)?(null):(Long.parseLong(param));
	}

	protected Long        obtainEntityKeyFromRequestStrict()
	{
		return EX.assertn(
		  obtainEntityKeyFromRequest(),

		  "Can't obtain primary key of entity for the model ",
		  "bean instance from the HTTP request!"
		);
	}

	protected ModelsStore modelsStore()
	{
		return ModelsAccessPoint.modelsStore();
	}


	/* private: the view state */

	private ModelBean[] models;
}