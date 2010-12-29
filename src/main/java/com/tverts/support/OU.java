package com.tverts.support;

/* standard Java classes */

import java.io.Serializable;

/**
 * Various utility functions for objects.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class OU
{
	/* public: runnable wrappers  */

	public Runnable unwrap(Runnable task)
	{
		while(task instanceof RunnableWrapper)
			task = ((RunnableWrapper)task).getWrappedTask();
		return task;
	}

	/* public: object clones */

	/**
	 * Makes a shallow copy with standard
	 * {@link Object#clone()} invoked via
	 * the reflection.
	 */
	public static Object clone(Cloneable obj)
	{
		if(obj == null)
			return null;

		try
		{
			return obj.getClass().getMethod("clone").invoke(obj);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes a deep clone via serialization.
	 */
	public static Object cloneDeep(Serializable obj)
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

		return res;
	}

	public static Object cloneBest(Object obj)
	{
		if(obj == null)
			return null;

		if(obj instanceof Serializable)
			return cloneDeep((Serializable)obj);

		if(obj instanceof Cloneable)
			return clone((Cloneable)obj);

		throw new IllegalArgumentException(
		  "don't know how to clone class " + obj.getClass().getName());
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
}
