package com.tverts.endure.auth;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.Person;

/* com.tverts: support */

import static com.tverts.support.SU.cat;
import static com.tverts.support.SU.cats;
import static com.tverts.support.SU.catif;


/**
 * Various helper functions and constants
 * related to authentication subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class Auth
{
	/* types of the authentication entities */

	public static final String TYPE_COMPUTER =
	  "Core: Auth: Computer";

	public static final String TYPE_LOGIN    =
	  "Core: Auth: Login";



	/* display names helpers */

	public static String name(Computer c)
	{
		return cats(catif(c.getCode(), "[", c.getCode(), "], "), c.getName());
	}

	public static String name(Person p)
	{
		return cat(null, " ", p.getFirstName(),
		  p.getMiddleName(), p.getLastName()).toString();
	}
}
