package com.tverts.support;

/* Java */

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;
import com.tverts.objects.ObjectAccessRef;

/* com.tverts: support */

import com.tverts.support.streams.BigDecimalXMLEncoderPersistenceDelegate;
import com.tverts.support.streams.BytesStream;
import com.tverts.support.streams.Streams.NotCloseInput;
import com.tverts.support.streams.Streams.NotCloseOutput;


/**
 * Various utility functions for objects.
 *
 * @author anton.baukin@gmail.com
 */
public class OU
{
	/* Object Clones */

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
			throw EX.wrap(e);
		}
	}

	/**
	 * Makes a deep clone via serialization.
	 */
	@SuppressWarnings("unchecked")
	public static <O extends Serializable> O
	                     cloneDeep(O obj)
	{
		BytesStream bs = new BytesStream();
		bs.setNotClose(true);

		try
		{
			//~: write the object
			ObjectOutputStream os = new ObjectOutputStream(bs);

			os.writeObject(obj);
			os.close();

			//~: read it back
			ObjectInputStream  is = new ObjectInputStream(bs.inputStream());
			return (O) is.readObject();
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
		finally
		{
			bs.closeAlways();
		}
	}

	public static <O> O  cloneStrict(O obj)
	{
		return (obj == null)?(null):EX.assertn(cloneBest(obj),
		  "Don't know how to clone object of the class [", LU.cls(obj), "]!"
		);
	}

	@SuppressWarnings("unchecked")
	public static <O> O  cloneBean(O obj)
	{
		BytesStream bs = new BytesStream();
		bs.setNotClose(true);

		try
		{
			//~: write the object to XML
			obj2xml(bs, obj);

			//~: read it back
			return (O) xml2obj(bs.inputStream());
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
		finally
		{
			bs.closeAlways();
		}
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

		return cloneBean(obj);
	}


	/* Factories and Object Access */

	public static <O> ObjectAccess<O> permAcces(O ref)
	{
		return new ObjectAccessRef<>(ref);
	}


	/* Classes and Interfaces */

	public static interface ClassPredicate
	{
		/* ClassPredicate interface */

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
		LinkedHashSet<Class> set = new LinkedHashSet<>(3);
		LinkedList<Class>    stk = new LinkedList<>();

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


	/* Java Bean XML Serialization */

	public static String  obj2xml(Object bean)
	{
		try(BytesStream bs = new BytesStream())
		{
			OU.obj2xml(bs, bean);

			try
			{
				return new String(bs.bytes(), "UTF-8");
			}
			catch(Exception e)
			{
				throw EX.wrap(e);
			}
		}
	}

	/**
	 * Writes the bean object to the stream.
	 * The stream is not closed!
	 */
	public static void    obj2xml(OutputStream os, Object bean)
	{
		try
		{
			XMLEncoder enc = new XMLEncoder(new NotCloseOutput(os));

			enc.setPersistenceDelegate(BigDecimal.class,
			  BigDecimalXMLEncoderPersistenceDelegate.getInstance());

			enc.writeObject(bean);
			enc.close();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error occured while XML Encoding Java Bean of class [",
			  LU.cls(bean), "]!");
		}
	}

	public static Object  xml2obj(String xml)
	{
		try
		{
			return (xml == null)?(null):
			  OU.xml2obj(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <O> O   xml2obj(String xml, Class<O> cls)
	{
		Object res = OU.xml2obj(xml);

		if((res != null) && (cls != null) && !cls.isAssignableFrom(res.getClass()))
			throw EX.state("Can't cast XML Decoded instance of class [",
			  LU.cls(res), "] to the required class [", LU.cls(cls), "]!"
			);

		return (O)res;
	}

	/**
	 * Reads a bean object from the stream.
	 * The stream is not closed!
	 */
	public static Object  xml2obj(InputStream is)
	{
		try
		{
			XMLDecoder enc = new XMLDecoder(new NotCloseInput(is));
			Object     res = enc.readObject();

			enc.close();
			return res;
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured while Decoding Java Bean from XML!");
		}
	}

	@SuppressWarnings("unchecked")
	public static <O> O   xml2obj(InputStream is, Class<O> cls)
	{
		Object res = OU.xml2obj(is);

		if((res != null) && (cls != null) && !cls.isAssignableFrom(res.getClass()))
			throw EX.state("Can't cast XML Decoded instance of class [",
			  LU.cls(res), "] to the required class [", LU.cls(cls), "]!"
			);

		return (O)res;
	}


	/* Java Serialization */

	public static byte[]  obj2bytes(Object obj)
	{
		BytesStream bs = new BytesStream();
		bs.setNotClose(true);

		try
		{
			ObjectOutputStream os = new ObjectOutputStream(bs);

			os.writeObject(obj);
			os.close();

			return bs.bytes();
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured while Java-serializing object of class [",
			  LU.cls(obj), "]!");
		}
		finally
		{
			bs.closeAlways();
		}
	}

	public static byte[]  eobj2bytes(boolean cls, boolean gzip, Externalizable obj)
	{
		EX.assertn(obj);

		BytesStream bs = new BytesStream();
		bs.setNotClose(true);

		try
		{
			ObjectOutputStream os = new ObjectOutputStream(
			  (gzip)?(new GZIPOutputStream(bs)):(bs));

			if(cls) IO.cls(os, obj.getClass());
			obj.writeExternal(os);
			os.close();

			return bs.bytes();
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured while externalizing object of class [",
			  LU.cls(obj), "]!");
		}
		finally
		{
			bs.closeAlways();
		}
	}

	public static Object  bytes2obj(byte[] bytes)
	{
		try
		{
			ObjectInputStream is = new ObjectInputStream(
			  new ByteArrayInputStream(bytes));

			return is.readObject();
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occurred while reading Java-serialized object!");
		}
	}

	@SuppressWarnings("unchecked")
	public static <O> O   bytes2obj(byte[] bytes, Class<O> cls)
	{
		Object obj = OU.bytes2obj(bytes);

		if((obj != null) && (cls != null) && !cls.isAssignableFrom(obj.getClass()))
			throw EX.state("Deserialized object of class [",
			  LU.cls(cls), "], but expected [", LU.cls(obj), "]!"
			);

		return (O) obj;
	}

	public static <O> O   bytes2eobj(Class<O> cls, boolean gzip, byte[] bytes)
	{
		try
		{
			//~: create input stream
			InputStream xs = new ByteArrayInputStream(bytes);
			if(gzip) xs = new GZIPInputStream(xs);

			//~: create object stream
			ObjectInputStream is = new ObjectInputStream(xs);

			//~: create new instance
			O o = cls.newInstance();
			if(!(o instanceof Externalizable)) throw EX.state();

			//~: read the data
			((Externalizable)o).readExternal(is);

			return o;
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occurred while reading ",
			  "externalized object of class [", LU.cls(cls), "]!");
		}
	}

	/**
	 * Reads externalized object with class writing option set.
	 */
	@SuppressWarnings("unchecked")
	public static <O> O   bytes2eobj(boolean gzip, byte[] bytes)
	{
		try
		{
			//~: create input stream
			InputStream xs = new ByteArrayInputStream(bytes);
			if(gzip) xs = new GZIPInputStream(xs);

			//~: create object stream
			ObjectInputStream is = new ObjectInputStream(xs);

			//~: read the class
			Class cls = IO.cls(is);
			EX.assertx(Externalizable.class.isAssignableFrom(cls));

			//~: create new instance
			O o = (O) cls.newInstance();

			//~: read the data
			((Externalizable)o).readExternal(is);

			return o;
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occurred while reading ",
			  "externalized object (with class write option set)!");
		}
	}


	/* Class Checks */

	/**
	 * Asserts that that at least one of the given check
	 * classes is assignable from the first parameter.
	 * If no, {@link IllegalStateException} is raised.
	 */
	@SuppressWarnings("unchecked")
	public static void    assignable(Class cls, Class... checks)
	{
		if(cls != null) for(Class check : checks)
			//?: {the class is in the check list}
			if((check != null) && check.isAssignableFrom(cls))
				return;

		//?: {the target is not a requested class}
		throw EX.state("Class assertion failed: there is no class in the list [",
		  SU.scats(", ", (Object)checks), "] that is assignable from the class '",
		  LU.cls(cls), "'!"
		);
	}

	public static void    assignable(Object obj, Class... checks)
	{
		assignable((obj == null)?(null):(obj.getClass()), checks);
	}

	public static boolean eqcls(Object a, Object b)
	{
		return (a == b) || !((a == null) || (b == null)) &&
		  a.getClass().getName().equals(b.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public static boolean isa(Object obj, Class cls)
	{
		return (obj != null) && (cls != null) &&
		  cls.isAssignableFrom(obj.getClass());
	}
}