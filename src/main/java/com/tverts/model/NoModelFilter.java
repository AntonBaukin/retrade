package com.tverts.model;

/* Java Servlet api */

import javax.servlet.http.Cookie;

/* tverts.com: servlet */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterTask;

/* tverts.com: faces */

import com.tverts.faces.Functions;
import com.tverts.faces.ModelView;

/* tverts.com: support */

import com.tverts.support.EX;


/**
 * Catches {@link NoModelException} and redirects the request
 * to the actual model if such is defined in the exception.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class NoModelFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{}

	public void closeFilter(FilterTask task)
	{
		NoModelException nomoe = EX.search(
		  task.getError(), NoModelException.class);

		//?: {got no model exception} do redirect
		if(nomoe != null) try
		{
			redirectOnNoModel(task, nomoe.getModel());
		}
		catch(Exception e)
		{
			task.setError(e);
		}
	}


	/* protected: no model redirecting */

	protected void   redirectOnNoModel(FilterTask task, ModelBean model)
	  throws Exception
	{
		Throwable error = task.getError();

		//?: {can't send redirect}
		if((model == null) || task.getResponse().isCommitted())
			return;

		//~: do the redirect
		setResponseRedirect(task, model);

		//!: clear the error
		if(task.getError() == error)
			task.setError(null);
	}

	protected void   setResponseRedirect(FilterTask task, ModelBean model)
	  throws Exception
	{
		//HINT: we add session cookie manually to always be sure...
		Cookie cookie = new Cookie("JSESSIONID",
		  task.getRequest().getSession().getId());

		cookie.setSecure(task.getRequest().isSecure());
		cookie.setPath(task.getRequest().getContextPath());
		task.getResponse().addCookie(cookie);


		//!: do send the redirect
		task.getResponse().sendRedirect(
		  task.getResponse().encodeURL(
		    createRedirectURL(task, model)));
	}

	protected String createRedirectURL(FilterTask task, ModelBean model)
	{
		String        cp  = task.getRequest().getContextPath();
		String        uri = task.getRequest().getRequestURI();
		String        url = Functions.absoluteURL(uri.substring(cp.length()));
		String        qs  = task.getRequest().getQueryString();
		String        mp  = ModelView.MODEL_PARAM + '=';
		StringBuilder res;

		//~: add request URI as it is
		if(qs == null) qs = "";
		res = new StringBuilder(url.length() + qs.length() + 64);
		res.append(url).append('?');

		//~: detect the model reference in the request
		int           mi  = qs.indexOf(mp);

		//?: {has no model parameter in query string} add it
		if(mi == -1)
		{
			res.append(mp).append(model.getModelKey());
			if(qs.length() != 0)
				res.append('&').append(qs);
		}
		//!: replace the model parameter in the query string
		else
		{
			int        ai  = qs.indexOf('&', mi);
			if(ai == -1) ai = qs.length();

			res.append(qs.substring(0, mi + mp.length()));
			res.append(model.getModelKey());
			res.append(qs.substring(ai));
		}

		return res.toString();
	}
}