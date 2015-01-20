package com.tverts.endure.auth;

/* Java */

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
	/* Types of the Authentication Entities */

	public static final String TYPE_COMPUTER =
	  "Core: Auth: Computer";

	public static final String TYPE_LOGIN    =
	  "Core: Auth: Login";


	/* System Codes */

	/**
	 * Defines code of {@link AuthLogin} that
	 * is automatically created in each Domain.
	 * As default, this login refers a Computer.
	 */
	public static final String SYSTEM_USER = "System";


	/* Messages Types */

	public static final String MSG_LOGIN_CREATE = "auth: login: create";


	/* Display Name Helpers */

	public static String name(ComputerEntity c)
	{
		return cats(catif(c.getCode(),
		  "[", c.getCode(), "], "), c.getName());
	}


	/* Login Helpers */

	public static String passwordHash(MessageDigest d, String p)
	{
		if(sXe(p)) throw new IllegalArgumentException();

		try
		{
			if(d != null)
				d.reset();
			else
				d = MessageDigest.getInstance("SHA-1");

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
