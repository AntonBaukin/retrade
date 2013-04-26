package com.tverts.model;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


/**
 * Uses the web session ID as the key to in-memory
 * storage of the models.
 *
 * @author anton.baukin@gmail.com
 */
public class ModelAccessMemory implements ModelAccess
{
	/* public: ModelAccessSession (bean) interface */

	public String getModelBean()
	{
		return modelBean;
	}

	public void   setModelBean(String modelBean)
	{
		this.modelBean = modelBean;
	}


	/* public: ModelAccess interface */

	protected static final String SESSION_ATTR =
	  ModelAccessMemory.class.getName();

	public ModelPoint    accessModel()
	{
		String     k = session().getId();
		ModelPoint m;

		synchronized(this)
		{
			//~: lookup the model
			m = points.get(k);

			//?: {the model already exists} return it
			if(m != null) return m;
		}

		//~: create the model bean from prototype
		m = (ModelPoint) bean(getModelBean());

		//~: save it to the storage
		synchronized(this)
		{
			//~: lookup the model again
			ModelPoint m2 = points.get(k);

			if(m2 != null)
				m = m2;
			else
				points.put(k, m);
		}

		return m;
	}


	/* private: strategy parameters */

	private String modelBean;


	/* protected: model points storage */

	protected Map<String, ModelPoint> points =
	  new HashMap<String, ModelPoint>(17);
}