package com.tverts.exec.service;

/* standard Java classes */

import java.util.Date;

/* com.tverts: system transactions */

import com.tverts.system.tx.Tx;

/* com.tverts: endure (core + authentication) */

import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.core.Domain;


/**
 * Transactional context installed by tasks
 * execution service. Provides additional
 * information on the task.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ExecTx extends Tx
{
	/* public: ExecTx interface */

	/**
	 * Queries the Domain of the request.
	 * Note that the result may be cached.
	 */
	public Domain      getDomain();

	/**
	 * Queries Authentication Session this request
	 * belongs to. System requests does not have it.
	 * Note that the result may be cached.
	 */
	public AuthSession getAuthSession();

	public Date        getRequestTime();
}