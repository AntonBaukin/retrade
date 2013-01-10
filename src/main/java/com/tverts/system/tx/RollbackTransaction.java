package com.tverts.system.tx;

/**
 * Orders to rollback the transaction.
 *
 * This exception never passes the transactional
 * layer until a cause (real) exception is present.
 *
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
	public Tx getTxContext()
	{
		return tx;
	}

	public RollbackTransaction setTxContext(Tx tx)
	{
		this.tx = tx;
		return this;
	}

	/* private: transaction context */

	private Tx tx;
}