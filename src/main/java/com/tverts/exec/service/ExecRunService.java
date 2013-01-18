package com.tverts.exec.service;

/* Spring Framework */

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: services */

import com.tverts.system.zservices.Event;
import com.tverts.system.zservices.ServiceBase;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;
import static com.tverts.system.tx.TxPoint.txContext;
import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: execution */

import com.tverts.exec.ExecPoint;

/* com.tverts: endure (authentication) */

import com.tverts.endure.auth.ExecRequest;

/* com.tverts: support */

import com.tverts.objects.XMAPoint;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Service that executes the tasks stored in the
 * database as {@link ExecRequest} objects.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ExecRunService extends ServiceBase
{
	/* public: Service interface */

	public void service(Event event)
	{
		if(event instanceof ExecRunEvent)
			executeEvent((ExecRunEvent) event);
	}


	/* protected: processing database tasks  */

	protected void executeEvent(ExecRunEvent e)
	{
		//?: {this task is not addressed directly}
		if(!uid().equals(e.getService()))
			return;

		//~: execute the task
		try
		{
			executeTask(e.getTaskKey());
		}
		catch(Throwable er)
		{
			//!: just log this unhandled error
			LU.E(getLog(), er, logsig(), " catched unexpected error ",
			  "while handling Exec Request [", e.getTaskKey(), "]!");
		}

		//!: notify the execution is done
		sendDoneEvent(e);
	}

	protected void sendDoneEvent(ExecRunEvent re)
	{
		ExecDoneEvent de = new ExecDoneEvent();

		de.setRunEvent(re);
		de.setService(re.getSourceService());

		this.send(de);
	}

	protected void executeTask(long taskKey)
	{
		try
		{
			executeTaskTx(taskKey);
		}
		catch(StateException e)
		{
			//!: rethrow this error to the outer
			throw e;
		}
		catch(Throwable e)
		{
			commitError(taskKey, e);
		}
	}

	/**
	 * This (Illegal) State Exception is not returned
	 * to the user (saved to the database), but just logged.
	 */
	public static class StateException extends RuntimeException
	{
		/* public: constructor */

		public StateException(String message)
		{
			super(message);
		}
	}


	/**
	 * Executes the request in it's own transaction.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void          executeTaskTx(long taskKey)
	  throws Throwable
	{
		//~: load the request
		ExecRequest request = (ExecRequest) txSession().
		  get(ExecRequest.class, taskKey);

		//?: {does not exist}
		if(request == null)
			throw new StateException(String.format(
			  "Exec Task [%d] does not exist!", taskKey
			));

		//?: {the request is already executed}
		if(request.isExecuted())
			throw new StateException(String.format(
			  "Exec Task [%d] is already executed!", taskKey
			));

		//?: {there is not request object} nothing to do
		if(request.getRequest() == null)
			return;

		//!: create execution Tx context
		ExecTxContext tx = createExecTx(request);

		try
		{
			//~: push execution transactional context
			TxPoint.getInstance().setTxContext(tx);

			executeTaskTx(request);
		}
		catch(Throwable e)
		{
			try
			{
				//!: set rollback only
				txContext().setRollbackOnly();

				//~: clear the session
				txSession().clear();
			}
			catch(Throwable e2)
			{
				//!: ignore this error, throw the original
			}

			throw e;
		}
		finally
		{
			//!: pop tx context
			TxPoint.getInstance().setTxContext(null);
		}
	}

	/**
	 * This method is invoked within {@link ExecTxContext} context.
	 */
	protected void          executeTaskTx(ExecRequest request)
	  throws Throwable
	{
		Object object;

		//~: get the request object from XML
		try
		{
			object = XMAPoint.readObject(request.getRequest());
		}
		catch(Throwable e)
		{
			throw new RuntimeException(
			  "Can't translate XML to Java Object!", e
			);
		}

		//~: execute the request object
		object = ExecPoint.execute(object);

		//?: {has response object} try write it
		if(object != null) try
		{
			request.setResponse(XMAPoint.writeObject(object));
		}
		catch(Throwable e)
		{
			throw new IllegalStateException(String.format(
			  "Can't translate Java Object with class '%s' to XML!",
			  object.getClass().getName()
			), e );
		}

		//!: mark the request as executed
		request.setExecuted(true);
		request.setResponseTime(new java.util.Date());

		//~: transaction number
		TxPoint.txn(request);
	}

	protected ExecTxContext createExecTx(ExecRequest request)
	{
		ExecTxContext ctx = new ExecTxContext(txContext());

		ctx.init(request);
		return ctx;
	}

	/**
	 * These call is executed in the context of the transaction
	 * that selects the execution requests as we can't rely upon
	 * the separated execution transaction where the session
	 * may be broken.
	 */
	protected void          commitError(long taskKey, Throwable error)
	{
		//~: load the request
		ExecRequest request = (ExecRequest) txSession().
		  load(ExecRequest.class, taskKey);

		Object      result  = null;
		byte[]      rbytes  = null;

		//!: mark it as executed
		request.setExecuted(true);

		//~: transaction number
		TxPoint.txn(request);

		//?: {is execution error}
		if(error instanceof ExecError)
			result = ((ExecError)error).getResult();

		//?: {the result exists} try convert it
		if(result != null) try
		{
			rbytes = XMAPoint.writeObject(result);
		}
		catch(Throwable e)
		{
			//!: ignore this error
		}

		//?: {has still no result} print the exception
		if(rbytes == null) try
		{
			rbytes = EX.print(error).getBytes("UTF-8");
		}
		catch(Throwable e)
		{
			//~: ignore this error
		}

		//~: set the response
		request.setResponse(rbytes);
		request.setResponseTime(new java.util.Date());

		//!: flush the session
		txSession().flush();
	}
}