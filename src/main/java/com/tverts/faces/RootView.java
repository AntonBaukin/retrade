package com.tverts.faces;

/* standard Java classes */

import java.util.concurrent.atomic.AtomicLong;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ViewWithModes;

/* com.tverts: secured */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * View of the root page template.
 *
 * @author anton.baukin@gmail.com
 */
@Component("rootView") @Scope("request")
public class RootView extends ViewWithModes
{
	/* constants */

	public static final String EXTJSDOMAIN_PARAM = "extjs_domain";

	/**
	 * Position within the desktop layout panels. The default
	 * value is {@code null} string, and the real place is defined
	 * by the implementation (the desktop center).
	 */
	public static final String EXTJSPOS_PARAM    = "extjs_desktop_position";


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
		return EXTJSDOMAIN_PARAM;
	}

	public String    getExtjsPosition()
	{
		return obtainExtjsPositionFromRequest();
	}

	public String    getExtjsPositionParam()
	{
		return EXTJSPOS_PARAM;
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


	/* public: security issues */

	public boolean   isSecure(String key)
	{
		return SecPoint.isSecure(key);
	}


	/* protected: view support interface */

	protected String obtainExtjsDomain()
	{
		String domain = obtainExtjsDomainFromRequest();
		return (domain != null)?(domain):("");
	}

	protected String obtainExtjsDomainFromRequest()
	{
		return s2s(request().getParameter(getExtjsDomainParam()));
	}

	protected String obtainExtjsPositionFromRequest()
	{
		return s2s(request().getParameter(getExtjsPositionParam()));
	}


	/* private: the state of the view */

	private String   extjsDomain;
	private String   viewId;

	/* private static: view ids generator  */

	private static AtomicLong VIEWID =
	  new AtomicLong();
}