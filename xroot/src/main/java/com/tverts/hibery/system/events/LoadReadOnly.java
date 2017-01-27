package com.tverts.hibery.system.events;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/* Hibernate Persistence Layer */

import org.hibernate.event.spi.PostLoadEvent;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This special listener sets read-only
 * for the entities had been loaded.
 *
 * @author anton.baukin@gmail.com
 */
public class LoadReadOnly extends OnHiberEventBase
{
	/* public: constructor */

	public LoadReadOnly(Class... classes)
	{
		this(Arrays.asList(classes));
	}

	public LoadReadOnly(Collection<Class> classes)
	{
		this.classes = orderClasses(classes);
	}

	protected LoadReadOnly()
	{
		this.classes = null;
	}


	/* public: LoadReadOnly interface */

	public Map<Class, Integer> getStat()
	{
		return stat;
	}


	/* protected: LoadReadOnly interface */

	protected void    onPostLoad(PostLoadEvent e)
	{
		//~: update the statistics
		updateStat(e);

		//?: {is read-only}
		if(isReadOnly(e))
			setReadOnly(e);
	}

	protected void    updateStat(PostLoadEvent e)
	{
		Class   c = HiberPoint.type(e.getEntity());
		Integer n = stat.get(c);
		stat.put(c, (n == null)?(1):(n + 1));
	}

	protected void    setReadOnly(PostLoadEvent e)
	{
		e.getSession().getPersistenceContext().
		  setReadOnly(e.getEntity(), true);
	}

	protected boolean isReadOnly(PostLoadEvent e)
	{
		return isReadOnly(HiberPoint.type(e.getEntity()));
	}

	protected boolean isReadOnly(Class cls)
	{
		return (classes != null) &&
		  (Arrays.binarySearch(classes, cls.getName()) >= 0);
	}


	/* private: create support */

	private static String[] orderClasses(Collection<Class> classes)
	{
		HashSet<Class> cs = new HashSet<Class>(EX.assertn(classes));
		EX.asserte(cs, "Load match classes must be specified!");

		String[]       rs = new String[cs.size()];
		int i = 0; for(Class c : cs) rs[i++] = c.getName();
		Arrays.sort(rs);

		return rs;
	}


	/* protected: the classes */

	protected final String[]            classes;

	protected final Map<Class, Integer> stat =
	  new HashMap<Class, Integer>(101);
}