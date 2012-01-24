package com.tverts.model;

/* Java Servlet api */

import javax.servlet.http.HttpSession;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


/**
 * COMMENT ModelAccessSession
 *
 * @author anton.baukin@gmail.com
 */
public class ModelAccessSession implements ModelAccess
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
	  ModelAccessSession.class.getName();

	public ModelPoint accessModel()
	{
		HttpSession s = session();
		ModelPoint  m = (ModelPoint)s.getAttribute(SESSION_ATTR);

		//?: {the model already exists} return it
		if(m != null) return m;

		synchronized(this)
		{
			//~: get the model again
			m = (ModelPoint)s.getAttribute(SESSION_ATTR);

			//?: {the model already exists} return it
			if(m != null) return m;

			//~: create the model bean
			m = (ModelPoint)bean(getModelBean());

			//!: save it to the session
			s.setAttribute(SESSION_ATTR, m);
		}

		return m;
	}


	/* private: strategy parameters */

	private String modelBean;
}