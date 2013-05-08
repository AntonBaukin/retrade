package com.tverts.secure.system;

/* Java Servlet api */

import javax.servlet.http.Cookie;

/* Spring framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: servlet (filters) */

import com.tverts.servlet.REQ;
import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.secure.session.ExpireStrategy;
import com.tverts.secure.session.SecSession;
import com.tverts.secure.session.TimeExpireStrategy;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Checks whether a request has web session,
 * and there is an active Secure Session there.
 *
 * TODO prevent DoS on database via fake bind keys
 *
 * @author anton.baukin@gmail.com
 */
public class LoginFilter extends FilterBase
{
	/* public: FilterBase interface */

	public void openFilter(FilterTask task)
	{
		//?: {is not an external call}
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		//!: clear the session (just for safe)
		SecPoint.getInstance().setSecSession(null);

		//?: {user has session}
		if(task.getRequest().getSession(false) != null)
			//?: {session had not expired} proceed the request
			if(!checkExpired(task))
			{
				String path = task.getRequest().getRequestURI().
				  substring(task.getRequest().getContextPath().length());

				//?: {this is not a direct request for login page}
				if(!path.startsWith(getLoginPath()))
					return;
			}

		//?: {only GET are allowed} forbid directly
		if(!"GET".equalsIgnoreCase(task.getRequest().getMethod())) try
		{
			//?: {this is a localhost request} allow it
			if(REQ.isLocalhost(task.getRequest()))
				return;

			task.getResponse().sendError(403);
			task.setBreaked();
			return;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//~: get the bind parameter
		String bind = SU.s2s(task.getRequest().
		  getParameter(getBindParameter()));

		//?: {has bind} try it
		if(!SU.sXe(bind)) try
		{
			//?: {the session was not bound} forbid directly
			if(!bindSession(task, bind))
				task.getResponse().sendError(403);
			//!: send bound status
			else
			{
				task.getResponse().setStatus(200);
				task.getResponse().setContentType("text/plain;charset=UTF-8");
				task.getResponse().getOutputStream().print("bound");
			}

			task.setBreaked();
			return;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//?: {this is a localhost request} allow it
		if(REQ.isLocalhost(task.getRequest()))
			return;

		//!: redirect to login
		redirectLogin(task);
	}

	public void closeFilter(FilterTask task)
	{
		//?: {is not an external call}
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		try
		{
			//~: save the session as bytes
			SecSession secs = SecPoint.getInstance().getSecSession();
			if(secs != null) save(task, secs);
		}
		finally
		{
			//!: unbind from the current thread
			SecPoint.getInstance().setSecSession(null);
		}
	}


	/* public: NoSessionFilter (bean) interface */

	public ExpireStrategy getExpireStrategy()
	{
		return expireStrategy;
	}

	public void setExpireStrategy(ExpireStrategy s)
	{
		if(s == null) throw new IllegalArgumentException();
		this.expireStrategy = s;
	}

	public String getBindParameter()
	{
		return bindParameter;
	}

	public void setBindParameter(String s)
	{
		if(SU.sXe(s)) throw new IllegalArgumentException();
		this.bindParameter = SU.s2s(s);
	}

	public String getLoginPage()
	{
		return loginPage;
	}

	public void setLoginPage(String s)
	{
		if(SU.sXe(s)) throw new IllegalArgumentException();
		this.loginPage = SU.s2s(s);
	}

	public String getDomainAttr()
	{
		return domainAttr;
	}

	public void setDomainAttr(String s)
	{
		if(SU.sXe(s)) throw new IllegalArgumentException();
		this.domainAttr = SU.s2s(s);
	}

	public String getLoginPath()
	{
		return loginPath;
	}

	public void setLoginPath(String s)
	{
		if(SU.sXe(s)) throw new IllegalArgumentException();
		this.loginPath = SU.s2s(s);
	}


	/* protected: filtering procedures */

	protected SecSession restore(FilterTask task)
	{
		//?: {has no web session}
		if(task.getRequest().getSession(false) == null)
			return null;

		return (SecSession) task.getRequest().
		  getSession().getAttribute(SecSession.class.getName());

		//~: lookup the secured session
		//byte[]     data = (byte[]) task.getRequest().
		//  getSession().getAttribute(SecSession.class.getName());

		//~: restore the object from the session
		//return (data == null)?(null):
		//  OU.bytes2obj(data, SecSession.class);
	}

	protected void       save(FilterTask task, SecSession secs)
	{
		//task.getRequest().getSession().setAttribute(
		//  SecSession.class.getName(), OU.obj2bytes(secs)
		//);

		task.getRequest().getSession().setAttribute(
		  SecSession.class.getName(), secs
		);
	}

	/**
	 * Returns true if the session had expired.
	 */
	protected boolean    checkExpired(FilterTask task)
	{
		//~: restore from the web session
		SecSession secs = restore(task);
		if(secs == null) return true;

		//~: bind to the point
		SecPoint.getInstance().setSecSession(secs);

		//?: is manually closed
		if(Boolean.TRUE.equals(secs.attr(SecSession.ATTR_CLOSED)))
			return true;

		//?: {has no session | expired}
		if(secs.getExpireStrategy().isExpired(secs))
			return true;

		//!: touch the session
		secs.getExpireStrategy().touch(secs);

		return false;
	}

	/**
	 * Returns true if the session was successfully bound.
	 */
	protected boolean    bindSession(FilterTask task, String bind)
	{
		//~: check the bind + create the secured session
		SecSession secs = checkBind(task, bind);

		//?: {bind is valid & created new secure session}
		if(secs != null)
		{
			//~: save it in the web session
			save(task, secs);

			//~: bind to the point
			SecPoint.getInstance().setSecSession(secs);
		}

		return (secs != null);
	}

	/**
	 * Returns new {@link SecSession} if the bind provided is valid.
	 */
	@Transactional
	protected SecSession checkBind(FilterTask task, String bind)
	{

/*

select se.id, lo.id from
  AuthSession se join se.login lo
where (se.bind = :bind) and
  (se.closeTime is null) and (lo.closeTime is null)

update AuthSession set bind = null where (id = :id)

*/

		Object row = HiberPoint.getInstance().getSession().createQuery(

"select se.id, lo.id from\n" +
"  AuthSession se join se.login lo\n" +
"where (se.bind = :bind) and\n" +
"  (se.closeTime is null) and (lo.closeTime is null)"

		).
		  setString("bind", bind).
		  uniqueResult();

		//?: {not found it}
		if(row == null)
			return null;

		String id = (String)((Object[])row)[0];
		Long   lo = (Long)((Object[])row)[1];

		//!: erase the bind
		int x = HiberPoint.getInstance().getSession().createQuery(

		  "update AuthSession set bind = null where (id = :id)"
		).
		  setString("id", id).
		  executeUpdate();

		//?: {not updated that row}
		if(x != 1) return null;

		//~: create the session
		SecSession secs = new SecSession();

		//~: auth id
		secs.attr(SecSession.ATTR_AUTH_SESSION, id);

		//~: login key
		secs.attr(SecSession.ATTR_AUTH_LOGIN, lo);

		//~: set expire strategy + touch
		secs.setExpireStrategy(getExpireStrategy());
		secs.getExpireStrategy().touch(secs);

		//~: find the domain key

/*

select d.id from AuthSession au
  join au.login l join l.domain d
where (au.id = :id)

 */
		Object d = HiberPoint.getInstance().getSession().createQuery(

"select d.id from AuthSession au\n" +
"  join au.login l join l.domain d\n" +
"where (au.id = :id)"

		).
		  setString("id", (String)id).
		  uniqueResult();

		//~: set domain
		if(d == null) throw new IllegalStateException();
		secs.attr(SecSession.ATTR_DOMAIN_PKEY, (Long)d);

		//~: set domain cookie
		setDomainCookie(task, (Long)d);

		return secs;
	}

	protected void       redirectLogin(FilterTask task)
	{
		//~: find the domain
		defineDomain(task);

		try
		{
			//~: forward to the login page
			task.getRequest().getRequestDispatcher(getLoginPage()).
			  forward(task.getRequest(), task.getResponse());

			//!: do break the task
			task.setBreaked();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected void       defineDomain(FilterTask task)
	{
		//~: lookup the secured session
		SecSession secs  = SecPoint.getInstance().getSecSession();

		//~: the domain code
		String     dcode = null;
		Long       dkey  = (secs == null)?(null):
		  (Long) secs.attr(SecSession.ATTR_DOMAIN_PKEY);

		//?: {request has login domain path}
		StringBuilder s = new StringBuilder(
		  task.getRequest().getContextPath());

		if(getLoginPath().charAt(0) != '/')
			s.append('/');
		s.append(getLoginPath());
		if(s.charAt(s.length() - 1) != '/')
			s.append('/');

		String x = s.toString();
		String u = task.getRequest().getRequestURI();

		//?: {request refers the domain}
		if(u.startsWith(x))
		{
			u = SU.s2s(u.substring(x.length()));
			if(u != null) dcode = u;
		}

		//?: {lookup cookie}
		if(SU.sXe(dcode) && (dkey == null))
			dkey = getDomainCookie(task);

		//!: {has Domain key}
		if(SU.sXe(dcode) && (dkey != null))
			dcode = domainCode(dkey);

		//?: {thr domain is defined} save it in the request
		if(!SU.sXe(dcode))
			task.getRequest().setAttribute(getDomainAttr(), SU.s2s(dcode));
	}

	/**
	 * Returns the code of the domain. Note that this method
	 * is invoked here when there was valid session, but
	 * it had expired. Second transaction is so allowed.
	 */
	@Transactional
	protected String     domainCode(Long pkey)
	{

// select code from Domain where (id = :id)

		return (String) HiberPoint.getInstance().
		  getSession().createQuery(

"select code from Domain where (id = :id)"
		).
		  setLong("id", pkey).
		  uniqueResult();
	}

	protected void       setDomainCookie(FilterTask task, Long domain)
	{
		Cookie cookie = new Cookie("ReTradeDomain", domain.toString());

		cookie.setSecure(task.getRequest().isSecure());
		cookie.setPath(task.getRequest().getContextPath());
		task.getResponse().addCookie(cookie);
	}

	protected Long       getDomainCookie(FilterTask task)
	{
		Cookie[] cookies = task.getRequest().getCookies();

		if(cookies != null) for(Cookie cookie : cookies)
			if("ReTradeDomain".equals(cookie.getName())) try
			{
				return Long.parseLong(cookie.getValue());
			}
			catch(Exception e)
			{}

		return null;
	}


	/* filter parameters */

	private ExpireStrategy expireStrategy = new TimeExpireStrategy();
	private String         bindParameter  = "retrade-session-bind";
	private String         domainAttr     = "retrade-domain-name";
	private String         loginPage      = "/login.jsp";
	private String         loginPath      = "/go/login/";
}