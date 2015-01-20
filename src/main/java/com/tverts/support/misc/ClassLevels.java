package com.tverts.support.misc;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This algorithm takes a Class arguments.
 *
 * It builds the levels of hierarchy: each
 * class or interface on each i-level is
 * i-ancestor of the argument class.
 *
 * You may go up or down by that levels.
 * The implementation is thread-safe.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public final class ClassLevels
{
	/* Classes Tree */

	public static interface Visitor
	{
		/**
		 * Invoked on each class node traversed.
		 * Returns false to stop the traversing.
		 */
		public boolean take(Class cls);
	}

	/**
	 * Traverses the classes tree starting from
	 * Object class down by the hierarchy levels.
	 *
	 * Each class or interface on each hierarchy
	 * level is invoked in unpredicted order.
	 *
	 * Each class or interface is visited only once!
	 * Argument class is always invoked the last.
	 */
	public void down(Class last, Visitor v)
	{
		EX.assertn(last);
		EX.assertn(v);

		//?: {didn't get the way}
		Class[] way = downs.get(last);
		if(way == null)
			downs.put(last, way = godown(last));

		//~: invoke the way
		for(Class c : way)
			if(!v.take(c))
				return;
	}

	private Map<Class, Class[]> downs = new ConcurrentHashMap<>(7);

	/**
	 * Traverses the classes tree starting from the
	 * class given up by the hierarchy levels.
	 *
	 * Each class or interface on a hierarchy level
	 * is invoked in unpredicted order.
	 *
	 * Each class or interface is visited only once!
	 * Starting class is always invoked the first,
	 * Object class is the last.
	 */
	public void up(Class start, Visitor v)
	{
		EX.assertn(start);
		EX.assertn(v);

		//?: {didn't get the way}
		Class[] way = ups.get(start);
		if(way == null)
			ups.put(start, way = goup(start));

		//~: invoke the way
		for(Class c : way)
			if(!v.take(c))
				return;
	}

	private Map<Class, Class[]> ups = new ConcurrentHashMap<>(7);


	/* private: traversing */

	private Class[] godown(Class last)
	{
		//~: trace the levels
		List<List<Class>> lvs = new ArrayList<>(4);
		levels(last, lvs);

		//~: trace them reversed
		Collections.reverse(lvs);
		return trace(lvs);
	}

	private Class[] goup(Class start)
	{
		//~: trace the levels
		List<List<Class>> lvs = new ArrayList<>(4);
		levels(start, lvs);

		//~: trace them directly
		return trace(lvs);
	}

	private Class[] trace(List<List<Class>> levels)
	{
		//~: exclude set
		Set<Class> ex = new LinkedHashSet<>(11);

		//~: trace
		for(List<Class> lv : levels)
			for(Class c : lv)
				ex.add(c);

		return ex.toArray(new Class[ex.size()]);
	}

	private void    levels(Class cls, List<List<Class>> levels)
	{
		//~: current level, level accumulation
		HashSet<Class> lv = new HashSet<>(3);
		HashSet<Class> la = new HashSet<>(3);
		lv.add(cls);

		//c: while level exists
		while(!lv.isEmpty())
		{
			//~: add it
			levels.add(new ArrayList<Class>(lv));

			//~: accumulate the new level
			la.clear(); for(Class c : lv)
			{
				//?: {has super-class not Object}
				if((c.getSuperclass() != null) && !Object.class.equals(c.getSuperclass()))
					la.add(c.getSuperclass());

				//c: for each super-interface
				la.addAll(Arrays.asList(c.getInterfaces()));
			}

			//~: swap them
			HashSet<Class> lx = la; la = lv; lv = lx;
		}

		//~: add Object as the last always
		levels.add(Collections.singletonList((Class) Object.class));
	}
}