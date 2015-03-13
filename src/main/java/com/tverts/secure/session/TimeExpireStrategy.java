package com.tverts.secure.session;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Expire Strategy checking the session
 * inactivity time.
 *
 * @author anton.baukin@gmail.com
 */
public class TimeExpireStrategy implements ExpireStrategy
{
	/* public: ExpireStrategy interface */

	public boolean isExpired(SecSession session)
	{
		//~: get expire time
		Object etime = session.attr(SecSession.ATTR_EXPIRE_TIME);

		//~: check it
		return (etime == null) ||
		  (System.currentTimeMillis() > Long.parseLong(etime.toString()));
	}

	public void    touch(SecSession session)
	{
		//~: touch time
		long touch = System.currentTimeMillis();
		session.attr(SecSession.ATTR_TOUCH_TIME, Long.toString(touch));

		//~: timeout
		Object tmout = session.attr(SecSession.ATTR_LIFE_TIME);
		if(tmout == null)
			tmout = 1000L * 60 * getTimeout();
		else
			tmout = Long.parseLong((String) tmout);
		EX.assertx((Long)tmout > 0L);

		//~: set expire time
		session.attr(SecSession.ATTR_EXPIRE_TIME,
		  Long.toString(touch + (Long)tmout));
	}


	/* Time Expire Strategy (bean) */

	/**
	 * Defines session expire timeout in minutes.
	 * By default, it is {@link SystemConfig#getSessionTime()}.
	 */
	public int  getTimeout()
	{
		return (timeout != null)?(timeout):
		  SystemConfig.getInstance().getSessionTime();
	}

	private Integer timeout;

	public void setTimeout(int timeout)
	{
		EX.assertx(timeout >= 0);
		this.timeout = (timeout == 0)?(null):(timeout);
	}
}