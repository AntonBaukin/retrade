package com.tverts.secure.force;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
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
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: events */

import com.tverts.event.EventPoint;

/* com.tverts: endure (core + secure) */

import com.tverts.endure.core.GetUnity;
import com.tverts.endure.secure.SecKey;
import com.tverts.endure.secure.SecRule;

/* com.tverts: secure */

import com.tverts.secure.SecKeys;
import com.tverts.secure.SecPoint;


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

	public String getTitle()
	{
		return title;
	}

	public String getDescr()
	{
		return descr;
	}


	/* public: SecForceBase (bean) interface */

	public void setUID(String uid)
	{
		this.uid = uid;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
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
		actionRun(ActionType.ENSURE, key);

		//~: cache the key saved
		SecKeys.INSTANCE.cache(key);
		return true;
	}

	protected SecKey  key(String name)
	{
		SecKey key = SecKeys.INSTANCE.key(name);

		if(key == null) throw new IllegalStateException(String.format(
		  "%s requested Secure Key [%s] that doesn't exist!",
		  logsig(), name
		));

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
		if((rule.getDomain() != null) && (rule.getRelated() == null))
			rule.setRelated(rule.getDomain().getUnity());

		//!: act save
		actionRun(ActionType.SAVE, rule);
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
	private String title;
	private String descr;
}