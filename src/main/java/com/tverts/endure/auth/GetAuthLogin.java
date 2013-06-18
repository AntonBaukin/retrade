package com.tverts.endure.auth;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.Person;

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
	public List<AuthLogin> getLogins(Person person)
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

	public Person   getPersonByLogin(Long domain, String login)
	{
/*

select p from AuthLogin al join al.person p where
  (al.domain.id = :domain) and (al.code = :login)

*/
		return (Person) Q(

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
}