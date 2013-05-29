package com.tverts.secure.force;

/* com.tverts: events */

import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.secure.SecRule;

/* com.tverts: support */

import com.tverts.support.LU;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;
import com.tverts.support.fmt.TextFormat;


/**
 * Security Force that registers Security
 * Key configured and creates one Rule
 * for each {@link Domain}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DomainKeyForce extends SecForceBase
{
	/* public: SecForce interface */

	public void   init()
	{
		if(sXe(getSecKey()))
			throw new IllegalStateException(String.format(
			  "Domain Key Force [%s] has no Secure Key defined!", uid()
			));

		//?: {the key was actually created}
		if(ensureKey(getSecKey()))
			LU.I(getLog(), logsig(), " created sec key [", getSecKey(), ']');
	}

	public String getTitle(SecRule rule)
	{
		return (title == null)?(null):(title.format(
		  rule.getDomain().getCode()
		));
	}

	public String getDescr(SecRule rule)
	{
		return (descr == null)?(null):(descr.format(
		  rule.getDomain().getCode()
		));
	}


	/* public: Reactor interface */

	public void   react(Event event)
	{
		//?: {Domain created event}
		if(event instanceof CreatedEvent)
			if(event.target() instanceof Domain)
				reactDomainCreated((Domain)event.target(), (CreatedEvent) event);
	}


	/* public: DomainKeyForce (bean) interface */

	public String getSecKey()
	{
		return secKey;
	}

	public void   setSecKey(String secKey)
	{
		this.secKey = secKey;
	}

	public void   setRuleTitleFmt(String fmt)
	{
		this.title = ((fmt = s2s(fmt)) == null)?(null):
		  new TextFormat(fmt);
	}

	public void   setRuleDescrFmt(String fmt)
	{
		this.descr = ((fmt = s2s(fmt)) == null)?(null):
		  new TextFormat(fmt);
	}


	/* protected: reactions */

	protected void reactDomainCreated(Domain d, CreatedEvent e)
	{
		//~: save the rule
		SecRule rule = new SecRule();

		//~: domain is being created
		rule.setDomain(d);

		//!: save
		saveRule(rule);

		LU.D(getLog(), logsig(), " acts on create Domain [", d.getPrimaryKey(),
		 "], saved rule [", rule.getPrimaryKey(), ']');
	}


	/* private: security key */

	private String     secKey;
	private TextFormat title;
	private TextFormat descr;
}