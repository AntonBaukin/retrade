package com.tverts.exec.service;

/* standard Java classes */

import java.util.Date;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxWrapperBase;
import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: endure (core + authentication) */

import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.ExecRequest;
import com.tverts.endure.core.Domain;


/**
 * Implementation of {@link ExecTxContext} transaction context.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecTxContext extends TxWrapperBase implements ExecTx
{
	/* public: constructor */

	public ExecTxContext(Tx tx)
	{
		super(tx);
	}


	/* public: ExecTxContext interface */

	public ExecTxContext init(ExecRequest request)
	{
		this.requestKey  = request.getPrimaryKey();
		this.requestTime = request.getRequestTime();

		return this;
	}

	/**
	 * Initializes the context with the authentication
	 * session of the currently executed request.
	 */
	public ExecTxContext init()
	{
		this.domainCache  = SecPoint.loadDomain();
		this.sessionCache = SecPoint.loadAuthSession();
		this.requestTime  = new Date();

		return this;
	}


	/* public: ExecTx interface */

	public Domain      getDomain()
	{
		if(domainCache != null)
			return domainCache;


/*

select do from Domain do, ExecRequest er where
  (er.id = :pk) and (do.id = er.domain)

*/

		return domainCache = (Domain) txSession().createQuery(

"select do from Domain do, ExecRequest er where\n" +
"  (er.id = :pk) and (do.id = er.domain)"

		).
		  setLong("pk", requestKey).
		  uniqueResult();
	}

	public AuthSession getAuthSession()
	{
		if((sessionCache != null) || sessionMissed)
			return sessionCache;

/*

select au from AuthSession au, ExecRequest er where
  (er.id = :pk) and (au.id = er.sessionId)

*/
		sessionCache = (AuthSession) txSession(this).createQuery(

"select au from AuthSession au, ExecRequest er where\n" +
"  (er.id = :pk) and (au.id = er.sessionId)"

		).
		  setLong("pk", requestKey).
		  uniqueResult();

		if(sessionCache == null)
			sessionMissed = true;
		return sessionCache;
	}

	public Date        getRequestTime()
	{
		return requestTime;
	}



	/* protected: the context */

	protected Long requestKey;
	protected Date requestTime;

	protected Domain      domainCache;
	protected AuthSession sessionCache;
	protected boolean     sessionMissed;
}
