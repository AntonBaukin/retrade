package com.tverts.secure.force;

/* com.tverts: events */

import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;

/* com.tverts: endure (core + secure) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.secure.SecLink;
import com.tverts.endure.secure.SecRule;

/* com.tverts: support */

import com.tverts.support.LU;
import static com.tverts.support.SU.sXe;


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

	public void    init()
	{
		if(sXe(getSecKey()))
			throw new IllegalStateException(String.format(
			  "Domain Key Force [%s] has no Secure Key defined!", uid()
			));

		//?: {the key was actually created}
		if(ensureKey(getSecKey()))
			LU.I(getLog(), logsig(), " created sec key [", getSecKey(), ']');
	}

	public String  getTitle(SecRule rule)
	{
		return getTitle();
	}

	public String  getDescr(SecRule rule)
	{
		return getDescr();
	}


	/* public: Reactor interface */

	public void    react(Event event)
	{
		//?: {Domain created event}
		if(ise(event, CreatedEvent.class) && ist(event, Domain.class))
			reactDomainCreated((Domain)event.target(), (CreatedEvent)event);

		//?: {ask force event}
		if(event instanceof AskSecForceEvent)
			if(uid().equals(((AskSecForceEvent)event).getForce()))
				reactAskForce((AskSecForceEvent)event);
	}


	/* public: DomainKeyForce (bean) interface */

	public String  getSecKey()
	{
		return secKey;
	}

	public void    setSecKey(String secKey)
	{
		this.secKey = secKey;
	}

	public boolean isForbid()
	{
		return forbid;
	}

	public void    setForbid(boolean forbid)
	{
		this.forbid = forbid;
	}


	/* protected: reactions */

	protected void    reactDomainCreated(Domain d, CreatedEvent e)
	{
		//~: create & save the rule
		SecRule rule = saveDomainRule(d);

		LU.D(getLog(), logsig(), " acts on create Domain [", d.getPrimaryKey(),
		 "], saved rule [", rule.getPrimaryKey(), ']');

		//~: create the link (with the domain)
		linkRuleWithDomain(rule);
	}

	protected SecRule saveDomainRule(Domain d)
	{
		//~: create the rule
		SecRule rule = new SecRule();

		//~: domain is being created
		rule.setDomain(d);

		//!: save it
		saveRule(rule);

		return rule;
	}

	/**
	 * Creates {@link SecLink} for the Domain. This
	 * link is actually needed only for System Domain
	 * as only there the list of Domains is.
	 */
	protected void    linkRuleWithDomain(SecRule rule)
	{
		ensureLink(getSecKey(), rule, rule.getDomain(), !isForbid());
	}

	protected void    reactAskForce(AskSecForceEvent e)
	{
		//~: load the domain rule
		Long    domain = e.target().getDomain().getPrimaryKey();
		SecRule rule   = loadDomainRule(domain);

		//!: ensure the able
		ensureAble(rule, e.target());

		LU.D(getLog(), logsig(), " now able for login [",
		  e.target().getCode(), ']');
	}


	/* private: security key */

	private String  secKey;
	private boolean forbid;
}