package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.services.Servicer;

/* com.tverts: self-shunts (core, protocol, reports) */

import com.tverts.shunts.SelfShunt;
import com.tverts.shunts.SelfShuntCtx;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;
import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolWeb;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestGroups;
import com.tverts.shunts.protocol.SeShResponse;
import com.tverts.shunts.protocol.SeShResponseBase;
import com.tverts.shunts.reports.SeShReportConsumer;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Self-Shunting Service is to invoke (Shunt Protocols)
 * and handle the requests coming from the Shunt income
 * servlet (actually, a filter).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SelfShuntService extends ServiceBase
{
	/* public: Service interface */

	public void         init(Servicer servicer)
	{
		super.init(servicer);

		//?: {has no handler configured}
		if(this.handler == null)
			throw new IllegalArgumentException(String.format(
			  "%s has no Requests Handler strategy configured!", logsig()
			));

		//?: {has no reports consumer configured}
		if(this.consumer == null)
			throw new IllegalArgumentException(String.format(
			  "%s has no Reports Consuming strategy configured!", logsig()
			));

		//~: log the shunts existing
		logShuntsExisting();

		//~: log the groups existing
		logGroupsExisting();
	}


	/* public: SelfShuntService (shunts) interface */

	/**
	 * Executes Self Shunt Request coming from the service
	 * (this service) via HTTP, JMS or else media.
	 */
	public SeShResponse executeRequest(SeShRequest request)
	{
		//?: {the request is undefined}
		if(request == null) throw new IllegalArgumentException(
		  "Self-Shunt Request argument is undefined!");

		//?: {request has no Context UID}
		if(request.getContextUID() == null)
			throw new IllegalStateException(String.format(
			  "Self-Shunt Request with key [%s] has no Context UID!",
			  request.getSelfShuntKey()
			));

		//~: obtain the context
		SelfShuntCtx ctx = getContext(request.getContextUID());
		if(ctx == null) throw new IllegalStateException(String.format(
		  "Self-Shunt Request with key [%s] refers Context " +
		  "by UID [%s] that is doesn't exist!",

		  request.getSelfShuntKey(), request.getContextUID()
		));

		//~: set the context to the point
		SelfShuntPoint.getInstance().setContext(ctx);

		//~: execute the request
		try
		{
			//!: invoke handler strategy
			SeShResponse res = handler.handleShuntRequest(request);

			//?: {handler can't take the request}
			if(res == null) throw new IllegalArgumentException(String.format(
			  "Self Shunt Service can't execute the request of " +
			  "the class '%s' having the key '%s'!",

			  request.getClass().getName(), request.getSelfShuntKey()
			));

			return res;
		}
		catch(Throwable e)
		{
			SeShResponseBase response =
			  new SeShResponseBase(request);

			response.setSystemError(e);
			return response;
		}
		finally
		{
			//~: clear the context from the point
			SelfShuntPoint.getInstance().setContext(null);
		}
	}


	/* public: Service interface */

	public void         service(Event event)
	{
		if(!(event instanceof SelfShuntEvent))
			return;

		//~: create the protocol
		SeShProtocol protocol = createProtocol(event);

		//?: {the event is not supported} do nothing
		if(protocol == null) return;

		//~: create the context
		SelfShuntCtx ctx = createContext((SelfShuntEvent)event);
		saveContext(ctx);

		//~: execute the protocol
		try
		{
			createProtocolTask(ctx, protocol).run();
		}
		finally
		{
			//~: remove the context
			removeContext(ctx.getUID());
		}
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
	public void setRequestsHandler(SeShRequestsHandler handler)
	{
		if(handler == null) throw new IllegalArgumentException();
		this.handler = handler;
	}

	/**
	 * The strategy of processing the Self Shut Reports
	 * installed into the service.
	 *
	 * Without this strategy the service is not active.
	 */
	public void setReportConsumer(SeShReportConsumer consumer)
	{
		if(consumer == null) throw new IllegalArgumentException();
		this.consumer = consumer;
	}


	/* protected: protocols creation */

	protected SeShProtocol createProtocol(Event event)
	{
		//?: {shunt the groups given}
		if(event instanceof SelfShuntGroupsEvent)
		{
			SeShRequestGroups request = new SeShRequestGroups();
			List<String>      groups  = ((SelfShuntGroupsEvent)event).getGroups();

			//~: assign the groups
			request.setGroups(SU.a2a(
			  groups.toArray(new String[groups.size()])
			));

			//?: {has no groups defined}
			if(request.getGroups().length == 0)
				throw new IllegalArgumentException(String.format(
				  "%s has empty Self-Shunt Groups list!",
				  event.getClass().getSimpleName()
				));

			return new SeShProtocolWeb(request);
		}

		return null;
	}


	/* protected: processing & report handling */

	protected Runnable createProtocolTask(SelfShuntCtx ctx, SeShProtocol protocol)
	{
		return new ProtocolTask(ctx, protocol);
	}

	protected void     processShuntReport(SelfShuntReport report)
	{
		//?: {has report consumer installed} invoke it
		if(consumer != null)
			consumer.consumeReport(report);
	}


	/* protected: Self-Shunt Context handling */

	protected SelfShuntCtx createContext(SelfShuntEvent event)
	{
		SelfShuntCtx ctx = new SelfShuntCtx(
		  contextIdPrefix + contextIdNumber.incrementAndGet()
		);

		//~: domain
		ctx.setDomain(event.getDomain());

		//~: readonly
		if(event.isReadonly())
			ctx.setReadonly();

		return ctx;
	}

	protected void         saveContext(SelfShuntCtx ctx)
	{
		contexts.put(ctx.getUID(), ctx);
	}

	protected SelfShuntCtx getContext(String uid)
	{
		SelfShuntCtx ctx = contexts.get(uid);

		//?: {the context is not found}
		if(ctx == null)
			throw new IllegalStateException(String.format(
			  "No Self-Shunt Context with UID [%s] present!", uid
			));

		return ctx;
	}

	protected void         removeContext(String uid)
	{
		contexts.remove(uid);
	}


	/* public: ProtocolTask implementation  */

	public class ProtocolTask implements Runnable
	{
		/* public: constructor */

		public ProtocolTask(SelfShuntCtx context, SeShProtocol protocol)
		{
			this.context  = context;
			this.protocol = protocol;
		}


		/* public: Runnable interface */

		public void    run()
		{
			//?: {the service | task is breaked} quit
			if(this.closed) return;

			SelfShuntReport report = new SelfShuntReport();

			//0: open the protocol
			try
			{
				logProtocolOpenBefore();
				protocol.openProtocol(context);
				logProtocolOpenSuccess();
			}
			catch(Throwable e)
			{
				handleProtocolOpenError(e);
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
		 * The Self-Shunt Context of the execution.
		 */
		protected final SelfShuntCtx context;

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

	protected void   logShuntsExisting()
	{
		Set<String> shunts = SelfShuntPoint.getInstance().
		  getShuntsSet().enumShunts();

		LU.I(getLog(), " following Self-Shunts found: [\n\n",
		  SU.scat("\n", shunts), "\n\n]\n"
		);
	}

	protected void   logGroupsExisting()
	{
		TreeSet<String> groups = new TreeSet<String>();

		//c: for all the shunts registered
		for(SelfShunt shunt : SelfShuntPoint.getInstance().getShuntsSet().listShunts())
			groups.addAll(Arrays.asList(SU.a2a(shunt.getGroups())));

		LU.I(getLog(), logsig(), " following Self-Shunt Groups found: [\n\n",
		  SU.scat("\n", groups), "\n\n]\n"
		);
	}


	/* protected: strategies of the service  */

	protected SeShRequestsHandler handler;
	protected SeShReportConsumer  consumer;


	/* private: runtime state of the service */

	private Map<String, SelfShuntCtx> contexts        =
	  Collections.synchronizedMap(new HashMap<String, SelfShuntCtx>(1));

	private final String              contextIdPrefix =
	  Long.toHexString(System.currentTimeMillis()).toUpperCase() + "-";

	private final AtomicLong          contextIdNumber =
	  new AtomicLong();
}