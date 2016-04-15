package com.tverts.api.core;

/* standard Java classes */

import java.util.Date;


/**
 * Implemented by entities that have timestamp
 * equal to the database order index.
 */
public interface Timed
{
	/* public: Timed interface */

	public Date getTime();

	public void setTime(Date time);
}
