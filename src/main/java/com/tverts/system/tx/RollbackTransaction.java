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
}