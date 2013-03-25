package com.tverts.api.support;

/* standard Java classes */

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Collects properties of the entities
 * (beans) and uses them to deeply compare
 * the instances and to process the
 * properties.
 *
 * @author anton.baukin@gmail.com
 */
public class BeansHandler
{
	public static final BeansHandler INSTANCE =
	  new BeansHandler();


	/* public: comparison interface */

	@SuppressWarnings("unchecked")
	public boolean    differs(Object a, Object b)
	  throws Exception
	{
		if((a == null) || (b == null))
			return (a != b);

		if(!a.getClass().equals(b.getClass()))
			throw new IllegalArgumentException(String.format(
			  "Compared objects has differ classes: '%s' Vs '%s'!",
			  a.getClass().getName(), b.getClass().getName()
			));

		if(a instanceof Object[])
			return this.differs(
			  Arrays.asList((Object[])a),
			  Arrays.asList((Object[])b)
			);

		if(a instanceof Collection)
			return this.differs(
			  (Collection)a,
			  (Collection)b
			);

		if(a instanceof Iterable)
			return this.differs(
			  (Iterable)a,
			  (Iterable)b
			);

		if(a instanceof Map)
			return this.differs(
			  (Map)a,
			  (Map)b
			);

		//?: {the objects are equals by the standard}
		if(a.equals(b))
			return false;

		//?: {the objects are equals as Comparable} (all the standard data types)
		if(a instanceof Comparable)
			return (((Comparable)a).compareTo(b) != 0);

		return differBeans(a, b);
	}

	public boolean    differs(Collection a, Collection b)
	  throws Exception
	{
		return (a.size() != b.size()) ||
		  this.differs((Iterable)a, (Iterable)b);
	}

	@SuppressWarnings("unchecked")
	public boolean    differs(Iterable a, Iterable b)
	  throws Exception
	{
		Iterator ia = a.iterator();
		Iterator ib = b.iterator();

		while(true)
		{
			boolean ha = ia.hasNext();
			boolean hb = ib.hasNext();

			if(ha != hb)
				return true;

			//?: {no more elements} the collections are same
			if(!ha)
				return false;

			if(this.differs(ia.next(), ib.next()))
				return true;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean    differs(Map a, Map b)
	  throws Exception
	{
		if(a.size() != b.size())
			return true;

		Set ka = a.keySet();
		Set kb = b.keySet();

		if(this.differs(ka, kb))
			return true;

		for(Object k : ka)
			if(this.differs(a.get(k), b.get(k)))
				return true;

		return false;
	}

	/**
	 * Returns the bean information for the class given.
	 */
	public BeanInfo   getBeanInfo(Class type)
	  throws Exception
	{
		BeanInfo binfo = beans.get(type);

		if(binfo == null)
			beans.put(type, binfo = new BeanInfo(type));
		return binfo;
	}

	/**
	 * Calculates the number of objects and properties
	 * of the object and the nested ones marked with
	 * {@link XmlType} and {@link XmlRootElement}
	 * annotations.
	 *
	 * A Character Sequence forms the size of
	 * the length divided by 50.
	 */
	@SuppressWarnings("unchecked")
	public int        estimateXMLSize(Object obj)
	  throws Exception
	{
		int n = 0;

		if(obj == null)
			return 0;

		if(obj.getClass().isPrimitive())
			return 1;

		if(obj instanceof CharSequence)
			return 1 + ((CharSequence)obj).length() / 50;

		if(obj.getClass().isArray())
			obj = Arrays.asList((Object[])obj);

		//?: {is a collection}
		if(obj instanceof Iterable)
		{
			for(Object o : ((Iterable)obj))
				n += estimateXMLSize(o);

			return n;
		}

		//?: {is a map}
		if(obj instanceof Map)
		{
			for(Object e : ((Map)obj).entrySet())
			{
				n += estimateXMLSize(((Map.Entry)e).getKey());
				n += estimateXMLSize(((Map.Entry)e).getValue());
			}

			return n;
		}

		//?: {is an XML bean}
		Object xt = obj.getClass().getAnnotation(XmlType.class);
		Object rt = null;

		if(xt == null)
			rt = obj.getClass().getAnnotation(XmlRootElement.class);

		//?: {not a bean} count as 1
		if((xt == null) & (rt == null))
			return 1;

		//~: process the bean
		PropertyDescriptor[] pds = getBeanInfo(obj.getClass()).getProps();

		for(PropertyDescriptor pd : pds)
			n += estimateXMLSize(pd.getReadMethod().invoke(obj));

		return n;
	}


	/* protected: beans comparison */

	protected boolean differBeans(Object a, Object b)
	  throws Exception
	{
		PropertyDescriptor[] pds =
		  getBeanInfo(a.getClass()).getProps();

		for(PropertyDescriptor pd : pds)
		{
			Method gm = pd.getReadMethod();
			Object va = gm.invoke(a);
			Object vb = gm.invoke(b);

			if(this.differs(va, vb))
				return true;
		}

		return false;
	}


	/* private: support structures */

	private Map<Class, BeanInfo> beans =
	  Collections.synchronizedMap(new HashMap<Class, BeanInfo>(17));
}