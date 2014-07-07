package com.tverts.endure.auth;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.secure.session.SecSession;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.PersonEntity;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Loads {@link AuthLogin} and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAuthLogin")
public class GetAuthLogin extends GetObjectBase
{
	/* Get Authentication Logins */

	public AuthLogin getLogin(Long pk)
	{
		return (AuthLogin) session().get(AuthLogin.class, pk);
	}

	public AuthLogin getLogin(Long domain, String code)
	{

// from AuthLogin where (domain.id = :domain) and (code = :code)

		return (AuthLogin) Q(

"from AuthLogin where (domain.id = :domain) and (code = :code)"

		).
		  setLong  ("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	public AuthLogin getSystemLogin(Long domain)
	{
		return getLogin(domain, Auth.SYSTEM_USER);
	}

	public AuthLogin getSystemLoginStrict(Long domain)
	{
		AuthLogin login = getLogin(domain, Auth.SYSTEM_USER);

		if(login == null) throw EX.state(
		  "System Login is not found in Domain [", domain, "]!"
		);

		return login;
	}

	@SuppressWarnings("unchecked")
	public List<AuthLogin> getLogins(Computer computer)
	{

// from AuthLogin where (computer = :computer)

		return (List<AuthLogin>) Q(

"from AuthLogin where (computer = :computer)"

		).
		  setParameter("computer", computer).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<AuthLogin> getLogins(PersonEntity person)
	{

// from AuthLogin where (person = :person)

		return (List<AuthLogin>) Q(

"from AuthLogin where (person = :person)"

		).
		  setParameter("person", person).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<AuthLogin> getSelectedLogins(Long domain, String selset)
	{

/*

 from AuthLogin l where (l.domain.id = :domain) and l.id in
   (select si.object from SelItem si join si.selSet ss
     where (ss.name = :set) and (ss.login.id = :login))

*/
		return (List<AuthLogin>) Q(

"from AuthLogin l where (l.domain.id = :domain) and l.id in\n" +
"   (select si.object from SelItem si join si.selSet ss\n" +
"     where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong("domain", domain).
		  setLong("login",  SecPoint.login()).
		  setString("set",  selset).
		  list();
	}


	/* Get Authentication Related Entities */

	public Computer getComputer(Long domain, String code)
	{

// from Computer where (domain.id = :domain) and (code = :code)

		return (Computer) Q(

"from Computer where (domain.id = :domain) and (code = :code)"

		).
		  setLong  ("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	public PersonEntity getPersonByLogin(Long domain, String login)
	{
/*

select p from AuthLogin al join al.person p where
  (al.domain.id = :domain) and (al.code = :login)

*/
		return (PersonEntity) Q(

"select p from AuthLogin al join al.person p where\n" +
"  (al.domain.id = :domain) and (al.code = :login)"

		).
		  setLong  ("domain", domain).
		  setString("login",  login).
		  uniqueResult();
	}


	/* Get Authentication Session */

	public AuthSession getAuthSession(String pkey)
	{
		return (pkey == null)?(null):
		  (AuthSession) session().get(AuthSession.class, pkey);
	}

	public SecSession  checkBind(String bind)
	{
		//<: load the session and the user login

/*

 select se.id, lo.id from
   AuthSession se join se.login lo
 where (se.bind = :bind) and
   (se.closeTime is null) and (lo.closeTime is null)

*/
		Object[] row = object(Object[].class,

"select se.id, lo.id from\n" +
"  AuthSession se join se.login lo\n" +
"where (se.bind = :bind) and\n" +
"  (se.closeTime is null) and (lo.closeTime is null)",

		  "bind", bind
		);

		//?: {not found it}
		if(row == null) return null;

		String id = (String)row[0];
		Long   lo = (Long)row[1];

		//>: load the session and the user login


		//<: once-activate web-session bind

// update AuthSession set bind = null where (id = :pk)

		//!: erase the bind
		int x = Q(

"  update AuthSession set bind = null where (id = :pk)",

		  "pk", id
		).
		  executeUpdate();

		//?: {not updated that row}
		if(x != 1) return null;

		//~: create the session
		SecSession secs = new SecSession();

		//~: auth id
		secs.attr(SecSession.ATTR_AUTH_SESSION, id);

		//~: login key
		secs.attr(SecSession.ATTR_AUTH_LOGIN, lo);

		//>: once-activate web-session bind


		//<: find the domain key

/*

 select d.id from AuthSession au
   join au.login l join l.domain d
 where (au.id = :pk)

 */

		Long d = object(Long.class,

"select d.id from AuthSession au\n" +
"  join au.login l join l.domain d\n" +
"where (au.id = :pk)",

		  "pk", id
		);

		//~: set domain
		secs.attr(SecSession.ATTR_DOMAIN_PKEY, EX.assertn(d));

		//>: find the domain key

		return secs;
	}
}