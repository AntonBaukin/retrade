package com.tverts.exec.service;

/* standard Java classes */

import java.util.Date;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxContextWrapper;
import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: endure (core + authentication) */

import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.ExecRequest;
import com.tverts.endure.core.Domain;


/**
 * Implementation of {@link ExecTx} transaction context.
 *
 * @author anton.baukin@gmail.com
 */
public class      ExecTxContext
       extends    TxContextWrapper
       implements ExecTx
{
	/* public: constructor */

	public ExecTxContext(TxContext tx)
	{
		super(tx);
	}


	/* public: ExecTxContext interface */

	public void init(ExecRequest request)
	{
		this.requestKey  = request.getPrimaryKey();
		this.requestTime = request.getRequestTime();
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
		sessionCache = (AuthSession) txSession().createQuery(

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
