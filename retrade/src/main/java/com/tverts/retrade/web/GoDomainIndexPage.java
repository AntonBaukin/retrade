package com.tverts.retrade.web;

/* Sprint Framework */

import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import com.tverts.servlet.filters.PickFilter;
import com.tverts.servlet.go.GoFilterBase;

/* com.tverts: endure (core + login) */

import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Redirects call to index page
 * (as default '/go/index').
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 4995 })
public class GoDomainIndexPage extends GoFilterBase
{
	/* Go Domain Index Page */

	/**
	 * This is the path the filter reacts
	 * to be and index request.
	 */
	public String getIndexPage()
	{
		return indexPage;
	}

	private String indexPage = "/go/index";

	public void setIndexPage(String p)
	{
		this.indexPage = EX.asserts(p);
	}

	/**
	 * Index page of an external client.
	 */
	public String getClientPage()
	{
		return clientPage;
	}

	private String clientPage = "/go/index-client";

	public void setClientPage(String p)
	{
		this.clientPage = EX.asserts(p);
	}


	/* protected: go-operation */

	protected String getGoPage(String path)
	{
		//?: {not a go-index page}
		if(!path.equals(getIndexPage()))
			return null;

		//?: {has request-specific property}
		String p; if((p = lookupIndexPage(path)) != null)
			return p;

		//?: {redirect client}
		if((p = redirectClient()) != null)
			return p;

		return null;
	}

	protected String lookupIndexPage(String path)
	{
		//~: domain-wide property
		Property p = bean(GetProps.class).
		  get(SecPoint.domain(), "Web", "IndexPage");

		return (p == null)?(null):SU.s2s(p.getValue());
	}

	protected String redirectClient()
	{
		//?: {this person has firm}
		return (SecPoint.clientFirmKey() == null)?(null):(getClientPage());
	}
}