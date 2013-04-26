package com.tverts.secure.session;

/* com.tverts: system */

import com.tverts.system.SystemConfig;


/**
 * Expire Strategy checking the session
 * inactivity time.
 *
 * @author anton.baukin@gmail.com
 */
public class TimeExpireStrategy implements ExpireStrategy
{
	public static final long serialVersionUID = 0L;


	/* public: ExpireStrategy interface */

	public boolean isExpired(SecSession session)
	{
		//~: get expire time
		Long etime = (Long)session.attr(SecSession.ATTR_EXPIRE_TIME);
		if(etime == null) throw new IllegalStateException();

		//~: check it
		return (System.currentTimeMillis() > etime);
	}

	public void    touch(SecSession session)
	{
		//~: create time
		Long ctime = (Long) session.attr(SecSession.ATTR_CREATE_TIME);

		if(ctime == null) session.attr(
		  SecSession.ATTR_CREATE_TIME,
		  ctime = System.currentTimeMillis()
		);

		//~: touch time
		Long touch = System.currentTimeMillis();
		session.attr(SecSession.ATTR_TOUCH_TIME, touch);

		//~: timeout
		Long tmout = (Long) session.attr(SecSession.ATTR_LIFE_TIME);

		if(tmout == null)
			tmout = 1000L * 60 * getTimeout();
		if(tmout <= 0) throw new IllegalStateException();

		//~: set expire time
		session.attr(SecSession.ATTR_EXPIRE_TIME, touch + tmout);
	}


	/* public: TimeExpireStrategy (bean) interface */

	/**
	 * Defines session expire timeout in minutes.
	 * By default, it is {@link SystemConfig#getSessionTime()}.
	 */
	public int  getTimeout()
	{
		return (timeout != null)?(timeout):
		  SystemConfig.getInstance().getSessionTime();
	}

	public void setTimeout(int timeout)
	{
		if(timeout < 0) throw new IllegalArgumentException();
		this.timeout = (timeout == 0)?(null):(timeout);
	}


	/* default timeout */

	private Integer timeout;
}