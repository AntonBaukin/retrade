package com.tverts.shunts.service;

/* com.tverts: objects */

import com.tverts.objects.ObjectFactory;

/* com.tverts: system services */

import com.tverts.system.services.CycledTaskServiceBase;

/* com.tverts: shunts, shunt protocol, reports  */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntReport;

import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

import com.tverts.shunts.reports.SeShReportConsumer;

/* com.tverts: support */

import static com.tverts.support.LO.LANG_RU;

/**
 * This complex (active) service resolves all
 * the tasks needed to query self shunting
 * requests and to actually invoke them.
 *
 * In the active phase this service starts
 * invoking the tests via HTTP protocol.
 *
 * When this service is initialized it is able
 * to handle incoming shunt requests, both initial
 * and the actual.
 *
 * The shunt service is thread-safe, active when it
 * is properly configured.
 *
 * This service is not designed to be a singleton only.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SelfShuntService
       extends CycledTaskServiceBase
{
	/* public: ServiceBase interface */

	public boolean isActiveService()
	{
		return SelfShuntPoint.getInstance().isActive() &&
		  (getRequestsHandler() != null) &&
		  (getProtocolFactory() != null) &&
		  (getReportConsumer()  != null);
	}

	/* public: requests execution */

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
	 * An instance of {@link SeShRequestsHandler} strategy of
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
	 * Protocol factory implements strategy of sending
	 * a sequence of shunt requests to the server (this
	 * server). An instance of protocol is created for
	 * each activity task (thread) of this service.
	 *
	 * At the present time a service instance can have
	 * only one protocol instance, hence one task thread.
	 *
	 * To shunt the system through different mediad
	 * (such as HTTP, or JMS)
	 *
	 * Without this strategy the service is not active.
	 */
	public ObjectFactory<SeShProtocol>
	            getProtocolFactory()
	{
		return protocolFactory;
	}

	public void setProtocolFactory(ObjectFactory<SeShProtocol> pf)
	{
		this.protocolFactory = pf;
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

	/* public: task & protocol support */

	public SeShProtocol createProtocolInstance()
	{
		if(getProtocolFactory() == null)
			throw new IllegalStateException(String.format(
			  "%s has no Protocol Factory instance provided " +
			  "with the configutation!", logsig()));

		SeShProtocol protocol = getProtocolFactory().
		  createInstance(SeShProtocol.class);

		if(protocol == null)
			throw new IllegalStateException(String.format(
			  "%s couldn't create Protocol instance " +
			  "invoking the Factory!", logsig()));

		return protocol;
	}

	public void         processShuntReport(SelfShuntReport report)
	{
		SeShReportConsumer consumer = getReportConsumer();

		//?: {has report consumer installed} invoke it
		if(consumer != null)
			consumer.consumeReport(report);
	}

	/* protected: shunt service task implementation */

	/**
	 * Creates {@link SelfShuntServiceTask} instance.
	 */
	protected Runnable  createTask()
	{
		return new SelfShuntServiceTask(this);
	}

	/* protected: logging */

	protected String    logsig(String lang)
	{
		String one = LANG_RU.equals(lang)?
		  ("Сервис самошунтирования"):("Self Shunt Service");

		String two = getServiceName();
		if(two == null) two = "???";

		return String.format("%s '%s'", one, two);
	}

	/* private: strategies of the service  */

	private SeShRequestsHandler         requestsHandler;
	private ObjectFactory<SeShProtocol> protocolFactory;
	private SeShReportConsumer          reportConsumer;
}