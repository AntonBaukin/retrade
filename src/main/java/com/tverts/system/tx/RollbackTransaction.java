package com.tverts.system.tx;

/**
 * Orders to rollback the transaction. This exception
 * not passes the transational layer if no cause
 * exception does presend.
 *
 * @author anton.baukin@gmail.com
 */
public class   RollbackTransaction
       extends RuntimeException
{
	public RollbackTransaction()
	{}

	public RollbackTransaction(Throwable cause)
	{
		super(cause);
	}

	/* public: RollbackTransaction interface */

	/**
	 * Returns the transaction context within the
	 * exception was raised. Note that the context'
	 * connection to the persistence resources is
	 * closed and may not be used.
	 *
	 * The context may be not defined.
	 */
	public TxContext           getTxContext()
	{
		return txContext;
	}

	public RollbackTransaction setTxContext(TxContext tx)
	{
		this.txContext = tx;
		return this;
	}

	/* private: transaction context */

	private TxContext txContext;
}