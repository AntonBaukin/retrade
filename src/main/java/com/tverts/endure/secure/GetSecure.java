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
}