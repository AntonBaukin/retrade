package com.tverts.event;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.misc.ClassLevels;
import com.tverts.support.misc.ClassLevels.Visitor;


/**
 * Dispatches the events to the registered
 * reactors by the types of the events.
 *
 * The implementation uses {@link ClassLevels}
 * to also deliver the events by their ancestor
 * types and the interfaces. Ancestor class or
 * interface always goes before the distinct type
 * (class levels down() invocation).
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class EventsDisp implements Reactor, ReactorRef
{
	/* Reactor */

	public void react(Event event)
	{
		//~: access the reactors track
		Reactor[] track = cache.get(event.getClass());
		if(track == null)
			cache.put(event.getClass(), track = track(event.getClass()));

		//~: invoke the track
		for(Reactor r : track)
			r.react(event);
	}


	/* Reactor Reference */

	public List<Reactor> dereferObjects()
	{
		return Collections.<Reactor> singletonList(this);
	}


	/* Events Dispatching Bean */

	public void setRegistry(List<EventReactor> registry)
	{
		Map<Class<? extends Event>, List<Reactor>> reactors =
		  new HashMap<>(registry.size());

		//~: populate the reactors mapping
		for(EventReactor p : registry)
		{
			List<Reactor> rs = reactors.get(EX.assertn(p.getKey()));
			if(rs == null) reactors.put(p.getKey(), rs = new ArrayList<>(2));
			rs.add(EX.assertn(p.getValue()));
		}

		//~: append the reactors
		for(Map.Entry<Class<? extends Event>, List<Reactor>> e : reactors.entrySet())
			this.reactors.put(e.getKey(),
			  e.getValue().toArray(new Reactor[e.getValue().size()]));
	}

	@SuppressWarnings("unchecked")
	public void setRegistryAsText(String text)
	{
		String[] a  = SU.s2a(text);
		EX.assertx((a.length % 2) == 0);

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		//~: collect the pairs
		List<EventReactor> ps = new ArrayList<>(a.length / 2);
		for(int i = 0;(i < a.length);i+=2) try
		{
			ps.add(new EventReactor(
				(Class<? extends Event>) cl.loadClass(a[i]),
				(Reactor) cl.loadClass(a[i+1]).newInstance()
			));
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}

		//~: append the reactors
		if(!ps.isEmpty())
			setRegistry(ps);
	}


	/* protected: reactors tracking  */

	protected Reactor[] track(Class<? extends Event> event)
	{
		final List<Reactor> track = new ArrayList<>(4);

		//~: trace the classes
		this.levels.down(event, new Visitor()
		{
			public boolean take(Class cls)
			{
				//~: invoke the reactors by that types
				Reactor[] rs = reactors.get(cls);
				if(rs != null)
					track.addAll(Arrays.asList(rs));
				return true;
			}
		});

		//~: the result
		return track.toArray(new Reactor[track.size()]);
	}


	protected final Map<Class, Reactor[]> reactors = new HashMap<>(17);

	/**
	 * Cache of the reactors for each event class received.
	 */
	protected final Map<Class<? extends Event>, Reactor[]> cache =
	  new ConcurrentHashMap<>(17);

	protected final ClassLevels levels = new ClassLevels();
}