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
 * @author anton.baukin@gmail.com
 */
public class MsgAdapters
{
	/* Singleton */

	public static final MsgAdapters INSTANCE =
	  new MsgAdapters();

	private MsgAdapters()
	{}


	/* Adapters Access */

	public Object adapter(String cls)
	{
		EX.asserts(cls, "Adapter class name is undefined!");

		//~: lookup in the cache
		Object a = adapters.get(cls);
		if(a != null) return a;

		//~: try to create an instance
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

	/**
	 * Returns all adapter instances of the message.
	 */
	public static Object[] adapters(Message msg)
	{
		EX.assertn(msg);
		if(msg.getAdapters() == null)
			return null;

		int i = 0, size = msg.getAdapters().size();
		if(size == 0) return null;

		Object[] res = new Object[size];
		for(String cls : msg.getAdapters())
			res[i++] = INSTANCE.adapter(cls);

		return res;
	}

	/**
	 * Searchers for the adapter implementing
	 * the interface given.
	 */
	@SuppressWarnings("unchecked")
	public static <A> A    adapter(Message msg, Class<A> iface)
	{
		EX.assertn(msg);
		EX.assertn(iface);

		if(msg.getAdapters() == null)
			return null;

		for(String cls : msg.getAdapters())
		{
			Object a = INSTANCE.adapter(cls);
			if(iface.isAssignableFrom(a.getClass()))
				return (A)a;
		}

		return null;
	}

	public static String   oxSearch(Message msg)
	{
		MsgSearch a = adapter(msg, MsgSearch.class);

		//?: {adapter supports ox-search}
		if(a != null)
			return ((MsgSearch)a).getOxSearch(msg);

		//?: {message has no title}
		return SU.sXe(msg.getTitle())?(null):SU.oxtext(msg.getTitle());
	}

	public static String   msgScript(Message msg)
	{
		MsgScript a = adapter(msg, MsgScript.class);

		//?: {adapter supports scripts generation}
		if(a != null)
			return ((MsgScript)a).makeScript(msg);

		return "";
	}
}