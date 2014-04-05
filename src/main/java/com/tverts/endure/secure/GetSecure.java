package com.tverts.endure.secure;

/* Java */

import java.util.List;
import java.util.Set;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Collection of functions to load
 * security related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component
public class GetSecure extends GetObjectBase
{
	/* Secure Keys */

	public SecKey getSecKey(Long pk)
	{
		return (SecKey) session().get(SecKey.class, pk);
	}

	public SecKey getSecKey(String name)
	{
		if(SU.sXe(name)) throw EX.arg();

		// from SecKey where (name = :name)

		return (SecKey) Q(
		  "from SecKey where (name = :name)"
		).
		  setString("name", name).
		  uniqueResult();
	}


	/* Secure Rules */

	public SecRule getSecRule(Long pk)
	{
		return (SecRule) session().get(SecRule.class, pk);
	}

	/**
	 * Finds the rules that related the Domain itself.
	 * (Called as Domain, or root Rules.)
	 */
	@SuppressWarnings("unchecked")
	public List<SecRule> selectRelatedRules(Long related, String force)
	{

/*

 from SecRule where (related.id = :related) and (force = :force)

*/

		return (List<SecRule>) Q(

"from SecRule where (related.id = :related) and (force = :force)"

		).
		  setLong("related", related).
		  setString("force", force).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSetAbleRules(Long login, Long secSet)
	{
/*

 select r.id from SecAble a join a.rule r join a.set s where
   (a.login.id = :login) and (s.id = :set)

 */
		return (List<Long>) Q(

"select r.id from SecAble a join a.rule r join a.set s where\n" +
"  (a.login.id = :login) and (s.id = :set)"

		).
		  setLong("login", login).
		  setLong("set",   secSet).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<SecAble> findSetAbles(Long login, Long secSet)
	{

// select a from SecAble a join a.set s where (a.login.id = :login) and (s.id = :set)

		return (List<SecAble>) Q(

"  select a from SecAble a join a.set s where (a.login.id = :login) and (s.id = :set)"

		).
		  setLong("login", login).
		  setLong("set",   secSet).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<SecAble> findLoginAblesOfSelectedRules
	  (Long login, Long secSet, String selset)
	{
/*

 select a from SecAble a join a.rule r join a.set s where
  (a.login.id = :login) and (s.id = :set) and
  r.id in (select si.object from SelItem si join si.selSet ss
    where (ss.name = :set) and (ss.login.id = :xlogin))

 */
		return (List<SecAble>) Q(

"select a from SecAble a join a.rule r join a.set s where\n" +
" (a.login.id = :login) and (s.id = :set) and\n" +
" r.id in (select si.object from SelItem si join si.selSet ss\n" +
"   where (ss.name = :set) and (ss.login.id = :xlogin))"

		).
		  setLong("login",  login).
		  setLong("set",    secSet).
		  setString("xset", selset).
		  setLong("xlogin", SecPoint.login()).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<SecAble> findLoginAblesOfSelectedRules(Long login, String selset)
	{
/*

 select a from SecAble a join a.rule r where
  (a.login.id = :login) and r.id in
    (select si.object from SelItem si join si.selSet ss
      where (ss.name = :xset) and (ss.login.id = :xlogin))

 */
		return (List<SecAble>) Q(

"select a from SecAble a join a.rule r where\n" +
"  (a.login.id = :login) and r.id in\n" +
"    (select si.object from SelItem si join si.selSet ss\n" +
"      where (ss.name = :xset) and (ss.login.id = :xlogin))"

		).
		  setLong("login",  login).
		  setString("xset", selset).
		  setLong("xlogin", SecPoint.login()).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSelectedRules(Long domain, String selset)
	{

/*

 select r.id from SecRule r where (r.domain.id = :domain) and
   r.id in (select si.object from SelItem si join si.selSet ss
     where (ss.name = :set) and (ss.login.id = :login))

 */
		return (List<Long>) Q(

"select r.id from SecRule r where (r.domain.id = :domain) and\n" +
"   r.id in (select si.object from SelItem si join si.selSet ss\n" +
"     where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong("domain", domain).
		  setLong("login",  SecPoint.login()).
		  setString("set",  selset).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSelectedSetsRules(Long domain, String selset)
	{

/*

 select distinct r.id from SecAble a join a.rule r join a.set s
   where (r.domain = :domain) and
     s.id in (select si.object from SelItem si join si.selSet ss
       where (ss.name = :set) and (ss.login.id = :login))

 */
		return (List<Long>) Q(

"select distinct r.id from SecAble a join a.rule r join a.set s\n" +
"   where (r.domain = :domain) and\n" +
"     s.id in (select si.object from SelItem si join si.selSet ss\n" +
"       where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong("domain", domain).
		  setLong("login",  SecPoint.login()).
		  setString("set",  selset).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSelectedLoginsRules(Long domain, String selset)
	{

/*

 select distinct r.id from SecAble a join a.rule r where
   (r.domain = :domain) and (r.hidden = false) and a.login.id in
     (select si.object from SelItem si join si.selSet ss
       where (ss.name = :set) and (ss.login.id = :login))

 */
		return (List<Long>) Q(

"select distinct r.id from SecAble a join a.rule r where\n" +
"  (r.domain = :domain) and (r.hidden = false) and a.login.id in\n" +
"    (select si.object from SelItem si join si.selSet ss\n" +
"      where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong("domain", domain).
		  setLong("login",  SecPoint.login()).
		  setString("set",  selset).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<SecSet> findSelectedSets(Long domain, String selset)
	{

/*

 from SecSet s where (s.domain = :domain) and
   s.id in (select si.object from SelItem si join si.selSet ss
      where (ss.name = :set) and (ss.login.id = :login))

 */
		return (List<SecSet>) Q(

"from SecSet s where (s.domain = :domain) and\n" +
"   s.id in (select si.object from SelItem si join si.selSet ss\n" +
"      where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong("domain", domain).
		  setLong("login",  SecPoint.login()).
		  setString("set",  selset).
		  list();
	}


	/* Secure Sets */

	public SecSet getSecSet(Long pk)
	{
		return (pk == null)?(null):
		  (SecSet) session().get(SecSet.class, pk);
	}

	public SecSet getSecSet(Long domain, String name)
	{
		if((name = SU.s2s(name)) == null) name = "";

// from SecSet where (domain.id = :domain) and (name = :name)

		return (SecSet) Q(
"  from SecSet where (domain.id = :domain) and (name = :name)"
		).
		  setLong("domain", domain).
		  setString("name", name).
		  uniqueResult();
	}

	public SecSet getDefaultSecSet(Long domain)
	{
		SecSet res = getSecSet(domain, "");

		if(res == null) throw EX.state("Domain [", domain,
		  "] has no default Secure Set!");

		return res;
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

	public SecAble getSecAble(Long pk)
	{
		return (SecAble) session().get(SecAble.class, pk);
	}

	public SecAble getSecAble(Long rule, Long login, Long set)
	{

/*

 select a from SecAble a join a.set s where (a.rule.id = :r) and
   (a.login.id = :l) and (s.id = :s)

 */

		return (SecAble) Q(

"select a from SecAble a join a.set s where (a.rule.id = :r) and \n" +
"  (a.login.id = :l) and (s.id = :s)"

		).
		  setLong("r", rule).
		  setLong("l", login).
		  setLong("s", set).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSecAbles(SecSet set)
	{

// select a.id from SecAble a join a.set s where (s = :set)

		return (List<Long>) Q(

"  select a.id from SecAble a join a.set s where (s = :set)"

		).
		  setParameter("set", set).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> findSecAblesWithDuplicates(SecSet set, SecSet check)
	{

/*

 select a1.id from SecAble a1 join a1.set s1 where (s1 = :set) and
   a1.rule in (select a2.rule from SecAble a2 join a2.set s2 where (s2 = :check))

 */

		return (List<Long>) Q(

"select a1.id from SecAble a1 join a1.set s1 where (s1 = :set) and\n" +
"  a1.rule in (select a2.rule from SecAble a2 join a2.set s2 where (s2 = :check))"

		).
		  setParameter("set",   set).
		  setParameter("check", check).
		  list();
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
	@SuppressWarnings("unchecked")
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

/*

--> debug -->

select sl.id from SecLink sl join sl.rule sr, SecAble ab
 where (sl.key = :key) and (sl.target.id = :target) and
   (sr = ab.rule) and (ab.login.id = :login) and (sl.deny = 1)


		List x = (List) (((n == null) || (n.intValue() == 0))?(null): Q(

"select sl.id from SecLink sl join sl.rule sr, SecAble ab\n" +
" where (sl.key = :key) and (sl.target.id = :target) and\n" +
"   (sr = ab.rule) and (ab.login.id = :login) and (sl.deny = 1)"

		).
		  setParameter("key",    key).
		  setLong     ("target", target).
		  setLong     ("login",  login).
		  list());
*/

		return (n != null) && (n.intValue() == 0);
	}

	/**
	 * This method works as or operator on calls of
	 * {@link #isSecure(Long, Long, SecKey)}.
	 */
	@SuppressWarnings("unchecked")
	public boolean isAnySecure(Long login, Long target, Set<SecKey> keys)
	{
/*

 select max(sl.deny), sl.key.id from
   SecLink sl join sl.rule sr, SecAble ab
 where (sl.key in (:keys)) and (sl.target.id = :target) and
   (sr = ab.rule) and (ab.login.id = :login)
 group by sl.key.id

*/
		List<Object[]> ns = (List<Object[]>) Q(

"select max(sl.deny), sl.key.id from\n" +
"  SecLink sl join sl.rule sr, SecAble ab\n" +
"where (sl.key in (:keys)) and (sl.target.id = :target) and\n" +
"  (sr = ab.rule) and (ab.login.id = :login)\n" +
"group by sl.key.id"

		).
		  setParameterList("keys", keys).
		  setLong("target", target).
		  setLong("login",  login).
		  list();

		for(Object[] x : ns)
		{
			Number n = (Number)x[0];

			if((n != null) && (n.intValue() == 0))
				return true;
		}
		return false;
	}


	/**
	 * This method works as and operator on calls of
	 * {@link #isSecure(Long, Long, SecKey)}.
	 */
	@SuppressWarnings("unchecked")
	public boolean isAllSecure(Long login, Long target, Set<SecKey> keys)
	{
/*

 select sum(sl.deny), sl.key.id from
   SecLink sl join sl.rule sr, SecAble ab
 where (sl.key in (:keys)) and (sl.target.id = :target) and
   (sr = ab.rule) and (ab.login.id = :login)
 group by sl.key.id

*/
		List<Object[]> ns = (List<Object[]>) Q(

"select sum(sl.deny), sl.key.id from\n" +
"  SecLink sl join sl.rule sr, SecAble ab\n" +
"where (sl.key in (:keys)) and (sl.target.id = :target) and\n" +
"  (sr = ab.rule) and (ab.login.id = :login)\n" +
"group by sl.key.id"

		).
		  setParameterList("keys", keys).
		  setLong("target", target).
		  setLong("login",  login).
		  list();

		if(ns.size() != keys.size())
			return false;

		for(Object[] x : ns)
		{
			Number n = (Number)x[0];

			if((n == null) || (n.intValue() != 0))
				return false;
		}

		return true;
	}
}