package com.tverts.shunts.service;

/* com.tverts: objects */

import com.tverts.objects.RunnableInterruptible;

/* com.tverts: system services */

import com.tverts.system.services.BreakingTask;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;

import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolError;

/* com.tverts: system support */

import com.tverts.support.LU;

/**
 * Cycling, {@link BreakingTask} and {@link RunnableInterruptible}
 * task that is created for the thread of {@link SelfShuntService}.
 *
 * In each cycle sends the next request to the shunt system.
 * Is able to interrupt both the protocol, and the task thread.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SelfShuntServiceTask
       implements RunnableInterruptible, BreakingTask

{
	/* public: constructor */

	public SelfShuntServiceTask(SelfShuntService service)
	{
		if(service == null)
			throw new IllegalArgumentException();

		this.service  = service;
		this.protocol = service.createProtocolInstance();
	}

	/* public: Runnable interface */

	public void    run()
	{
		//~: check the closed status
		if(closed) return;

		//?: {the cycle is no already opened} do it
		if(!opened) try
		{
			logProtocolOpenBefore();
			protocol.openProtocol();
			opened = true;
			logProtocolOpenSuccess();
		}
		catch(Throwable e)
		{
			handleProtocolOpenError(e);
			if(closed) return;
		}

		//!: do the protocol cycle
		try
		{
			logSendNextRequestBefore();
			closed = !protocol.sendNextRequest();
			logSendNextRequestSuccess();
		}
		catch(Throwable e)
		{
			handleSendNextRequestError(e);
		}

		SelfShuntReport report = null;

		//?: {the protocol must be closed} do it
		if(closed) try
		{
			logProtocolCloseBefore();
			report = protocol.closeProtocol();
			logProtocolCloseSuccess();
		}
		catch(Throwable e)
		{
			handleProtocolCloseError(e);
		}

		//?: {closed, has the report} handle it
		if(report != null)
			processShuntReport(report);
	}

	/* public: RunnableInterruptible interface */

	public void    interrupt()
	  throws IllegalStateException
	{
		//?: {the protocol is already closed} nothing to do
		if(closed) return;

		//~: interrupt the protocol
		try
		{
			protocol.interruptProtocol();
		}
		catch(SeShProtocolError e)
		{
			throw new IllegalStateException(e);
		}

		//!: interrupt the task thread
		if(interruptor != null)
			interruptor.interrupt();
	}

	public void    setInterruptor(Interruptor x)
	{
		this.interruptor = x;
	}

	/* public: BreakingTask interface */

	public boolean isTaskBreaked()
	{
		return closed;
	}

	/* protected: reports processor */

	protected void  processShuntReport(SelfShuntReport report)
	{
		service.processShuntReport(report);
	}

	/* protected: errors handling */

	/**
	 * General reaction on a protocol error:
	 * loses the protocol and quits the task.
	 */
	protected void  handleProtocolError(Throwable e)
	{
		this.closed = true;
	}

	protected void  handleProtocolOpenError(Throwable e)
	{
		logProtocolOpenError(e);
		handleProtocolError(e);
	}

	protected void  handleSendNextRequestError(Throwable e)
	{
		logSendNextRequestError(e);
		handleProtocolError(e);
	}

	protected void  handleProtocolCloseError(Throwable e)
	{
		logProtocolCloseError(e);
		handleProtocolError(e);
	}

	/* protected: logging */

	protected String getLog()
	{
		return SelfShuntPoint.LOG_SERVICE;
	}

	protected void   logProtocolOpenBefore()
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(),
		  " is opening the shunt protocol...");
	}

	protected void   logProtocolOpenSuccess()
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), logsig(),
		  ": the shunt protocol was opened successfully!");
	}

	protected void   logProtocolOpenError(Throwable e)
	{
		LU.E(getLog(), e,
		  logsig(), " got error while opening the shunt protocol!");
	}

	protected void   logSendNextRequestBefore()
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(),
		  " is about to send the next shunt request...");
	}

	protected void   logSendNextRequestSuccess()
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(),
		  " had successfully sent the next shunt request, ",
		  "it will continue: ", !this.closed);
	}

	protected void   logSendNextRequestError(Throwable e)
	{
		LU.E(getLog(), e,
		  logsig(), " got error while sending the next ",
		  "request via the shunt protocol!");
	}

	protected void   logProtocolCloseBefore()
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), logsig(),
		  "%s: the shunt protocol was closed successfully!");
	}

	protected void   logProtocolCloseSuccess()
	{
		if(!LU.isT(getLog())) return;
	}

	protected void   logProtocolCloseError(Throwable e)
	{
		LU.E(getLog(), e,
		  logsig(), " got error while closing the shunt protocol!");
	}

	protected String logsig()
	{
		final String LS = "SeSh-Srv ";
		String       ls = service.getServiceInfo().getServiceSignature();

		return new StringBuilder(LS.length() + ls.length()).
		  append(LS).append(ls).toString();
	}

	/* protected: shunt task parameters */

	/**
	 * Self Shunt Service.
	 */
	protected final SelfShuntService service;

	/**
	 * The protocol to instance invoke the shunts.
	 */
	protected final SeShProtocol protocol;

	protected Interruptor            interruptor;

	/* protected: shunt task state */

	/**
	 * Indicates that the protocol was already
	 * opened on the first cycle.
	 */
	protected boolean                opened;

	/**
	 * Indicates that the protocol is closed.
	 * May be set only from the task's thread.
	 */
	protected volatile boolean       closed;
}
