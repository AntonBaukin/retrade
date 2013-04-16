package com.tverts.endure.auth;

/* standard Java classes */

import java.security.MessageDigest;

/* com.tverts: support */

import static com.tverts.support.SU.bytes2hex;
import static com.tverts.support.SU.cats;
import static com.tverts.support.SU.catif;
import static com.tverts.support.SU.sXe;


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


	/* system codes */

	/**
	 * Defines code of {@link AuthLogin} that
	 * is automatically created in each Domain.
	 * As default, this login refers a Computer.
	 */
	public static final String SYSTEM_USER = "System";


	/* display name helpers */

	public static String name(Computer c)
	{
		return cats(catif(c.getCode(),
		  "[", c.getCode(), "], "), c.getName());
	}


	/* login helpers */

	public static String passwordHash(MessageDigest d, String p)
	{
		if(sXe(p)) throw new IllegalArgumentException();

		try
		{
			d.reset();

			return new String(bytes2hex(
			  d.digest(p.getBytes("UTF-8"))
			));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
