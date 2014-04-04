package com.tverts.exec;

/* com.tverts: execution service */

import com.tverts.exec.service.ExecTx;
import com.tverts.exec.service.ExecTxContext;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;


/**
 * Point to invoke execution subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecPoint
{
	/* ExecPoint Singleton */

	public static ExecPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ExecPoint INSTANCE =
	  new ExecPoint();

	protected ExecPoint()
	{}


	/* public: ExecPoint (singleton) interface */

	public static Object execute(Object request)
	  throws ExecError
	{
		return INSTANCE.executor.execute(request);
	}

	/**
	 * Executes the task in an {@link ExecTx} context.
	 * External {@link Tx} context must exist!
	 *
	 * If there is an execution context,
	 * new one is not created.
	 */
	public static Object executeTx(Object request)
	  throws ExecError
	{
		//~: lookup the existing context
		ExecTx etx = TxPoint.txContext(ExecTx.class);
		ExecTx xtx = etx;

		//?: {not found it} create the new one
		if(xtx == null)
		{
			xtx = new ExecTxContext(TxPoint.txContext()).init();
			TxPoint.INSTANCE.setTxContext(xtx);
		}

		//~: execute the request
		try
		{
			return INSTANCE.executor.execute(request);
		}
		finally
		{
			//?: {created own context} pop it
			if(xtx != etx)
				TxPoint.INSTANCE.setTxContext(null);
		}
	}


	/* public: ExecPoint (instance) interface */

	public void setExecutor(RootExecutor executor)
	{
		if(executor == null) throw new IllegalArgumentException();
		this.executor = executor;
	}

	public void activate()
	{
		this.executor.registerExecutors();
	}


	/* private: root executor */

	private volatile RootExecutor executor =
	  new RootExecutor();
}