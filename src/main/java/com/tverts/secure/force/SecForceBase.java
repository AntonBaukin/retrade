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

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: secure */

import com.tverts.secure.SecKeys;
import com.tverts.endure.secure.SecKey;


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

	protected void ensureKey(String name)
	{
		//?: {the key exists}
		if(SecKeys.INSTANCE.key(name) != null)
			return;

		//~: create the key
		SecKey key = new SecKey();

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


	/* security force attributes */

	private String uid;
	private String title;
	private String descr;
}