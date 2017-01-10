package com.tverts.secure.force;

/* standard Java classes */

import java.util.List;

/* com.tverts: events */

import com.tverts.endure.secure.SecAble;
import com.tverts.event.AbleEvent;
import com.tverts.event.CreatedEvent;
import com.tverts.event.Event;

/* com.tverts: endure (core + auth + secure) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.secure.SecRule;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Domain Secure Force that enforces security on
 * the relations with Auth Logins: what user may
 * do with own login.
 *
 * An example of force: Allow user to change own
 * password, or login.
 *
 * This Force creates one Domain rule (to assign
 * Able) and hidden rules for each Auth Login.
 * When user is granted the Domain rule, an able
 * with his Login is created.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class OwnLoginForce extends DomainKeyForce
{
	/* public: Reactor interface */

	public void init()
	{
		super.init();

		//~: ensure the logins having no rules
		ensureMissingLogins();
	}

	public void react(Event event)
	{
		super.react(event);

		//?: {Auth Login created event}
		if(ise(event, CreatedEvent.class) && ist(event, AuthLogin.class))
			reactLoginCreated((CreatedEvent) event);

		//?: {own able event for Domain}
		if(isOwnAble(event) && isAbleRelated(event, Domain.class))
			reactDomainAble((AbleEvent) event);
	}


	/* protected: reactions */

	protected void reactLoginCreated(CreatedEvent e)
	{
		ensureLoginRule((AuthLogin) e.target());
	}

	protected void ensureLoginRule(AuthLogin l)
	{
		//~: try find the rule
		SecRule r = findRelatedRule(l.getPrimaryKey());
		if(r != null) return;

		//~: create the rule
		SecRule rule = new SecRule();

		//!: hidden
		rule.setHidden(true);

		//~: domain
		rule.setDomain(l.getDomain());

		//~: related login
		rule.setRelated(l.getUnity());

		//~: system title
		rule.setTitle(SU.cats("Hidden rule to [", getSecKey(),
		  "] own login [", l.getPrimaryKey(), "]"));

		//!: save it
		saveRule(rule);

		//~: ensure link with that login only
		ensureLink(getSecKey(), rule, l, !isForbid());
	}

	protected void ensureMissingLogins()
	{
		//~: find logins to ensure
		List<Long> ids = findLoginsToEnsure();

		LU.D(getLog(), logsig(), " found ",
		  ids.isEmpty()?("no"):("[" + ids.size() + ']'),
		  " logins to ensure hidden rule");

		//~: ensure them
		for(Long id : ids)
			ensureLoginRule((AuthLogin) session().load(AuthLogin.class, id));
	}

	protected void reactDomainAble(AbleEvent e)
	{
		SecAble able = (SecAble) e.target();

		//~: find hidden rule for that login
		SecRule rule = findRelatedRule(able.getLogin().getPrimaryKey());

		//?: {there is no rule}
		if(rule == null) throw EX.state(logsig(),
		  ": no hidden Secure Rule exists for login",
		  able.getLogin().getPrimaryKey()
		);

		//?: {was granted} ensure able for hidden rule
		if(e.isGranted())
			ensureAble(rule, able.getLogin(), null);
		//!: it was revoked
		else
			revokeAble(rule, able.getLogin().getPrimaryKey(), null);
	}

	@SuppressWarnings("unchecked")
	protected List<Long> findLoginsToEnsure()
	{
/*

 select l.id from AuthLogin l where l.id not in
   (select r.related.id from SecRule r where r.force = :force)

 */
		return (List<Long>) Q(

"select l.id from AuthLogin l where l.id not in\n" +
"  (select r.related.id from SecRule r where r.force = :force)"

		).
		  setParameter("force", uid()).
		  list();
	}
}