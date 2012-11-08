package com.tverts.endure.auth;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.Person;


/**
 * Loads {@link AuthLogin} and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAuthLogin")
public class GetAuthLogin extends GetObjectBase
{
	/* Get Authentication Logins */

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
}
