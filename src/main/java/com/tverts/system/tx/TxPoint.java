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

	public static TxContext txContext()
	{
		return TxPoint.getInstance().getTxContextSctrict();
	}

	/**
	 * Gives the global transaction context associated
	 * with the current request to the system.
	 */
	public TxContext        getTxContext()
	{
		return contexts.get();
	}

	/**
	 * Returns the global transaction context if it presents,
	 * or raises {@link IllegalStateException}.
	 */
	public TxContext        getTxContextSctrict()
	{
		TxContext tx = contexts.get();

		if(tx == null) throw new IllegalStateException();
		return contexts.get();
	}

	protected void          setTxContext(TxContext tx)
	{
		if(tx == null)
			contexts.remove();
		else
			contexts.set(tx);
	}

	/* private: rollback only flags */

	private final ThreadLocal<TxContext> contexts =
	  new ThreadLocal<TxContext>();
}