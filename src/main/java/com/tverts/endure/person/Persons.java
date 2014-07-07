package com.tverts.endure.person;

/* com.tverts: api */

import com.tverts.api.clients.Person;

/* com.tverts: support */

import static com.tverts.support.SU.cat;


/**
 * Various helper functions and constants
 * related to the persons of the system.
 *
 * @author anton.baukin@gmail.com
 */
public class Persons
{
	/* type of the person */

	public static final String TYPE_PERSON =
	  "Core: Person";


	/* display name helpers */

	public static String name(PersonEntity pe)
	{
		Person p = pe.getOx();

		return cat(null, " ", p.getLastName(),
		  p.getFirstName(), p.getMiddleName()).toString();
	}
}