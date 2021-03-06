package com.tverts.secure.force;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Hibernate Persistence Layer */

import com.tverts.endure.secure.SecSet;
import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.actions.ActionsPoint.actionRun;
import static com.tverts.actions.ActionsPoint.NO_FLUSH;

/* com.tverts: events */

import com.tverts.event.AbleEvent;
import com.tverts.event.Event;
import com.tverts.event.EventPoint;

/* com.tverts: endure (core + auth + secure) */

import com.tverts.endure.United;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecAble;
import com.tverts.endure.secure.SecKey;
import com.tverts.endure.secure.SecLink;
import com.tverts.endure.secure.SecRule;

/* com.tverts: secure */

import com.tverts.secure.SecKeys;
import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Implementation base for
 * Security Force Strategy.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SecForceBase
       implements     SecForce, SecForceRef
{
	/* public: SecForceRef interface */

	public List<SecForce> dereferObjects()
	{
		return Collections. <SecForce> singletonList(this);
	}


	/* public: SecForce interface */

	public String uid()
	{
		return this.uid;
	}

	/* public: SecForceBase (bean) interface */

	public void   setUID(String uid)
	{
		this.uid = uid;
	}


	/* protected: enforcing helpers */

	protected boolean ensureKey(String name)
	{
		//?: {the key exists}
		if(SecKeys.INSTANCE.key(name) != null)
			return false;

		//~: create the key
		SecKey key = new SecKey();

		//~: key name
		key.setName(name);

		//!: run ensure action
		actionRun(ActionType.ENSURE, key, NO_FLUSH, true);

		//~: cache the key saved
		SecKeys.INSTANCE.cache(key);
		return true;
	}

	protected SecKey  key(String name)
	{
		SecKey key = SecKeys.INSTANCE.key(name);

		if(key == null) throw EX.state(logsig(),
		  " requested Secure Key [", name, "] that doesn't exist!");

		return key;
	}

	protected Object  loadRelated(SecRule rule)
	{
		if(rule.getRelated() == null)
			throw new IllegalStateException();

		Object res = bean(GetUnity.class).
		  getUnited(rule.getRelated());

		if(res == null)
			throw new IllegalStateException();

		return res;
	}

	protected void    saveRule(SecRule rule)
	{
		//~: domain
		if(rule.getDomain() == null)
			rule.setDomain(SecPoint.loadDomain());

		//~: force UID
		if(rule.getForce() == null)
			rule.setForce(this.uid());

		//~: related unity (Domain by default)
		if(rule.getRelated() == null)
			rule.setRelated(rule.getDomain().getUnity());

		//?: {hidden rule without title} set empty string
		if(rule.isHidden() && (rule.getTitle() == null))
			rule.setTitle("");

		//!: act save
		actionRun(ActionType.SAVE, rule, NO_FLUSH, true);

		if(LU.isD(getLog())) LU.D(getLog(), logsig(), " saved ",
		  rule.isHidden()?("hidden "):(""), "rule on target ",
		  rule.getRelated().getUnityType().getTypeClass().getSimpleName(),
		  " [", rule.getRelated().getPrimaryKey(), "]"
		);
	}

	protected SecRule loadDomainRule(Long domain)
	{
		SecRule rule = findDomainRule(domain);

		if(rule == null)
			throw EX.state(logsig(), " has no Domain Rules in [", domain, "]!");

		return rule;
	}

	protected SecRule findDomainRule(Long domain)
	{
		List<SecRule> rules = bean(GetSecure.class).
		  selectRelatedRules(domain, uid());

		if(rules.isEmpty())
			return null;

		if(rules.size() != 1)
			throw EX.state(logsig(), " has multiple Domain Rules in [", domain, "]!");

		if(!domain.equals(rules.get(0).getRelated().getPrimaryKey()))
			throw EX.state(logsig(), " has Rule targeting not a Domain!");

		return rules.get(0);
	}

	protected SecRule findRelatedRule(Long related)
	{
		List<SecRule> rules = bean(GetSecure.class).
		  selectRelatedRules(related, uid());

		if(rules.isEmpty())
			return null;

		if(rules.size() != 1)
			throw EX.state(logsig(), " has multiple Related Rules ",
			  "for entity with pkey [", related, "]!");

		return rules.get(0);
	}

	protected boolean isAble(SecRule rule, AuthLogin login)
	{
		return bean(GetSecure.class).hasAbles(rule, login);
	}

	protected void    ensureLink(String key, SecRule rule, United target, boolean allow)
	{
		SecLink link = new SecLink();

		//~: secure key
		link.setKey(key(key));

		//~: rule
		link.setRule(rule);

		//~: target unity
		link.setTarget(EX.assertn(target.getUnity(), logsig(),
		  " Sec Link target [", target.getPrimaryKey(), "] has no Unified Mirror!"
		));

		//~: deny-allow
		if(!allow) link.setDeny();

		//!: ensure the link
		link = actionResult(SecLink.class,
		  actionRun(ActionType.ENSURE, link, NO_FLUSH, true));

		if(link != null) LU.D(getLog(), logsig(), " ensured " ,
		  (allow)?("ALLOW"):("FORBID"), " '", key, "' link [", link.getPrimaryKey(),
		  "] on target ", HiberPoint.type(target).getSimpleName(),
		  " [", target.getPrimaryKey(), ']'
		);
	}

	protected void    removeLink(String key, SecRule rule, United target)
	{
		SecLink link = bean(GetSecure.class).
		  getSecLink(key(key), rule, target.getPrimaryKey());
		if(link == null) return;

		//!: delete the link
		actionRun(ActionType.DELETE, link, NO_FLUSH, true);

		LU.D(getLog(), logsig(), " removed '", key, "' link [",
		  link.getPrimaryKey(), "] on target ",
		  HiberPoint.type(target).getSimpleName(),
		  " [", target.getPrimaryKey(), ']'
		);
	}

	protected void    ensureAble(SecRule rule, AuthLogin login, SecSet set)
	{
		//~: create able instance
		SecAble able = new SecAble();

		//~: login
		able.setLogin(login);

		//~: rule
		able.setRule(rule);

		//~: set
		able.setSet(set);

		//!: ensure the able
		actionRun(ActionType.ENSURE, able, NO_FLUSH, true);
	}

	protected void    revokeAble(SecRule rule, Long login, Long set)
	{
		//?: {has no set} take the default
		if(set == null) set = bean(GetSecure.class).getDefaultSecSet(
		  rule.getDomain().getPrimaryKey()).getPrimaryKey();

		//~: find able instance
		SecAble able = bean(GetSecure.class).
		  getSecAble(rule.getPrimaryKey(), login, set);

		//?: {found it} delete
		if(able != null)
			actionRun(ActionType.DELETE, able, NO_FLUSH, true);
	}


	/* protected: queries support */

	protected Session session()
	{
		Tx tx = TxPoint.getInstance().getTxContext();

		//?: {has context bound}
		if(tx != null)
			return TxPoint.txSession(tx);

		//~: direct Hibernate session access
		return HiberPoint.getInstance().getSession();
	}

	protected Query   Q(String hql, Object... replaces)
	{
		return HiberPoint.query(session(), hql, replaces);
	}

	protected Query   QB(QueryBuilder qb)
	{
		return qb.buildQuery(session());
	}


	/* protected: checks support */

	protected boolean ise(Event e, Class<? extends Event> cls)
	{
		return cls.isAssignableFrom(e.getClass());
	}

	@SuppressWarnings("unchecked")
	protected boolean ist(Event e, Class cls)
	{
		return cls.isAssignableFrom(e.target().getClass());
	}

	protected boolean isOwnAble(Event e)
	{
		return (e instanceof AbleEvent) &&
		  uid().equals(((SecAble) e.target()).getRule().getForce());
	}

	@SuppressWarnings("unchecked")
	protected boolean isAbleRelated(Event e, Class cls)
	{
		return (e instanceof AbleEvent) && cls.isAssignableFrom(
		  ((SecAble) e.target()).getRule().getRelated().getUnityType().getTypeClass()
		);
	}


	/* protected: logging */

	protected String getLog()
	{
		return EventPoint.LOG_EVENTS;
	}

	private volatile String logsig;

	protected String logsig()
	{
		if(this.logsig != null)
			return this.logsig;

		StringBuilder s = new StringBuilder(32);

		//~: class name
		s.append(getClass().getSimpleName());

		//?: {has no force}
		if(s.indexOf("Force") == -1)
			s.append(" Force");

		//~: uid
		s.append(" [").append(uid()).append(']');

		return this.logsig = s.toString();
	}


	/* security force attributes */

	private String uid;
}