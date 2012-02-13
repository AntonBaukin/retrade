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

	public String    getExtjsDomain()
	{
		return (extjsDomain != null)?(extjsDomain):
		  (extjsDomain = obtainExtjsDomain());
	}

	public String    getNewViewId()
	{
		return String.format("view_%x", VIEWID.incrementAndGet());
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


	/* private: the state of the view */

	private String   extjsDomain;
	private String   viewId;

	/* private static: view ids generator  */

	private static AtomicLong VIEWID =
	  new AtomicLong();
}