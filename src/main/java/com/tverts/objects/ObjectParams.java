package com.tverts.objects;

/* standard Java classes */

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.TreeMap;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Contains implementation of {@link ObjectParam}
 * and the support classes.
 *
 * @author anton.baukin@gmail.com
 */
public class ObjectParams
{
	/* object parameter implementation */

	public static class ReflectedParameter implements ObjectParam
	{
		/* public: constructor */

		public ReflectedParameter(Object object, Method read, Method write)
		{
			if(object == null)
				throw new IllegalArgumentException();
			if((read == null) & (write == null))
				throw new IllegalArgumentException();

			if((read != null)  && (read.getParameterTypes().length != 0))
				throw new IllegalArgumentException();
			if((write != null) && (write.getParameterTypes().length != 1))
				throw new IllegalArgumentException();


			this.object = object;
			this.read   = read;
			this.write  = write;
		}

		/* public: ObjectParam interface */

		public String  getName()
		{
			return name;
		}

		public void    setName(String name)
		{
			this.name = name;
		}

		public String  getDescr()
		{
			return descr;
		}

		public void    setDescr(String descr)
		{
			this.descr = descr;
		}

		public boolean isRequired()
		{
			return required;
		}

		public void    setRequired(boolean required)
		{
			this.required = required;
		}

		public boolean isRead()
		{
			return (read != null);
		}

		public Object  getValue()
		{
			if(!isRead())
				throw new IllegalArgumentException(String.format(
				  "Can't read parameter [%s] of object with class [%s]!",
				   getName(), object.getClass().getName()
				));

			try
			{
				return read.invoke(object);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		public String  getString()
		{
			Object v = this.getValue();
			return (v == null)?(""):(v.toString());
		}

		public boolean isWrite()
		{
			return (write != null);
		}

		public void    setValue(Object v)
		{
			if(!isWrite())
				throw new IllegalArgumentException(String.format(
				  "Can't write parameter [%s] of object with class [%s]!",
				   getName(), object.getClass().getName()
				));

			try
			{
				write.invoke(object, v);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		public void    setString(String s)
		{
			if(!isWrite())
				throw new IllegalArgumentException(String.format(
				  "Can't write parameter [%s] of object with class [%s]!",
				   getName(), object.getClass().getName()
				));

			try
			{
				write.invoke(object,
				  SU.s2v(write.getParameterTypes()[0], s));
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}


		/* the state */

		protected final Object object;
		protected final Method read;
		protected final Method write;
		private String         name;
		private String         descr;
		private boolean        required;
	}


	/* parameters reading */

	/**
	 * Finds {@link Param}-annotated methods of the object
	 * given, and creates {@link ReflectedParameter} for them.
	 */
	public static ObjectParam[] find(Object obj)
	{
		if(obj == null)
			return null;

		// 0 = "description",  1 = read-only,
		// 2 = write-only,     3 = required
		TreeMap<String, Object[]> ps =
		  new TreeMap<String, Object[]>();

		//~: read methods
		HashMap<String, Method>   rs =
		  new HashMap<String, Method>(11);

		//~: write methods
		HashMap<String, Method>   ws =
		  new HashMap<String, Method>(11);

		//c: for all the methods
		for(Method m : obj.getClass().getMethods())
		{
			Param  a = m.getAnnotation(Param.class);
			String n = m.getName();
			String x = "";
			Class  t = m.getReturnType();

			//?: {set-method}
			if(m.getParameterTypes().length != 0)
				t = m.getParameterTypes()[0];

			//?: {set-method}
			if(n.startsWith("set"))
				x = n.substring(3);
			//?: {get-method}
			else if(n.startsWith("get"))
				x = n.substring(3);
			//?: {is-method for boolean}
			if(n.startsWith("is"))
				if(Boolean.class.equals(t) || boolean.class.equals(t))
					x = n.substring(2);

			//?: {the name is valid for get-set-is}
			if(!x.isEmpty())
				if(x.charAt(0) == Character.toUpperCase(x.charAt(0)))
					n = x;

			//?: {name is defined by the annotation}
			if((a != null) && !SU.sXe(a.name()))
				n = SU.s2s(a.name());

			//?: {this is a precise read-method}
			if(!Void.class.equals(t) && (m.getParameterTypes().length == 0))
				rs.put(n, m);

			//?: {this is a precise write-method}
			if(Void.class.equals(t) && (m.getParameterTypes().length == 1))
				ws.put(n, m);

			//?: {this is read-alike method}
			if(!Void.class.equals(t))
				if(rs.get(n) == null)
					rs.put(n, m);

			//?: {this is write-alike method}
			if(m.getParameterTypes().length != 0)
				if(ws.get(n) == null)
					ws.put(n, m);

			//?: {has no annotation} do not add
			if(a == null) continue;

			//~: create the array
			Object[] p = ps.get(n);
			if(p == null)
				ps.put(n, p = new Object[4]);

			//[0]: set the description
			if(!SU.sXe(a.descr()))
				p[0] = a.descr();

			//[1]: {read-only}
			if(a.readonly())
				p[1] = true;

			//[2]: {write-only}
			if(a.writeonly())
				p[2] = true;

			//[3]: {required}
			if(a.required())
				p[3] = true;
		}

		//~: create the result
		ObjectParam[] r = new ObjectParam[ps.size()]; int i = 0;
		for(String n : ps.keySet())
		{
			Object[] p = ps.get(n);

			//?: {read-only} clear writer
			if(Boolean.TRUE.equals(p[1]))
				ws.remove(n);

			//?: {write-only} clear reader
			if(Boolean.TRUE.equals(p[2]))
				rs.remove(n);

			//?: {read-only & write-only}
			if((rs.get(n) == null) & (ws.get(n) == null))
				throw new IllegalStateException(String.format(
				  "Parameter [%s] of class [%s] is marked as write-only " +
				  "and read-only!", n, obj.getClass().getName()
				));

			//!: create the parameters
			ReflectedParameter op =
			  new ReflectedParameter(obj, rs.get(n), ws.get(n));

			//~: name
			op.setName(n);

			//~: description
			op.setDescr((String)p[0]);

			//~: required
			op.setRequired(Boolean.TRUE.equals(p[3]));

			//~: assign
			r[i++] = op;
		}

		return r;
	}
}