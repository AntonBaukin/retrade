package com.tverts.endure.msg;

/* Java */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * A collection of Message adapters mapped
 * by their class names.
 *
 * Each adapter must be a stateless instance
 * collecting the required data from the given
 * Message instances only.
 *
 * Adapter must be thread-safe (reentable).
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class MsgAdapters
{
	/* Singleton */

	public static final MsgAdapters INSTANCE =
	  new MsgAdapters();


	/* Adapters Access */

	public Object adapter(String cls)
	{
		Object a = adapters.get(cls);
		if(a != null) return a;

		try
		{
			adapters.put(cls, a = Thread.currentThread().
			  getContextClassLoader().loadClass(cls).newInstance());
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error creating Message Adapter of class [", cls, "]!");
		}

		return a;
	}

	private final Map<String, Object> adapters =
	  new ConcurrentHashMap<>(17);


	/* Support */

	public static Object adapter(Message msg)
	{
		return (msg.getAdapter() == null)?(null):
		  INSTANCE.adapter(msg.getAdapter());
	}

	public static String oxSearch(Message msg)
	{
		Object a = adapter(msg);

		//?: {adapter supports ox-search}
		if(a instanceof MsgSearch)
			return ((MsgSearch)a).getOxSearch(msg);

		//?: {message has no title}
		return SU.sXe(msg.getTitle())?(null):SU.oxtext(msg.getTitle());
	}
}