package com.tverts.model;

/* Java Servlet api */

import javax.servlet.http.Cookie;

/* tverts.com: servlet */

import static com.tverts.servlet.RequestPoint.request;
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
 * WARNING! As HTTP specifies, only GET requests may be
 *  redirected via 302 response! 307 response is not
 *  supported transparently for the user.
 *
 *  This implementation raises exception on POST redirect.
 *  Do not use redirect in such manner!
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
			redirectOnNoModel(task, nomoe);
		}
		catch(Exception e)
		{
			task.setError(e);
		}
	}


	/* protected: no model redirecting */

	protected void   redirectOnNoModel(FilterTask task, NoModelException nomoe)
	  throws Exception
	{
		Throwable error = task.getError();

		//?: {can't send redirect}
		if((nomoe.getModel() == null) || task.getResponse().isCommitted())
			return;

		//?: {not a GET request} 302 redirect is forbidden!
		if(!"GET".equalsIgnoreCase(request(0).getMethod()))
			return;

		//~: do the redirect
		setResponseRedirect(task, nomoe);

		//!: clear the error
		if(task.getError() == error)
			task.setError(null);
	}

	protected void   setResponseRedirect(FilterTask task, NoModelException nomoe)
	  throws Exception
	{
		//HINT: we add session cookie manually to always be sure...
		Cookie cookie = new Cookie("JSESSIONID",
		  request(0).getSession().getId());

		cookie.setSecure(request(0).isSecure());
		cookie.setPath(request(0).getContextPath());
		task.getResponse().addCookie(cookie);


		//!: do send the redirect
		task.getResponse().sendRedirect(
		  task.getResponse().encodeURL(
		    createRedirectURL(task, nomoe)));
	}

	protected String createRedirectURL(FilterTask task, NoModelException nomoe)
	{
		String        cp  = request(0).getContextPath();
		String        uri = request(0).getRequestURI();
		String        url = Functions.absoluteURL(uri.substring(cp.length()));
		String        qs  = request(0).getQueryString();
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
			res.append(mp).append(nomoe.getModelKeys());
			if(qs.length() != 0)
				res.append('&').append(qs);
		}
		//!: replace the model parameter in the query string
		else
		{
			int        ai  = qs.indexOf('&', mi);
			if(ai == -1) ai = qs.length();

			res.append(qs.substring(0, mi + mp.length()));
			res.append(nomoe.getModelKeys());
			res.append(qs.substring(ai));
		}

		return res.toString();
	}
}