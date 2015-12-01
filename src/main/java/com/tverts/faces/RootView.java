package com.tverts.faces;

/* Java */

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;
import static com.tverts.servlet.RequestPoint.response;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * View of the root page template.
 *
 * @author anton.baukin@gmail.com
 */
@Component("rootView") @Scope("request")
public class RootView extends ViewWithModes
{
	/* constants */

	public static final String PARAM_DOMAIN   =
	  "extjs_domain";

	/**
	 * Position within the desktop layout panels. The default
	 * value is {@code null} string, and the real place is defined
	 * by the implementation (the desktop center).
	 */
	public static final String PARAM_POSITION =
	  "extjs_desktop_position";


	/* global actions */

	public String doLogoff()
	{
		SecPoint.closeSecSession();
		return null;
	}


	/* public: access shared view state */

	public String    getEffectiveViewId()
	{
		if(viewId == null)
		{
			viewId = obtainRequestedViewId();
			if(viewId == null) viewId = getNewViewId();
		}

		return viewId;
	}

	public String    getExtjsDomainParam()
	{
		return PARAM_DOMAIN;
	}

	public String    getExtjsPosition()
	{
		return obtainExtjsPositionFromRequest();
	}

	public String    getExtjsPositionParam()
	{
		return PARAM_POSITION;
	}

	public String    getExtjsDomain()
	{
		return (extjsDomain != null)?(extjsDomain):
		  (extjsDomain = obtainExtjsDomain());
	}

	public String    getNewViewId()
	{
		return String.format("view_%x", VIEWID.incrementAndGet());
	}

	/**
	 * Returns web context related URL
	 * (starts with '/') to the page
	 * current request was issued for.
	 * It is already URL-encoded.
	 */
	public String    getRequestURI()
	{
		return response().encodeURL(request(0).getRequestURI());
	}

	public Long      getEntityKey()
	{
		String p = SU.s2s(request().getParameter(getEntityParam()));
		return (p == null)?(null):Long.parseLong(p);
	}

	private static final List<String> STD_PARAMS = Arrays.asList(
	  "_dc", RootView.PARAM_DOMAIN, ModelViewBase.MODEL_PARAM,
	  ViewWithModes.VIEWID_PARAM,   ViewWithModes.ENTITY_PARAM
	);

	/**
	 * Returns all additional parameters of the request
	 * as JSON text excluding these one: entity key.
	 */
	public String    getRequestParams()
	{
		StringBuilder         s  = new StringBuilder(32);
		Map<String, String[]> pm = request().getParameterMap();

		s.append('{');
		for(Map.Entry<String, String[]> e : pm.entrySet())
		{
			//?: {standard parameter}
			if(STD_PARAMS.contains(e.getKey()))
				continue;

			if(s.length() != 1)
				s.append(", ");

			//~: parameter name as the key
			s.append('"').append(SU.jss(e.getKey())).append("\": ");

			//?: {has single value}
			if(e.getValue().length == 1)
				s.append('"').append(SU.jss(e.getValue()[0])).append('"');
			//~: write an array
			else
			{
				s.append('[');
				for(int i = 0;(i < e.getValue().length);i++)
					s.append((i == 0)?("\""):(", \"")).
					  append(SU.jss(e.getValue()[i])).
					  append('"');
				s.append(']');
			}
		}
		s.append('}');

		return s.toString();
	}


	/* protected: view support interface */

	protected String obtainExtjsDomain()
	{
		String domain = obtainExtjsDomainFromRequest();
		return (domain != null)?(domain):("");
	}

	protected String obtainExtjsDomainFromRequest()
	{
		return SU.s2s(request().getParameter(getExtjsDomainParam()));
	}

	protected String obtainExtjsPositionFromRequest()
	{
		return SU.s2s(request().getParameter(getExtjsPositionParam()));
	}


	/* private: the state of the view */

	private String   extjsDomain;
	private String   viewId;

	/* private static: view ids generator  */

	private static AtomicLong VIEWID =
	  new AtomicLong();
}