package com.tverts.shunts.service;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;

/* com.tverts: genesis */

import com.tverts.genesis.GenesisDone;

/* com.tverts: self-shunts (core, protocol, reports) */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolReference;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;
import com.tverts.shunts.reports.SeShReportConsumer;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


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
 * @author anton.baukin@gmail.com
 */
public class SelfShuntService extends ServiceBase
{
	/* public: SelfShuntService (shunts) interface */

	/**
	 * Synchronously executes the Shunt Protocol
	 * given sending shunt requests to this service.
	 */
	public void         executeProtocol(SeShProtocol protocol)
	{
		createProtocolTask(protocol).run();
	}

	/**
	 * Executes Self Shunt Request coming from the service
	 * (this service) via HTTP, JMS or else media.
	 */
	public SeShResponse executeRequest(SeShRequest request)
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


	/* public: Service interface */

	public void     service(Event event)
	{
		if(event instanceof GenesisDone)
			service((GenesisDone) event);
	}

	public String[] depends()
	{
		if(SU.sXe(getGenesisService()))
			return null;

		return new String[] { getGenesisService() };
	}


	/* public: SelfShuntService (bean) interface */

	/**
	 * If you want this service to explicitly depend
	 * of Genesis Service, set those Service UID.
	 */
	public String getGenesisService()
	{
		return genesisService;
	}

	public void setGenesisService(String genesisService)
	{
		this.genesisService = genesisService;
	}

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
	public SeShRequestsHandler getRequestsHandler()
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
	public SeShReportConsumer getReportConsumer()
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
	public SeShProtocolReference getInitialTasks()
	{
		return initialTasks;
	}

	public void setInitialTasks(SeShProtocolReference initialTasks)
	{
		this.initialTasks = initialTasks;
	}


	/* protected: service work steps */

	protected void     service(GenesisDone event)
	{
		LU.I(getServicesLog(), logsig(), " recieved GenesisDone event from '",
		  event.getSourceService(), "'...");

		//?: {this is not a Genesis Service configured}
		if(!SU.sXe(getGenesisService()))
			if(!getGenesisService().equals(event.getSourceService()))
				return;

		//!: do the genesis of all the initial tasks
		SeShProtocolReference pref = getInitialTasks();
		if(pref == null) return;

		LU.I(getServicesLog(), logsig(), " starting initial Self-Shunts...");

		for(SeShProtocol p : pref.dereferObjects())
			this.executeProtocol(p);

		LU.I(getServicesLog(), logsig(), " initial Self-Shunts completed!");
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
			if(this.closed) return;

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
				opened = !this.closed; //<-- the error may be dealt
				if(this.closed) report.setSystemError(e);
			}

			//~: do the protocol cycle
			while(!this.closed) try
			{
				logSendNextRequestBefore();
				this.closed = !protocol.sendNextRequest();
				logSendNextRequestSuccess();
			}
			catch(Throwable e)
			{
				handleSendNextRequestError(e);
				if(this.closed) report.setSystemError(e);
			}

			boolean reported = false;

			//?: {the protocol must be closed} do it
			try
			{
				logProtocolCloseBefore();
				report   = protocol.closeProtocol();
				reported = true;
				logProtocolCloseSuccess(report);
			}
			catch(Throwable e)
			{
				handleProtocolCloseError(e);
				if(this.closed) report.setSystemError(e);
			}

			//?: {this.closed, has the report} handle it
			if(reported) try
			{
				processShuntReport(report);
			}
			catch(Throwable e)
			{
				report.setSystemError(e);
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


	/* protected: logging */

	protected String getLog()
	{
		return SelfShuntPoint.LOG_SERVICE.getName();
	}


	/* private: genesis service uid */

	private String                genesisService;


	/* private: strategies of the service  */

	private SeShRequestsHandler   requestsHandler;
	private SeShReportConsumer    reportConsumer;
	private SeShProtocolReference initialTasks;
}