package com.tverts.support;

/* standard Java classes */

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;
import com.tverts.objects.ObjectAccessRef;
import com.tverts.objects.ObjectAccessTimedCache;
import com.tverts.objects.RunnableWrapper;
import com.tverts.objects.RunnableInterruptible;

/* com.tverts: support */

import com.tverts.support.streams.BigDecimalXMLEncoderPersistenceDelegate;


/**
 * Various utility functions for objects.
 *
 * @author anton.baukin@gmail.com
 */
public class OU
{
	/* public: runnable wrappers */

	public static Runnable unwrap(Runnable task)
	{
		while(task instanceof RunnableWrapper)
			task = ((RunnableWrapper)task).getWrappedTask();
		return task;
	}

	public static RunnableInterruptible
	                       interruptable(Runnable task)
	{
		while(task != null)
		{
			if(task instanceof RunnableInterruptible)
				return (RunnableInterruptible)task;

			task = !(task instanceof RunnableWrapper)?(null):
			  ((RunnableWrapper)task).getWrappedTask();
		}

		return null;
	}


	/* public: object clones */

	/**
	 * Makes a shallow copy with standard
	 * {@link Object#clone()} invoked via
	 * the reflection.
	 */
	@SuppressWarnings("unchecked")
	public static <O extends Cloneable> O
	                     clone(Cloneable obj)
	{
		if(obj == null)
			return null;

		try
		{
			return (O)obj.getClass().
			  getMethod("clone").invoke(obj);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes a deep clone via serialization.
	 */
	@SuppressWarnings("unchecked")
	public static <O extends Serializable> O
	                     cloneDeep(O obj)
	{
		Object res;

		try
		{
			//write the object
			java.io.ByteArrayOutputStream bos = new
			  java.io.ByteArrayOutputStream(256);
			java.io.ObjectOutputStream    oos = new
			  java.io.ObjectOutputStream(bos);

			oos.writeObject(obj);
			oos.close();

			//read it back
			java.io.ByteArrayInputStream  bis = new
			  java.io.ByteArrayInputStream(bos.toByteArray());
			java.io.ObjectInputStream     ois = new
			  java.io.ObjectInputStream(bis);

			res = ois.readObject();
			ois.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return (O)res;
	}

	public static <O> O  cloneStrict(O obj)
	{
		if(obj == null)
			return null;

		O res = cloneBest(obj);

		if(res == null)
			throw new IllegalArgumentException(String.format(
			  "Don't know how to clone the class '%s'!",
			  obj.getClass().getName()));

		return res;
	}

	@SuppressWarnings("unchecked")
	public static <O> O  cloneBest(O obj)
	{
		if(obj == null)
			return null;

		if(obj instanceof Cloneable)
			return (O)clone((Cloneable)obj);

		if(obj instanceof Serializable)
			return (O)cloneDeep((Serializable)obj);

		return null;
	}


	/* public: factories and object access */

	public static <O> ObjectAccess<O>
	                     timedCache(ObjectAccess<O> factory)
	{
		return new ObjectAccessTimedCache<O>(factory);
	}

	public static <O> ObjectAccess<O>
	                     timedCache(O ref)
	{
		return new ObjectAccessTimedCache<O>(new ObjectAccessRef<O>(ref));
	}


	/* public: classes and interfaces */

	public static interface ClassPredicate
	{
		/* public: ClassPredicate interface */

		public boolean isThatClass(Class c);
	}

	/**
	 * Starting from the class given, this search strategy
	 * tries to find the closest possible class or interface
	 * that matches the predicate given.
	 *
	 * It follows the levels of the inheritance using the true
	 * classes (not interfaces) as the first candidates to check.
	 *
	 * First, it checks the given class itself. Second, all it's
	 * own implemented interfaces (not inherited) by the class,
	 * plus, all the super interfaces of that interfaces.
	 *
	 * Note that this implementation does handle the inheritance
	 * within the interfaces: it checks not only the interfaces
	 * implemented in the hierarchy, but also the super interfaces
	 * of that interfaces.
	 *
	 * Finally, it continues the whole procedure with the superclass.
	 *
	 * Also note that the same interface may be checked several
	 * times in the case of a complex hierarchy.
	 */
	public static Class   selectClassOrInterface(Class c, ClassPredicate p)
	{
		//i: process the stack
		while(c != null)
		{
			//i.0: check the class itself
			if(p.isThatClass(c))
				return c;

			//i.1: check all the interfaces of the class
			for(Class i : getAllInterfaces(c))
				if(p.isThatClass(i))
					return i;

			//i.2: goto the superclass
			c = c.getSuperclass();
		}

		return null;
	}

	/**
	 * Returns the interfaces the class implements directly.
	 */
	public static Class[] getAllInterfaces(Class c)
	{
		LinkedHashSet<Class> set = new LinkedHashSet<Class>(3);
		LinkedList<Class>    stk = new LinkedList<Class>();

		stk.addLast(c);

		while(!stk.isEmpty())
		{
			Class i = stk.removeFirst();
			if(set.contains(i)) continue;
			set.add(i);
			stk.addAll(Arrays.asList(i.getInterfaces()));
		}

		set.remove(c);
		return set.toArray(new Class[set.size()]);
	}

	public static String  getSimpleNameLowerFirst(Class c1ass)
	{
		String        cn = c1ass.getSimpleName();
		StringBuilder sb = new StringBuilder(cn.length() + 1);
		boolean       lo = false;

		//~: append simple name
		sb.append(cn);

		//~: search for lower case characters
		for(int i = 0;(i < sb.length());i++)
		{
			char c = sb.charAt(i);

			//?: {not an uppercase}
			if(lo = (Character.toUpperCase(c) != c))
				break;
		}

		//?: {has lowercase characters} turn the first
		if(lo) sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));

		return sb.toString();
	}


	/* public: plain XML serialization */

	public static String  obj2xml(Object bean)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLEncoder            enc = new XMLEncoder(bos);

			enc.setPersistenceDelegate(BigDecimal.class,
			  BigDecimalXMLEncoderPersistenceDelegate.getInstance());

			enc.writeObject(bean);
			enc.close();

			return new String(bos.toByteArray(), "UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured while XML Encoding Java Bean of class " + cls(bean), e);
		}
	}

	public static Object  xml2obj(String xml)
	{
		if(xml == null) return null;

		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			XMLDecoder           enc = new XMLDecoder(bis);
			Object               res = enc.readObject();

			enc.close();
			return res;
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured while Decoding Java Bean from XML!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <O> O   xml2obj(String xml, Class<O> c1ass)
	{
		Object res = xml2obj(xml);

		if((res != null) && (c1ass != null) && !c1ass.isAssignableFrom(res.getClass()))
			throw new IllegalStateException(
			  "Can't cast XML Decoded instance of class '" + cls(res) + "' to the " +
			  "required class '" + c1ass.getName() + "'!");

		return (O)res;
	}


	/* public: logging support */

	/**
	 * TODO move all logging support from OU to LU
	 */
	public static String sig(Object obj)
	{
		if(obj == null) return "null";

		return String.format(
		  "%s@%d",
		  obj.getClass().getSimpleName(),
		  System.identityHashCode(obj)
		);
	}

	public static String cls(Object obj)
	{
		return cls((obj == null)?(null):(obj.getClass()));
	}

	public static String cls(Object obj, Class def)
	{
		return cls((obj == null)?(def):(obj.getClass()));
	}

	public static String cls(Class cls)
	{
		return (cls == null)?("undefined"):(cls.getName());
	}

	/**
	 * Asserts that that at least one of the given check
	 * classes is assignable from the first parameter.
	 * If no, {@link IllegalStateException} is raised.
	 */
	@SuppressWarnings("unchecked")
	public static void   assignable(Class cls, Class... checks)
	{
		if(cls != null) for(Class check : checks)
			//?: {the class is in the check list}
			if((check != null) && check.isAssignableFrom(cls))
				return;

		//?: {the target is not a requested class}
		throw new IllegalStateException(String.format(
		  "Class assertion failed: there is no class in the list %s " +
		  "that is assignable from the class '%s'!",

		  Arrays.asList(checks).toString(), OU.cls(cls)
		));
	}

	public static void   assignable(Object obj, Class... checks)
	{
		assignable((obj == null)?(null):(obj.getClass()), checks);
	}
}