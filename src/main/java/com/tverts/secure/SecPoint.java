package com.tverts.secure;

/* standard Java classes */

import java.util.Collection;
import java.util.HashSet;

/* Java Servlet api */

import javax.servlet.http.HttpSession;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import com.tverts.servlet.RequestPoint;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core + auth + secure) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.ActAuthSession;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecKey;

/* com.tverts: secure */

import com.tverts.secure.session.SecSession;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Access various security related objects
 * bound to the pending request, or web session.
 *
 * @author anton.baukin@gmail.com
 */
public final class SecPoint
{
	/* Singleton */

	public static final SecPoint INSTANCE =
	  new SecPoint();

	public static SecPoint getInstance()
	{
		return INSTANCE;
	}


	/* security keys */

	public static final String KEY_VIEW   = "view";
	public static final String KEY_EDIT   = "edit";
	public static final String KEY_DELETE = "delete";


	/* log destinations */

	public static final String LOG_SEC    =
	  "com.tverts.secure";


	/* public: SecPoint (static) interface */

	public static SecSession  secSession()
	{
		return EX.assertn( INSTANCE.getSecSession(),
		  "No Secure Session instance is bound to the current thread!"
		);
	}

	public static Long        domain()
	{
		String d = secSession().attr(SecSession.ATTR_DOMAIN_PKEY);
		if(d == null) throw EX.state(
		  "Secure Session has no Domain key for Auth: [",
		  secSession().attr(SecSession.ATTR_AUTH_SESSION), "]"
		);

		return Long.parseLong(d);
	}

	public static Long        login()
	{
		String l = secSession().attr(SecSession.ATTR_AUTH_LOGIN);
		if(l == null) throw EX.state(
		  "Secure Session has no Login key for Auth: [",
		  secSession().attr(SecSession.ATTR_AUTH_SESSION), "]"
		);

		return Long.parseLong(l);
	}

	public static Long        loginOrNull()
	{
		String l = secSession().attr(SecSession.ATTR_AUTH_LOGIN);
		return (l == null)?(null):Long.parseLong(l);
	}

	public static void        closeSecSession()
	{
		//~: set the closed attribute
		secSession().attr(SecSession.ATTR_CLOSED, "true");

		//~: execute Auth Login close action
		AuthSession session = bean(GetAuthLogin.class).
		  getAuthSession(secSession().attr(SecSession.ATTR_AUTH_SESSION));

		//?: {session exists and is not closed yet}
		if((session != null) && (session.getCloseTime() == null))
			actionRun(ActAuthSession.CLOSE, session);
	}

	public static Domain      loadDomain()
	{
		Domain d = bean(GetDomain.class).getDomain(domain());

		if(d == null) throw EX.state(
		  "Domain pkey [", domain(), "] doesn't exist!"
		);

		return d;
	}

	public static AuthSession loadAuthSession()
	{
		return EX.assertn(bean(GetAuthLogin.class).getAuthSession(
		  secSession().attr(SecSession.ATTR_AUTH_SESSION)),

		  "Auth Session [", secSession().attr(SecSession.ATTR_AUTH_SESSION),
		  "] is not found in the database!"
		);
	}

	public static AuthLogin   loadLogin()
	{
		AuthLogin l = bean(GetAuthLogin.class).getLogin(login());

		if(l == null) throw EX.state(
		  "Auth Login pkey [", login(), "] doesn't exist!"
		);

		return l;
	}

	/**
	 * Code of the System Domain.
	 */
	public static final String SYSTEM_DOMAIN = "System";

	public static boolean     isSystemDomain()
	{
		return SYSTEM_DOMAIN.equals(loadDomain().getCode());
	}

	private static final String SESSION_SYSTEM_ATTR =
	  SecPoint.class.getName() + ": system login: ";

	public static boolean     isSystemLogin()
	{
		//~: inspect the session cache
		HttpSession session = RequestPoint.sessionOrNull();
		if(session != null)
		{
			Long x = (Long) session.getAttribute(SESSION_SYSTEM_ATTR + domain());
			if(x != null) return x.equals(login());
		}

		//~: load the System login
		AuthLogin system = bean(GetAuthLogin.class).getSystemLogin(domain());

		//?: {has session} cache
		if((system != null) && (session != null))
			session.setAttribute(SESSION_SYSTEM_ATTR + domain(), system.getPrimaryKey());

		return (system != null) && system.getPrimaryKey().equals(login());
	}

	public static void        checkSystemDomain()
	{
		if(!isSystemDomain())
			throw EX.state("System Domain only!");
	}

	public static Domain      loadSystemDomain()
	{
		Domain d =  bean(GetDomain.class).getDomain(SYSTEM_DOMAIN);

		if(d == null) throw EX.state("No System Domain present!");
		return d;
	}

	public static Long        clientFirmKey()
	{
		//~ access session
		SecSession s = INSTANCE.getSecSession();
		if(s == null) return null;

		//?: {this person has firm}
		String r = s.attr(SecSession.ATTR_CLIENT_FIRM);
		if(r != null) return Long.parseLong(r);

		//?: {searched the key}
		if("true".equals(s.attr(ATTR_CLIENT_FIRM_SEARCHED)))
			return null;

		//~: do search
		AuthLogin l = EX.assertn(loadLogin());
		if((l.getPerson() != null) && (l.getPerson().getFirm() != null))
			s.attr(SecSession.ATTR_CLIENT_FIRM,
			  r = l.getPerson().getFirm().getPrimaryKey().toString());

		//~: mark as searched
		s.attr(ATTR_CLIENT_FIRM_SEARCHED, "true");

		return (r == null)?(null):(Long.parseLong(r));
	}

	public static Long        clientFirmKeyStrict()
	{
		return EX.assertn(clientFirmKey(), "Client with login[",
		  login(), "] has no Contractor (Firm) assignment!");
	}

	private static final String ATTR_CLIENT_FIRM_SEARCHED =
	  SecSession.ATTR_CLIENT_FIRM + " [Searched]";


	/* public: SecPoint (security checks) interface */

	/**
	 * Checks whether current user has access
	 * with the key named to the Domain.
	 */
	public static boolean     isSecure(String key)
	{
		return isSystemLogin() || bean(GetSecure.class).
		  isSecure(login(), domain(), SecKeys.secKey(key));
	}

	public static boolean     isSecure(Long target, String key)
	{
		return isSystemLogin() || bean(GetSecure.class).
		  isSecure(login(), target, SecKeys.secKey(key));
	}

	/**
	 * Returns true when at least one of the keys is
	 * allowed, and not simultaneously forbidden.
	 */
	public static boolean     isAnySecure(Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<>(keys.size());
		for(String key : keys)
			if((key = SU.s2s(key)) != null)
				skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAnySecure(login(), domain(), skeys);
	}

	public static boolean     isAnySecure(Long target, Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<>(keys.size());
		for(String key : keys)
			if((key = SU.s2s(key)) != null)
				skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAnySecure(login(), target, skeys);
	}

	/**
	 * Returns true when all the keys are allowed.
	 */
	public static boolean     isAllSecure(Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<>(keys.size());
		for(String key : keys)
			if((key = SU.s2s(key)) != null)
				skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAllSecure(login(), domain(), skeys);
	}

	public static boolean     isAllSecure(Long target, Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<>(keys.size());
		for(String key : keys)
			if((key = SU.s2s(key)) != null)
				skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAllSecure(login(), target, skeys);
	}

	public static void        checkSameDomain(Object target)
	{
		if(!Boolean.TRUE.equals(isSameDomain(target)))
			throw EX.forbid("Entity processed has else Domain!");
	}

	/**
	 * Returns true when object is in the same Domain
	 * as the user requesting; false, when not; or null
	 * when target object may not be traced up to it's
	 * Domain instance (can't tell).
	 */
	public static Boolean     isSameDomain(Object target)
	{
		//?: {not a database object}
		if(!(target instanceof NumericIdentity))
			return null;

		//?: {not a domain instance}
		if(!(target instanceof DomainEntity))
			return null;

		return CMP.eq(((DomainEntity)target).getDomain(), domain());
	}


	/* public: SecPoint (password protection) interface */

	/**
	 * Works with 20-byte source arrays. For given SHA-1 hash of the user
	 * password, and the seed, and the source bytes, produces the resulting
	 * bytes that may be decoded with the same password.
	 *
	 * Format of the seed may be any of supported by the sign
	 * procedure of {@link DigestCache#sign(Object...)}.
	 *
	 * The decode procedure is the same call over the previously
	 * encoded source bytes.
	 */
	public byte[] hashBytes(Object pass, Object seed, byte[] src)
	{
		byte[] d = DigestCache.INSTANCE.sign(pass, seed);

		EX.assertx(d.length == 20);
		EX.assertx(src.length == 20);
		for(int i = 0;(i < 20);i++)
			d[i] ^= src[i];

		return d;
	}

	/**
	 * Wrapper over {@link #hashBytes(Object, Object, byte[])}.
	 * Takes user password hash, see {@link AuthLogin#getPasshash()},
	 * and the seed object (some random string or bytes), covers the
	 * given password string to secure send it over public wire.
	 *
	 * Warning. Password may not contain more than 20 UTF-8 bytes!
	 */
	public String encodePassword(char[] passHash, Object seed, String pass)
	{
		try
		{
			byte[] p = pass.getBytes("UTF-8");
			EX.assertx(p.length <= 20);

			byte[] s = new byte[20];
			System.arraycopy(p, 0, s, 0, p.length);

			byte[] h = hashBytes(passHash, seed, s);
			return new String(SU.bytes2hex(h));
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}

	public String encodePassword(Object seed, String pass)
	{
		String ph = EX.assertn(loadLogin().getPasshash());
		EX.assertx(ph.length() == 40);

		return encodePassword(ph.toCharArray(), seed, pass);
	}

	public String decodePassword(char[] passHash, Object seed, String hash)
	{
		try
		{
			EX.assertx(hash.length() == 40);
			byte[] h = SU.hex2bytes(hash);
			EX.assertx(h.length == 20);

			//~: decode the bytes back
			h = hashBytes(passHash, seed, h);

			//~: find ending zero
			int end; for(end = 0;(end < h.length);end++)
				if(h[end] == 0) break;

			EX.assertx(end != 0);
			return new String(h, 0, end, "UTF-8");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}

	public String decodePassword(Object seed, String hash)
	{
		String ph = EX.assertn(loadLogin().getPasshash());
		EX.assertx(ph.length() == 40);

		return encodePassword(ph.toCharArray(), seed, hash);
	}


	/* public: SecPoint (access) interface */

	public SecSession getSecSession()
	{
		return session.get();
	}

	public void       setSecSession(SecSession secs)
	{
		if(secs != null)
			this.session.set(secs);
		else
			this.session.remove();
	}


	/* private: thread local state */

	private ThreadLocal<SecSession> session =
	  new ThreadLocal<>();
}