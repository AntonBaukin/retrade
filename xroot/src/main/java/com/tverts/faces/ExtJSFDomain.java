package com.tverts.faces;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


/**
 * Class-variable. Shortcut for
 * {@link RootView#getExtjsDomain()}.
 *
 * @author anton.baukin@gmail.com
 */
@Component("extDom") @Scope("request")
public class ExtJSFDomain
{
	/* Object */

	public String toString()
	{
		return (domain != null)?(domain):
		  (domain = bean(RootView.class).getExtjsDomain());
	}

	private String domain;
}