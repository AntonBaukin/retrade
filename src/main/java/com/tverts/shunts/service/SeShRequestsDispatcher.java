package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* com.tverts: shunt protocol */

import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

/* com.tverts: support */

import com.tverts.support.OU.ClassPredicate;
import static com.tverts.support.OU.selectClassOrInterface;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;

/**
 * Delegates processing the income requests
 * to the referencing dispatchers mapped
 * by the FQN of the request classes.
 *
 * This implementation also handles the subclasses
 * and the interfaces of the classes mapped.
 *
 * The order of mapping is preserved.
 *
 * @author anton.baukin@gmail.com
 */
public class      SeShRequestsDispatcher
       implements SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public boolean      canHandleRequest(SeShRequest request)
	{
		return (selectHandler(request) != null);
	}

	public SeShResponse handleShuntRequest(SeShRequest request)
	{
		SeShRequestsHandler handler = selectHandler(request);

		//?: {strange, the handler does not exist}
		if(handler == null) throw new IllegalStateException(String.format(
		  "Self Shunts Request Dispatcher can't find handler for the " +
		  "request of class '%s'. (System error.)",

		  request.getClass().getName()));

		return handler.handleShuntRequest(request);
	}

	/* public: SeShRequestsDispatcher interface */

	public Map<Class, SeShRequestsHandler>
	            getHandlersMap()
	{
		return handlers;
	}

	public void setHandlersMap(Map<Class, SeShRequestsHandler> handlers)
	{
		if((handlers == null) || handlers.isEmpty())
			this.handlers = Collections.emptyMap();
		else
			this.handlers = wrapHandlersMap(
			  new LinkedHashMap<Class, SeShRequestsHandler>(handlers));
	}

	public void setHandlers(Map<String, SeShRequestsHandler> handlers)
	{
		if((handlers == null) || handlers.isEmpty())
			this.handlers = Collections.emptyMap();
		else
			this.handlers = wrapHandlersMap(
			  createHandlersMap(handlers));
	}

	/* protected: handlers selecting */

	protected SeShRequestsHandler selectHandler(SeShRequest request)
	{
		if(request == null) throw new IllegalArgumentException(
		  "Self Shunts Requests Dispatcher can't provide a handler " +
		  "for a null request!");

		return getHandlersMap().get(selectClassOrInterface(
		  request.getClass(), createHandlersSelector(request)));
	}

	protected ClassPredicate      createHandlersSelector(SeShRequest request)
	{
		return new HandlersSelector(request);
	}

	/**
	 * Selects {@link SeShRequestsHandler} by the class
	 * of a {@link SeShRequest} it is registered for.
	 */
	protected class HandlersSelector implements ClassPredicate
	{
		/* public: constructor */

		public HandlersSelector(SeShRequest request)
		{
			this.request = request;
		}

		/* public: ClassPredicate interface */

		public boolean isThatClass(Class c)
		{
			SeShRequestsHandler res = getHandlersMap().get(c);
			return (res != null) && res.canHandleRequest(request);
		}

		/* protected: the request */

		protected final SeShRequest request;
	}

	/* protected: handlers mapping */

	protected Map<Class, SeShRequestsHandler>
	            createHandlersMap(Map<String, SeShRequestsHandler> handlers)
	{
		Map<Class, SeShRequestsHandler> res =
		  new LinkedHashMap<Class, SeShRequestsHandler>(handlers.size());
		ClassLoader                     cll =
		  Thread.currentThread().getContextClassLoader();

		for(Map.Entry<String, SeShRequestsHandler> e : handlers.entrySet())
			if(!sXe(e.getKey())) try
			{
				res.put(cll.loadClass(s2s(e.getKey())), e.getValue());
			}
			catch(ClassNotFoundException ee)
			{
				throw new RuntimeException(ee);
			}

		return res;
	}

	protected Map<Class, SeShRequestsHandler>
	            wrapHandlersMap(Map<Class, SeShRequestsHandler> handlers)
	{
		return Collections.unmodifiableMap(
		  Collections.synchronizedMap(handlers));
	}

	/* private: the mapping */

	private Map<Class, SeShRequestsHandler> handlers =
	  Collections.emptyMap();
}