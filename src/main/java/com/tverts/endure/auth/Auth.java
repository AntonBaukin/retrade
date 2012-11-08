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
	/* display names */

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
