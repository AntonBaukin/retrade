package com.tverts.system.tx;

/* Spring Framework */

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * TODO comment TxPoint
 *
 * @author anton.baukin@gmail.com
 */
public class TxPoint
{
	/* public: Singleton */

	public static TxPoint getInstance()
	{
		return INSTANCE;
	}

	private static final TxPoint INSTANCE =
	  new TxPoint();

	protected TxPoint()
	{}

	/* public: TxPoint interface */

	public boolean isRollbackOnly()
	{
		return Boolean.TRUE.equals(rollbackOnly.get());
	}

	public void    setRollbackFlag()
	{
		rollbackOnly.set(Boolean.TRUE);
	}

	public void    setRollbackOnly()
	{
		try
		{
			TransactionAspectSupport.currentTransactionStatus().
			  setRollbackOnly();
		}
		catch(NoTransactionException e)
		{
			throw new IllegalStateException(e);
		}

		rollbackOnly.set(Boolean.TRUE);
	}

	protected void clearRollbackOnly()
	{
		rollbackOnly.remove();
	}

	/* private: rollback only flags */

	private final ThreadLocal<Boolean> rollbackOnly =
	  new ThreadLocal<Boolean>();
}