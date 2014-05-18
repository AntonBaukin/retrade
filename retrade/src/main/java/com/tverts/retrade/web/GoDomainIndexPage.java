package com.tverts.retrade.web;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import com.tverts.servlet.filters.GoFilterBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Redirects call to index page
 * (as default '/go/index').
 *
 * @author anton.baukin@gmail.com
 */
public class GoDomainIndexPage extends GoFilterBase
{
	/* protected: GoFilterBase interface */

	protected String getGoPage(String path)
	{
		//?: {not a go-index page}
		if(!path.equals(getIndexPage()))
			return null;

		//~: lookup the index property
		Property p = bean(GetProps.class).
		  get(SecPoint.domain(), "Web", "IndexPage");

		return (p == null)?(null):s2s(p.getValue());
	}


	/* public: GoIndexPage (bean) interface */

	public String getIndexPage()
	{
		return indexPage;
	}

	public void   setIndexPage(String p)
	{
		if((p = s2s(p)) == null)
			throw new IllegalArgumentException();
		this.indexPage = p;
	}


	/* configuration */

	private String indexPage = "/go/index";
}