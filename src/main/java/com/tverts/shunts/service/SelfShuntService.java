package com.tverts.shunts.service;

/* com.tverts: objects */

import com.tverts.objects.ObjectFactory;
import com.tverts.objects.RunnableInterruptible;

/* com.tverts: system services */

import com.tverts.system.services.BreakingTask;
import com.tverts.system.services.CycledTaskServiceBase;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShProtocol;
import com.tverts.shunts.protocol.SeShProtocolError;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

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

	/* public: service configuration interface  */

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

	/* protected: shunt service task implementation */

	/**
	 * Creates {@link ShuntServiceTask} instance.
	 */
	protected Runnable   createTask()
	{
		if(getProtocolFactory() == null)
			throw new IllegalStateException(
			  "Self Shunt Service has no Protocol Factory instance " +
			  "provided with the configutation!");

		SeShProtocol p = getProtocolFactory().createInstance(SeShProtocol.class);

		if(p == null)
			throw new IllegalStateException(
			  "Self Shunt Service couldn't create Protocol instance " +
			  "invoking the Factory!");

		return new ShuntServiceTask(p);
	}

	protected class      ShuntServiceTask
	          implements RunnableInterruptible, BreakingTask

	{
		/* public: constructor */

		public ShuntServiceTask(SeShProtocol protocol)
		{
			this.protocol = protocol;
		}

		/* public: Runnable interface */

		public void    run()
		{

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
		}

		public void    setInterruptor(Interruptor x)
		{

		}

		/* public: BreakingTask interface */

		public boolean isTaskBreaked()
		{
			return closed;
		}

		/* protected: shunts invocation protocol */

		/**
		 * The protocol to instance incoke the shunts.
		 */
		protected final SeShProtocol protocol;

		/**
		 * Indicates that the protocol is closed.
		 * May be set only from the task's thread.
		 */
		protected volatile boolean   closed;
	}

	/* private: strategies of the service  */

	private SeShRequestsHandler         requestsHandler;
	private ObjectFactory<SeShProtocol> protocolFactory;
}