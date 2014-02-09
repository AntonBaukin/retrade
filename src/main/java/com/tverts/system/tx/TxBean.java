package com.tverts.system.tx;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Wraps execution into transaction scopes.
 *
 * @author anton.baukin@gmail.com.
 */
@Component @Scope("prototype")
public class TxBean
{
	/* public: execution interface */

	public void      execute(Runnable task)
	{
		EX.assertn(task);

		try
		{
			if(requireNew)
				executeNewTx(task);
			else
				executeTx(task);
		}
		catch(RuntimeException x)
		{
			Throwable e = EX.xrt(x);

			//?: {is pure rollback} not re-throw
			if(e instanceof RollbackTransaction)
				if(e.getCause() == null)
					e = null;

			if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else if(e != null)
				throw EX.wrap(e);
		}
	}


	/* public: configuration interface */

	public TxBean setNew(boolean requireNew)
	{
		this.requireNew = requireNew;
		return this;
	}


	/* protected: execution variants */

	@Transactional(rollbackFor = Throwable.class,
	  propagation = Propagation.REQUIRED
	)
	protected void executeTx(Runnable task)
	{
		openTxScope();

		Throwable error = null; try
		{
			task.run();
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			closeTxScope(error);
		}
	}

	@Transactional(rollbackFor = Throwable.class,
	  propagation = Propagation.REQUIRES_NEW
	)
	protected void executeNewTx(Runnable task)
	{
		openTxScope();

		Throwable error = null; try
		{
			task.run();
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			closeTxScope(error);
		}
	}


	/* protected: scope execution */

	protected Tx   createTxContext()
	{
		return TxPoint.getInstance().
		  getTxCreator().createTxContext();
	}

	protected void openTxScope()
	{
		TxPoint.getInstance().setTxContext(createTxContext());
	}

	protected void closeTxScope(Throwable error)
	{
		Tx tx = TxPoint.getInstance().getTxContext();

		//!: pop the nested transaction context
		TxPoint.getInstance().setTxContext(null);

		//~: unwrap the error
		error = EX.xrt(error);

		//?: {has rollback invocation error}
		if(error instanceof RollbackTransaction)
		{
			RollbackTransaction rbe = (RollbackTransaction)error;

			//?: {has no specific context set} set the current one
			if(rbe.getTxContext() == null)
				rbe.setTxContext(tx);

			throw rbe;
		}

		//?: {has uncaught invocation error}
		if(error != null)
			throw new RollbackTransaction(error).setTxContext(tx);

		//?: {is marked to rollback only}
		if(tx.isRollbackOnly())
			throw new RollbackTransaction().setTxContext(tx);
	}


	/* protected: configuration */

	protected boolean requireNew;
}