package com.tverts.faces;

/* Java */

import java.util.Arrays;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.secure.session.SecSession;

/* com.tverts: endure (core, person) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.person.PersonEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Faces View that provides essential seciruty checks.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class SecureViewBase extends ViewBase
{
	/* Domain and Person */

	public Domain       getDomain()
	{
		return SecPoint.loadDomain();
	}

	public PersonEntity getPerson()
	{
		if(person != null) return person;

		return person = EX.assertn(
		  SecPoint.loadLogin().getPerson(),
		  "Login [", SecPoint.login(), "] has Person undefined!"
		);
	}

	private PersonEntity person;

	public boolean      isClientUser()
	{
		return (SecPoint.secSession().attr(SecSession.ATTR_CLIENT_FIRM) != null);
	}


	/* Security Issues */

	public boolean isSecure(String key)
	{
		return SecPoint.isSecure(key);
	}

	public boolean isSecureEntity(NumericIdentity e, String key)
	{
		return SecPoint.isSecure(e.getPrimaryKey(), key);
	}

	public boolean isSecureEntityKey(Long pk, String key)
	{
		return SecPoint.isSecure(pk, key);
	}

	public boolean isSystemLogin()
	{
		return SecPoint.isSystemLogin();
	}

	public void    forceSecure(String key)
	{
		if(!SecPoint.isSecure(key))
			throw EX.forbid();
	}

	public void    forceSecureEntity(NumericIdentity e, String key)
	{
		if(!SecPoint.isSecure(e.getPrimaryKey(), key))
			throw EX.forbid();
	}

	public void    forceSecureEntityKey(Long pk, String key)
	{
		if(!SecPoint.isSecure(pk, key))
			throw EX.forbid();
	}

	public void    forceSameDomain(Object target)
	{
		if(!Boolean.TRUE.equals(SecPoint.isSameDomain(target)))
			throw EX.forbid("Entity processed has else Domain!");
	}

	/**
	 * Checks whether any of the keys given is secure.
	 * All the keys are encoded into single string and
	 * separated with ';' character.
	 */
	public boolean isAnySecure(String keys)
	{
		return SecPoint.isAnySecure(Arrays.asList(SU.s2a(keys, ';')));
	}

	public boolean isAnySecureEntity(NumericIdentity e, String keys)
	{
		return SecPoint.isAnySecure(
		  e.getPrimaryKey(), Arrays.asList(SU.s2a(keys, ';')));
	}

	public boolean isAllSecure(String keys)
	{
		return SecPoint.isAllSecure(Arrays.asList(SU.s2a(keys, ';')));
	}

	public boolean isAllSecureEntity(NumericIdentity e, String keys)
	{
		return SecPoint.isAllSecure(
		  e.getPrimaryKey(), Arrays.asList(SU.s2a(keys, ';')));
	}

	public void    forceAnySecure(String keys)
	{
		if(!isAnySecure(keys))
			throw EX.forbid();
	}

	public void    forceAnySecureEntity(NumericIdentity e, String keys)
	{
		if(!isAnySecureEntity(e, keys))
			throw EX.forbid();
	}

	public void    forceSecureClientOnly(boolean client)
	{
		//HINT:  -argument-  -client-  -own-user-
		//         true        allow     forbid
		//         false       forbid    allow

		//?: {client has no firm} forbid
		if(client == (SecPoint.clientFirmKey() == null))
			throw EX.forbid();
	}
}
