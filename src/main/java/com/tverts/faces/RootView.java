package com.tverts.faces;

/* Java */

import java.util.Arrays;
import java.util.List;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import com.tverts.servlet.REQ;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * View of the root page template.
 *
 * @author anton.baukin@gmail.com
 */
@Component @Scope("request")
public class RootView extends ViewWithModes
{
	/* Constants */

	public static final String PARAM_DOMAIN   =
	  "extjs_domain";

	/**
	 * Position within the desktop layout panels. The default
	 * value is {@code null} string, and the real place is defined
	 * by the implementation (the desktop center).
	 */
	public static final String PARAM_POSITION =
	  "extjs_desktop_position";


	/* Global Actions */

	public String    doLogoff()
	{
		SecPoint.closeSecSession();
		return null;
	}


	/* Access Shared View State */

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

	private String extjsDomain;

	public Long      getEntityKey()
	{
		String p = getParam(getEntityParam());
		return (p == null)?(null):Long.parseLong(p);
	}

	/**
	 * Returns all additional parameters of the request
	 * as JSON text excluding any of STD_PARAMS.
	 */
	public String    getRequestParams()
	{
		return REQ.jsonRequestParams(STD_PARAMS);
	}

	private static final List<String> STD_PARAMS = Arrays.asList(
	  "_dc", RootView.PARAM_DOMAIN, ModelViewBase.MODEL_PARAM,
	  ViewWithModes.VIEWID_PARAM,   ViewWithModes.ENTITY_PARAM
	);


	/* protected: view support interface */

	protected String genNewViewId()
	{
		return bean(GenViewId.class).genViewId();
	}

	protected String obtainExtjsDomain()
	{
		return SU.sXs(obtainExtjsDomainFromRequest());
	}

	protected String obtainExtjsDomainFromRequest()
	{
		return getParam(getExtjsDomainParam());
	}

	protected String obtainExtjsPositionFromRequest()
	{
		return getParam(getExtjsPositionParam());
	}
}