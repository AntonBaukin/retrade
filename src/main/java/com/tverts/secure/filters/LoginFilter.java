package com.tverts.secure.filters;

/* Spring framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: servlet (filters) */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* com.tverts: secure sessions */

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

		//?: {user has session}
		if(task.getRequest().getSession(false) != null)
			//?: {session had not expired} proceed the request
			if(!checkExpired(task))
				return;

		//?: {only GET are allowed} forbid directly
		if(!"GET".equalsIgnoreCase(task.getRequest().getMethod())) try
		{
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

		//!: redirect to login
		redirectLogin(task);
	}

	public void closeFilter(FilterTask task)
	{}


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

	public String getDomainPath()
	{
		return domainPath;
	}

	public void setDomainPath(String s)
	{
		if(SU.sXe(s)) throw new IllegalArgumentException();
		this.domainPath = SU.s2s(s);
	}


	/* protected: filtering procedures */

	/**
	 * Returns true if the session had expired.
	 */
	protected boolean    checkExpired(FilterTask task)
	{
		//~: lookup the secured session
		SecSession secs = (SecSession) task.getRequest().
		  getSession().getAttribute(SecSession.class.getName());

		//?: {has no one} regard as expired
		return (secs == null) || getExpireStrategy().isExpired(secs);
	}

	/**
	 * Returns true if the session was successfully bound.
	 */
	protected boolean    bindSession(FilterTask task, String bind)
	{
		//~: check the bind + create the secured session
		SecSession secs = checkBind(bind);

		//~: save it in the web session
		if(secs != null)
			task.getRequest().getSession().
			  setAttribute(SecSession.class.getName(), secs);

		return (secs != null);
	}

	/**
	 * Returns new {@link SecSession} if the bind provided is valid.
	 */
	@Transactional
	protected SecSession checkBind(String bind)
	{

// select id from AuthSession where (bind = :bind)
// update AuthSession set bind = null where (id = :id)

		Object id = HiberPoint.getInstance().getSession().createQuery(

"select id from AuthSession where (bind = :bind)"
		).
		  setString("bind", bind).
		  uniqueResult();

		//?: {not found it}
		if(id == null)
			return null;

		//!: erase the bind
		int x = HiberPoint.getInstance().getSession().createQuery(

		  "update AuthSession set bind = null where (id = :id)"
		).
		  setString("id", (String)id).
		  executeUpdate();

		//?: {not updated that row}
		if(x != 1) return null;

		//~: create the session
		SecSession secs = new SecSession();

		//~: auth id
		secs.attr(SecSession.ATTR_AUTH_SESSION, (String)id);

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
		SecSession secs = (task.getRequest().getSession(false) == null)?(null):
		  (SecSession) task.getRequest().getSession().
		    getAttribute(SecSession.class.getName());

		//~: the domain code
		String dcode = null;
		Long   dkey  = (secs == null)?(null):
		  (Long) secs.attr(SecSession.ATTR_DOMAIN_PKEY);

		//?: {request has login domain path}
		StringBuilder s = new StringBuilder(
		  task.getRequest().getContextPath());

		if(getDomainPath().charAt(0) != '/')
			s.append('/');
		s.append(getDomainPath());
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
		//!: {session stores Domain key}
		else if(dkey != null)
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


	/* filter parameters */

	private ExpireStrategy expireStrategy = new TimeExpireStrategy();
	private String         bindParameter  = "retrade-session-bind";
	private String         domainAttr     = "retrade-domain-name";
	private String         loginPage      = "/.items/login.jsp";
	private String         domainPath     = "/go/login/";
}