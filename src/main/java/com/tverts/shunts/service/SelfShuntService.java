package com.tverts.shunts.service;

/* com.tverts: system services */

import com.tverts.system.services.QueueExecutorServiceBase;

/* com.tverts: shunts, shunt protocol, reports  */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolReference;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;
import com.tverts.shunts.reports.SeShReportConsumer;

/* com.tverts: support */

import static com.tverts.support.LO.LANG_RU;
import com.tverts.support.LU;

/**
 * This complex (active) service resolves all
 * the issues needed to query self shunting
 * requests and to actually invoke them in the
 * background thread.
 *
 * In the active phase this service starts
 * invoking the tests via HTTP protocol.
 * As a queue executor service this service
 * is blocked on empty queue of the shunt
 * protocols.
 *
 * The shunt protocols may be added at any time:
 * before activating the service, and when it
 * is already active (running).
 *
 * When this service is initialized it is able
 * to handle incoming shunt requests, both initial,
 * and the sequenced.
 *
 * The shunt service is thread-safe, active when it
 * is properly configured.
 *
 * This service is not designed to be a singleton only.
 * Several instances are allowed. But as it's behaviour
 * is controlled by the internal strategies, and with
 * the protocol abstraction existing this is seemed to
 * be not demanded in the most cases.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SelfShuntService
       extends QueueExecutorServiceBase
{
	/* public: ServiceBase interface */

	public boolean isActiveService()
	{
		return SelfShuntPoint.getInstance().isActive() &&
		  (getRequestsHandler() != null) &&
		  (getReportConsumer()  != null);
	}

	/* public: SelfShuntService primary interface */

	/**
	 * Adds the Shunt Protocol to the execution queue.
	 */
	public void enqueueProtocol(SeShProtocol protocol)
	{
		((DequeProvider)this.getTasksProvider()).
		  appendTask(createProtocolTask(protocol));
	}

	/**
	 * Executes Self Shunt Request coming from the service
	 * (this service) via HTTP, JMS or else media.
	 */
	public SeShResponse
	            executeRequest(SeShRequest request)
	{
		SeShRequestsHandler handler = getRequestsHandler();

		try
		{
			//?: {the request is undefined}
			if(request == null) throw new IllegalArgumentException(
			  "Self Shunt request argument is undefined!");

			//?: {there is no handler}
			if(handler == null) throw new IllegalStateException(
			  "Self Shunt Service has no request handling " +
			  "strategy installed!");

			//?: {handler can't take the request}
			if(!handler.canHandleRequest(request))
				throw new IllegalArgumentException(String.format(
				  "Self Shunt Service can't execute the request of " +
				  "the class '%s' having the key '%s'!",

				  request.getClass().getName(),
				  request.getSelfShuntKey()));

			//!: invoke handler strategy
			return handler.handleShuntRequest(request);
		}
		catch(Throwable e)
		{
			SeShBasicResponse response =
			  new SeShBasicResponse(request);

			response.setSystemError(e);
			return response;
		}
	}

	/* public: service configuration */

	/**
	 * An instance of {@link SeShRequestsHandler} strategy
	 * handling the incoming requests.
	 *
	 * By default this instance is not defined, hence the service
	 * is not active. It would not send shunt requests (to itself,
	 * via transport such as HTTP or JMS), and when income request
	 * will come, it would raise an illegal state exception.
	 *
	 * It is expected that here would be set an instance of class
	 * {@link SeShRequestsDispatcher}.
	 */
	public SeShRequestsHandler
	            getRequestsHandler()
	{
		return requestsHandler;
	}

	public void setRequestsHandler(SeShRequestsHandler handler)
	{
		this.requestsHandler = handler;
	}

	/**
	 * The strategy of processing the Self Shut Reports
	 * installed into the service.
	 *
	 * Without this strategy the service is not active.
	 */
	public SeShReportConsumer
	            getReportConsumer()
	{
		return reportConsumer;
	}

	public void setReportConsumer(SeShReportConsumer reportConsumer)
	{
		this.reportConsumer = reportConsumer;
	}

	/**
	 * Defines the root reference to the Shunt Protocols
	 * to enqueue right after the service is initialized
	 * and before it is activated. The reference may be
	 * not defined.
	 */
	public SeShProtocolReference
	            getInitialTasks()
	{
		return initialTasks;
	}

	public void setInitialTasks(SeShProtocolReference initialTasks)
	{
		this.initialTasks = initialTasks;
	}

	/* protected: processing & report handling */

	protected Runnable createProtocolTask(SeShProtocol protocol)
	{
		return new ProtocolTask(protocol);
	}

	protected void     processShuntReport(SelfShuntReport report)
	{
		SeShReportConsumer consumer = getReportConsumer();

		//?: {has report consumer installed} invoke it
		if(consumer != null)
			consumer.consumeReport(report);
	}

	/* protected: ProtocolTask implementation  */

	protected class ProtocolTask implements Runnable
	{
		/* public: constructor */

		public ProtocolTask(SeShProtocol protocol)
		{
			this.protocol = protocol;
		}

		/* public: Runnable interface */

		public void    run()
		{
			//?: {the service | task is breaked} quit
			if(breaked || closed) return;

			SelfShuntReport report = new SelfShuntReport();
			boolean         opened;

			//0: open the protocol
			try
			{
				logProtocolOpenBefore();
				protocol.openProtocol();
				opened = true;
				logProtocolOpenSuccess();
			}
			catch(Throwable e)
			{
				handleProtocolOpenError(e);
				opened = !closed; //<-- the error may be dealt
				if(closed) report.setSystemError(e);
			}

			//?: {hadn't opened the protocol} quit now
			if(!opened) try
			{
				logProtocolFinishBefore(report);
				protocol.finishProtocol(report);
				logProtocolFinishSuccess(report);
			}
			catch(Throwable e)
			{
				logProtocolFinishError(e);
			}
			finally
			{
				return;
			}

			//~: do the protocol cycle
			while(!breaked && !closed) try
			{
				logSendNextRequestBefore();
				closed = !protocol.sendNextRequest();
				logSendNextRequestSuccess();
			}
			catch(Throwable e)
			{
				handleSendNextRequestError(e);
				if(closed) report.setSystemError(e);
			}

			boolean reported = false;

			//?: {the protocol must be closed} do it
			if(opened) try
			{
				logProtocolCloseBefore();
				report   = protocol.closeProtocol();
				reported = true;
				logProtocolCloseSuccess(report);
			}
			catch(Throwable e)
			{
				handleProtocolCloseError(e);
				if(closed) report.setSystemError(e);
			}

			//?: {closed, has the report} handle it
			if(!breaked && reported) try
			{
				processShuntReport(report);
			}
			catch(Throwable e)
			{
				report.setSystemError(e);
			}

			//!: finish the protocol
			try
			{
				logProtocolFinishBefore(report);
				protocol.finishProtocol(report);
				logProtocolFinishSuccess(report);
			}
			catch(Throwable e)
			{
				logProtocolFinishError(e);
			}
		}

		/* protected: errors handling */

		protected void handleProtocolOpenError(Throwable e)
		{
			logProtocolOpenError(e);
			this.closed = true;
		}

		protected void handleSendNextRequestError(Throwable e)
		{
			logSendNextRequestError(e);
			this.closed = true;
		}

		protected void handleProtocolCloseError(Throwable e)
		{
			logProtocolCloseError(e);
			this.closed = true;
		}

		/* protected: logging */

		protected void logProtocolOpenBefore()
		{
			if(!LU.isT(getLog())) return;

			LU.T(getLog(), logsig(),
			  " is opening the shunt protocol...");
		}

		protected void logProtocolOpenSuccess()
		{
			if(!LU.isD(getLog())) return;

			LU.D(getLog(), logsig(),
			  ": the shunt protocol was opened successfully!");
		}

		protected void logProtocolOpenError(Throwable e)
		{
			LU.E(getLog(), e, logsig(),
			  " got error while opening the shunt protocol!");
		}

		protected void logSendNextRequestBefore()
		{
			if(!LU.isT(getLog())) return;

			LU.T(getLog(), logsig(),
			  " is about to send the next shunt request...");
		}

		protected void logSendNextRequestSuccess()
		{
			if(!LU.isT(getLog())) return;

			LU.T(getLog(), logsig(),
			  " had successfully sent the next shunt request, ",
			  "it will continue: ", !this.closed);
		}

		protected void logSendNextRequestError(Throwable e)
		{
			LU.E(getLog(), e,
			  logsig(), " got error while sending the next ",
			  "request via the shunt protocol!");
		}

		protected void logProtocolCloseBefore()
		{
			if(!LU.isT(getLog())) return;

			LU.T(getLog(), logsig(),
			  " is going to close the shunt protocol...");
		}

		protected void logProtocolCloseSuccess(SelfShuntReport report)
		{
			if(!LU.isD(getLog())) return;

			LU.D(getLog(), logsig(),
			  ": the shunt protocol was closed successfully!");
		}

		protected void logProtocolCloseError(Throwable e)
		{
			LU.E(getLog(), e, logsig(),
			  " got error while closing the shunt protocol!");
		}

		protected void logProtocolFinishBefore(SelfShuntReport report)
		{
			if(!LU.isT(getLog())) return;

			LU.T(getLog(), logsig(),
			  " is about to finish the shunt protocol...");
		}

		protected void logProtocolFinishSuccess(SelfShuntReport report)
		{
			if(!LU.isT(getLog())) return;

			LU.D(getLog(), logsig(),
			  ": the shunt protocol was finished successfully!");
		}

		protected void logProtocolFinishError(Throwable e)
		{
			LU.E(getLog(), e, logsig(),
			 " got error while finishing the shunt protocol!");
		}

		/* protected: the protocol */

		/**
		 * The protocol to invoke.
		 */
		protected final SeShProtocol protocol;

		/**
		 * Indicates that the protocol is closed.
		 * May be set only from the task's thread.
		 */
		protected volatile boolean   closed;
	}

	/* protected: StatefulServiceBase (state control) */

	/**
	 * Adds the initial protocols to the queue.
	 */
	protected void   afterInitService()
	{
		super.afterInitService();

		SeShProtocolReference pref = getInitialTasks();

		if(pref != null)
			for(SeShProtocol p : pref.dereferObjects())
				this.enqueueProtocol(p);
	}

	/* protected: logging */

	protected String getLog()
	{
		return SelfShuntPoint.LOG_SERVICE;
	}

	protected String logsig(String lang)
	{
		String one = LANG_RU.equals(lang)?
		  ("Сервис самошунтирования"):("Self Shunt Service");

		String two = getServiceName();
		if(two == null) two = "???";

		return String.format("%s '%s'", one, two);
	}

	/* private: strategies of the service  */

	private SeShRequestsHandler   requestsHandler;
	private SeShReportConsumer    reportConsumer;
	private SeShProtocolReference initialTasks;
}