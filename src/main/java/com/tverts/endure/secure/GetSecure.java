package com.tverts.endure.secure;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;


/**
 * Collection of functions to load
 * security related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getSecure")
public class GetSecure extends GetObjectBase
{
	/* Secure Keys */

	public SecKey getSecKey(Long pk)
	{
		return (SecKey) session().get(SecKey.class, pk);
	}

	public SecKey getSecKey(String name)
	{
		if(sXe(name)) throw new IllegalArgumentException();

		// from SecKey where (name = :name)

		return (SecKey) Q(
		  "from SecKey where (name = :name)"
		).
		  setString("name", name).
		  uniqueResult();
	}


	/* Secure Rules */

	@SuppressWarnings("unchecked")
	public List<SecRule> selectRules(Long domain, String force)
	{

// from SecRule where (domain.id = :domain) and (force = :force)

		return (List<SecRule>) Q(
"  from SecRule where (domain.id = :domain) and (force = :force)"
		).
		  setLong("domain", domain).
		  setString("force", force).
		  list();
	}


	/* Secure Able */

	public boolean hasAbles(SecRule r, AuthLogin l)
	{
// select 1 from SecAble a where (a.rule = :r) and (a.login = :l)

		return !Q(
"  select 1 from SecAble a where (a.rule = :r) and (a.login = :l)"
		).
		  setParameter("r", r).
		  setParameter("l", l).
		  setMaxResults(1).
		  list().isEmpty();
	}


	/* Secure Links */

	public SecLink getSecLink(SecKey k, SecRule r, Long target)
	{

/*

from SecLink sl where (sl.key = :k) and
  (sl.rule = :r) and (sl.target.id = :t)

*/

		return (SecLink) Q(

"from SecLink sl where (sl.key = :k) and\n" +
"  (sl.rule = :r) and (sl.target.id = :t)"

		).
		  setParameter("k", k).
		  setParameter("r", r).
		  setLong     ("t", target).
		  uniqueResult();
	}



	/* security checks */

	/**
	 * The Key on the Target entity is allowed when there is
	 * at least one allowing Link from that target by the key.
	 * (Link allows when it's deny = 0, forbids when deny = 1)
	 *
	 * And there is no deny Links to the same target.
	 *
	 * Links are selected by the Rules Able for the user.
	 */
	public boolean isSecure(Long login, Long target, SecKey key)
	{
/*

 select max(sl.deny) from SecLink sl join sl.rule sr, SecAble ab
 where (sl.key = :key) and (sl.target.id = :target) and
   (sr = ab.rule) and (ab.login.id = :login)

*/
		Number n = (Number) Q(

"select max(sl.deny) from SecLink sl join sl.rule sr, SecAble ab\n" +
"where (sl.key = :key) and (sl.target.id = :target) and\n" +
"  (sr = ab.rule) and (ab.login.id = :login)"

		).
		  setParameter("key",    key).
		  setLong     ("target", target).
		  setLong     ("login",  login).
		  uniqueResult();

		return (n != null) && (n.intValue() == 0);
	}
}