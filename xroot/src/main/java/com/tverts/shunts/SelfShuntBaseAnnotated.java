package com.tverts.shunts;

/* standard Java classes */

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/* com.tverts: support */

import static com.tverts.support.SU.a2a;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;


/**
 * This class extends {@link SelfShuntBase} adding support
 * for auto discovering the shunt methods searching the
 * {@link SelfShuntMethod} annotated methods.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntBaseAnnotated
       extends        SelfShuntBase
{
	/* public: SelfShunt interface */

	public String[]  getGroups()
	{
		SelfShuntGroups sg = atShuntGroups();
		return (sg != null)?(a2a(sg.value())):(EMPTY_GROUPS);
	}


	/* protected: basic implementation */

	protected String getNameDefault()
	{
		SelfShuntUnit  su = atShuntUnit();

		if((su != null) && !sXe(su.value()))
			return s2s(su.value());
		return super.getNameDefault();
	}


	/* protected: shunt unit invocation */

	protected void   initUnitReport(SelfShuntUnitReport report)
	{
		super.initUnitReport(report);

		SelfShuntDescr sd = atShuntDescription();

		//~: unit description (English) by the annotation
		if((sd != null) && !sXe(sd.value()))
			report.setDescriptionEn(s2s(sd.value()));

		//~: unit description (local) by the annotation
		if((sd != null) && !sXe(sd.value()))
			report.setDescriptionLo(s2s(sd.lo()));
	}


	/* protected: shunt methods discovery */

	protected Collection<Method> findMethods()
	{
		Collection<Method> r = new ArrayList<Method>(4);
		Class<?>           T = getTarget().getClass();

		//c: for all methods of the target class
		for(Method m : T.getMethods())
		{
			SelfShuntMethod a = atMethod(m);

			//?: {this method or it's supers does not have @ShuntMethod;}
			if(a == null) continue;

			//?: {the declaring class does not allow inheritance}
			if(!T.equals(m.getDeclaringClass()) && !a.inherit())
				continue;

			r.add(m);
		}

		return r;
	}

	protected void               sortMethods(ArrayList<Method> methods)
	{
		Collections.sort(methods, getMethodsComparator());
	}

	protected MethodsComparator  getMethodsComparator()
	{
		return new MethodsComparator();
	}


	/* protected: shunt methods invocation */

	protected SelfShuntTaskReport createReport(Method m)
	{
		SelfShuntTaskReport r = super.createReport(m);
		SelfShuntMethod     a = atMethod(m);

		//~: critical flag
		if(a != null)
			r.setCritical(a.critical());

		//~: editing flag
		if(a != null)
			r.setEditing(a.editing());

		//~: task name (overwrites the method name)
		if((a != null) && !sXe(a.name()))
			r.setTaskName(s2s(a.name()));

		//~: task (method) description (English) by the annotation
		if((a != null) && !sXe(a.descrEn()))
			r.setDescriptionEn(s2s(a.descrEn()));

		//~: task (method) description (local) by the annotation
		if((a != null) && !sXe(a.descrLo()))
			r.setDescriptionLo(s2s(a.descrLo()));

		return r;
	}


	/* protected: annotations handling */

	protected SelfShuntUnit   atShuntUnit()
	{
		return getTarget().getClass().
		  getAnnotation(SelfShuntUnit.class);
	}

	protected SelfShuntDescr  atShuntDescription()
	{
		return getTarget().getClass().
		  getAnnotation(SelfShuntDescr.class);
	}

	protected SelfShuntGroups atShuntGroups()
	{
		return getTarget().getClass().
		  getAnnotation(SelfShuntGroups.class);
	}

	/**
	 * Finds {@link SelfShuntMethod} annotation on the method
	 * given or on the overwritten methods of the superclass.
	 */
	protected SelfShuntMethod atMethod(Method m)
	{
		SelfShuntMethod r = null;

		while((r == null) && (m != null))
			if((r = m.getAnnotation(SelfShuntMethod.class)) == null)
				m = superMethod(m);
		return r;
	}

	protected Method          superMethod(Method m)
	{
		Class<?> sc = m.getDeclaringClass().getSuperclass();
		if(sc == null) return null;

		try
		{
			return sc.getMethod(m.getName(), m.getParameterTypes());
		}
		catch(NoSuchMethodException e)
		{
			return null;
		}
	}


	/* public: methods comparator */

	public static class MethodsComparator
	       implements   Comparator<Method>
	{
		public int compare(Method m1, Method m2)
		{
			Class<?> c1 = m1.getDeclaringClass();
			Class<?> c2 = m2.getDeclaringClass();

			//?: {the methods are not from the same class}
			if(!c1.equals(c2))
				//!: superclasses are invoked first
				return (c1.isAssignableFrom(c2))?(-1):(+1);

			SelfShuntMethod a1 = m1.getAnnotation(SelfShuntMethod.class);
			SelfShuntMethod a2 = m2.getAnnotation(SelfShuntMethod.class);
			final int       MX = Integer.MAX_VALUE;
			long            de = (long)
			  ((a1 != null)?(a1.order()):(MX)) -
			  ((a2 != null)?(a2.order()):(MX));

			return (de < 0L)?(-1):(de == 0L)?(0):(+1);
		}
	}
}