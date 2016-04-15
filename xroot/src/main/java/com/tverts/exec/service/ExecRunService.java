package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: spring + tx */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: api */

import com.tverts.api.Payload;
import com.tverts.api.Ping;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.secure.session.SecSession;

/* com.tverts: execution */

import com.tverts.exec.ExecPoint;

/* com.tverts: endure (authentication) */

import com.tverts.endure.auth.ExecRequest;

/* com.tverts: support */

import com.tverts.objects.XPoint;
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
			executeTaskPrepare(taskKey);
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

	protected void          executeTaskPrepare(long taskKey)
	  throws Throwable
	{
		//~: load the request
		final ExecRequest request = (ExecRequest) TxPoint.txSession().
		  get(ExecRequest.class, taskKey);

		//?: {does not exist}
		EX.assertn(request, "Exec Task [", taskKey, "] does not exist!");

		//?: {the request is already executed}
		EX.assertx(!request.isExecuted(), "Exec Task [", taskKey, "] is already executed");

		//?: {there is not request object} nothing to do
		if(request.getRequest() == null)
			return;

		//!: do execute the request in separated transaction
		try
		{
			//?: {already in execution-tx} just invoke
			if(TxPoint.txContext(ExecTxContext.class) != null)
				executeTaskTx(request, null);
			//~: execute in the scopes (with new transaction)
			else
			{
				final String[] txid = new String[1];
				long           td   = System.currentTimeMillis();

				bean(TxBean.class).setNew().execute(new Runnable()
				{
					public void run()
					{
						try
						{
							executeTaskTx(request, txid);
						}
						catch(Throwable e)
						{
							throw EX.wrap(e);
						}
					}
				});

				if(LU.isD(LU.LOGT))
					LU.D(LU.LOGT, ")-[", txid[0], "]-tx in ", LU.td(td));
			}
		}
		catch(Throwable e)
		{
			throw EX.xrt(e);
		}
	}

	/**
	 * Executes the request in it's own transaction
	 * ({@link ExecTxContext} context)
	 */
	protected void          executeTaskTx(ExecRequest request, String[] txid)
	  throws Throwable
	{
		//!: create execution Tx context
		ExecTxContext tx = createExecTx(request);

		try
		{
			//~: push execution transactional context
			TxPoint.getInstance().setTxContext(tx);
			if(txid != null) txid[0] = tx.txid();

			if(LU.isD(LU.LOGT))
				LU.D(LU.LOGT, "tx-[", tx.txid(), "]-(");

			//~: bind secure session
			SecPoint.getInstance().setSecSession(createSecSession(tx));

			//!: actually execute
			executeTaskDo(request);
		}
		catch(Throwable e)
		{
			try
			{
				//!: set rollback only
				tx.setRollbackOnly();

				//~: clear the session
				TxPoint.txSession(tx).clear();
			}
			catch(Throwable e2)
			{
				//!: ignore this error, throw the original
			}

			throw e;
		}
		finally
		{
			//~: unbind secure session
			SecPoint.getInstance().setSecSession(null);

			//~: pop execution tx context
			TxPoint.getInstance().setTxContext(null);
		}
	}

	protected void          executeTaskDo(ExecRequest request)
	  throws Throwable
	{
		Object object;

		//~: get the request object from XML
		try
		{
			object = XPoint.xml().read(false, Object.class, request.getRequest());
		}
		catch(Throwable e)
		{
			throw new RuntimeException(
			  "Can't translate XML to Java Object!", e
			);
		}

		if(LU.isD(getExLog())) LU.D(getExLog(), "request [", logsig(object), ']');

		//~: execute the request object
		object = ExecPoint.execute(object);

		//?: {has response object} try write it
		if(object != null) try
		{
			request.setResponse(XPoint.xml().write(false, object));
		}
		catch(Throwable e)
		{
			throw new IllegalStateException(String.format(
			  "Can't translate Java Object with class '%s' to XML!",
			  object.getClass().getName()
			), e );
		}

		//~: mark the request as executed
		request.setExecuted(true);
		request.setResponseTime(new java.util.Date());

		//~: transaction number
		TxPoint.txn(request);
	}

	protected ExecTxContext createExecTx(ExecRequest request)
	{
		ExecTxContext ctx = new ExecTxContext(TxPoint.txContext());

		ctx.init(request);
		return ctx;
	}

	protected SecSession    createSecSession(ExecTxContext tx)
	{
		return new SecSession(tx.getAuthSession());
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
		ExecRequest request = (ExecRequest) TxPoint.txSession().
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

		//?: {has no result} write error as the text
		if(result == null)
			result = new com.tverts.api.ExecError(EX.print(error));

		//~: write result as XML bean
		try
		{
			rbytes = XPoint.xml().write(false, result);
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

		//~: transaction number
		TxPoint.txn(request);

		//!: flush the session
		//flush(TxPoint.txSession());
	}


	/* protected: logging */

	protected String getExLog()
	{
		return this.getClass().getName();
	}

	@SuppressWarnings("unchecked")
	protected String logsig(Object request)
	{
		if(!(request instanceof Ping))
			return LU.sig(request);

		Object obj = ((Ping)request).getRequest();
		if(obj == null) return LU.sig(request);

		if(!(obj instanceof Payload))
			return LU.sig(obj);

		Object op = ((Payload)obj).getOperation();
		if(op == null) return LU.sig(obj);

		String st = op.toString();
		String xs = op.getClass().getName() + "@" + Integer.toHexString(op.hashCode());
		return ((st != null) && !st.equals(xs))?(st):LU.sig(op);
	}
}