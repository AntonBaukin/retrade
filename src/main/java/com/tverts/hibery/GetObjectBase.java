package com.tverts.hibery;

/* standard Java classes */

import java.io.Serializable;
import java.util.List;

/* Hibernate Persistence Layer */

import com.tverts.secure.SecPoint;
import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core + secure) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.secure.SecKey;

/* com.tverts: secure */

import com.tverts.secure.SecKeys;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Basic implementation of so-called 'Get Object' Spring Bean.
 * It implements Data Access Object pattern.
 *
 * Get objects may be a prototype, or a singleton beans.
 * Consider {@link GetObjectPrototypeBase} subclass to allow
 * direct session references stored in prototype beans.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GetObjectBase
{
	/* public: access Session Factory */

	@SuppressWarnings("unchecked")
	public <T extends NumericIdentity> T
	                  getNumeric(Class<T> c1ass, Long pk)
	{
		return (T) session().get(c1ass, pk);
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		Tx tx = TxPoint.getInstance().getTxContext();

		//?: {has context bound}
		if(tx != null)
			return TxPoint.txSession(tx);

		//~: direct Hibernate session access
		return HiberPoint.getInstance().getSession();
	}

	protected Query   Q(String hql, Object... params)
	{
		return HiberPoint.params(
		  HiberPoint.query(session(), hql), params);
	}

	protected Query   QR(String hql, Object... replaces)
	{
		return HiberPoint.query(session(), hql, replaces);
	}

	@SuppressWarnings("unchecked")
	protected <O> O   get(Class<O> c1ass, Serializable key)
	{
		return (O)session().get(c1ass, key);
	}

	@SuppressWarnings("unchecked")
	protected <O> O   load(Class<O> c1ass, Serializable key)
	{
		return (O)session().load(c1ass, key);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> list(Class<T> cls, String hql, Object... params)
	{
		EX.assertn(cls);
		return (List<T>) Q(hql, params).list();
	}

	@SuppressWarnings("unchecked")
	protected <T> T   object(Class<T> cls, String hql, Object... params)
	{
		Object res = Q(hql, params).uniqueResult();

		EX.assertn(cls);
		EX.assertx((res == null) || cls.isAssignableFrom(res.getClass()));
		return (T) res;
	}

	protected Query   QB(QueryBuilder qb)
	{
		return qb.buildQuery(session());
	}


	/* protected: secured restrictions */

	protected void    secureByKey(QueryBuilder qb, String idpath, String key)
	{
		//~: get the key
		SecKey skey = SecKeys.INSTANCE.key(key);
		if(skey == null) return; //<-- soft variant (if no such forces)

		secureByKey(qb, idpath, skey, SecPoint.login());
	}

	protected void    secureByKey
	  (QueryBuilder qb, String idpath, SecKey key, Long login)
	{
		if(SecPoint.isSystemLogin()) return;


/*

 0 = (select max(secLink.deny) from SecLink secLink
   join secLink.rule secRule, SecAble secAble
   where (secLink.key = :key) and (secLink.target.id = ID) and
   (secRule = secAble.rule) and (secAble.login.id = :login))

*/
		final String Q =

"0 = (select max(secLink.deny) from SecLink secLink" +
"   join secLink.rule secRule, SecAble secAble" +
"   where (secLink.key = :key) and (secLink.target.id = ID) and" +
"   (secRule = secAble.rule) and (secAble.login.id = :login))";

		//~: domain
		qb.getClauseWhere().addPart(Q.replace("ID", idpath)).
		  param("key",   key).
		  param("login", login);
	}
}