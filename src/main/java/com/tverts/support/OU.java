package com.tverts.support;

/* standard Java classes */

import java.io.Serializable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/* com.tverts: objects */

import com.tverts.objects.RunnableWrapper;
import com.tverts.objects.RunnableInterruptible;

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

	@SuppressWarnings("unchecked")
	public static <O> O  cloneBest(O obj)
	{
		if(obj == null)
			return null;

		if(obj instanceof Serializable)
			return (O)cloneDeep((Serializable)obj);

		if(obj instanceof Cloneable)
			return (O)clone((Cloneable)obj);

		throw new IllegalArgumentException(
		  "don't know how to clone class " + obj.getClass().getName());
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

	/* public: logging support */

	public static String sig(Object obj)
	{
		if(obj == null) return "null";

		return String.format(
		  "%s#%d",
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
}
