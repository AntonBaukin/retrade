package com.tverts.endure.msg;

/* com.tverts: endure (core) */

import com.tverts.endure.OxSearch;


/**
 * Analogue of {@link OxSearch} for the Messages.
 *
 * @author anton.baukin@gmail.com
 */
public interface MsgSearch
{
	/* Message Search */

	public String getOxSearch(Message msg);
}