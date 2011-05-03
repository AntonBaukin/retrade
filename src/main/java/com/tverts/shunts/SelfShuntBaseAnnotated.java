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
 * Based on {@link SelfShuntBase}, this class extends it
 * adding support for auto discovering the shunt methods
 * searching the {@link SelfShuntMethod} annotated methods.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntBaseAnnotated
       extends        SelfShuntBase
{
	/* public: SelfShunt interface */

	public String   getShuntUnitName()
	{
		SelfShuntUnit  su = atShuntUnit();

		if((su != null) && !sXe(su.name()))
			return s2s(su.name());
		return super.getShuntUnitName();
	}

	public String[] getShuntGroups()
	{
		SelfShuntGroups sg = atShuntGroups();
		return (sg != null)?(a2a(sg.value())):(EMPTY_GROUPS);
	}

	/* protected: annotation support */

	protected void  initUnitReport(SelfShuntUnitReport report)
	{
		super.initUnitReport(report);

		SelfShuntDescr sd = atShuntDescription();

		if((sd != null) && !sXe(sd.en()))
			report.setDescriptionEn(s2s(sd.en()));

		if((sd != null) && !sXe(sd.en()))
			report.setDescriptionLo(s2s(sd.lo()));
	}

	protected SelfShuntTaskReport
	                createReport(Method m)
	{
		SelfShuntTaskReport r = super.createReport(m);
		SelfShuntMethod     a = atMethod(m);

		if(a != null)
			r.setCritical(a.critical());

		if((a != null) && !sXe(a.name()))
			r.setTaskName(s2s(a.name()));

		if((a != null) && !sXe(a.descrEn()))
			r.setDescriptionEn(s2s(a.descrEn()));

		if((a != null) && !sXe(a.descrLo()))
			r.setDescriptionLo(s2s(a.descrLo()));

		return r;
	}

	protected Collection<Method>
	                findMethods()
	{
		Collection<Method> r = new ArrayList<Method>(4);
		Class<?>           T = getTarget().getClass();

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

	protected void  sortMethods(ArrayList<Method> methods)
	{
		Collections.sort(methods, getMethodsComparator());
	}

	protected MethodsComparator
	                getMethodsComparator()
	{
		return new MethodsComparator();
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

	/* protected: methods comparator */

	protected static class MethodsComparator
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