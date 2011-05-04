package com.tverts.shunts;

/* standard Java classes */

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;

/* com.tverts: spring, support */

import static com.tverts.spring.SpringPoint.bean;
import static com.tverts.support.SU.s2s;

/**
 * Allows to refer by name a Spring bean to invoke
 * as the target of shunt unit. This class is for
 * prototype beans specially: after each method
 * invoked, the reference is cleared, and then
 * obtained again for the next shunt method call.
 * Hence, on each methods a new prototype instance
 * is created by Spring.
 *
 * If you have a singletone bean and don't want
 * to name it in Spring, you may consider
 * {@link SelfShuntTarget} instead of this class.
 *
 * @author anton.baukin@gmail.com
 */
public class      SelfShuntBean
       extends    SelfShuntBaseAnnotated
       implements SelfShuntReference
{
	/* public: SelfShuntReference interface */

	public List<SelfShunt>
	               dereferObjects()
	{
		return Collections.<SelfShunt>singletonList(this);
	}

	/* public: SelfShuntBean interface */

	/**
	 * Defines the name of Spring bean to target.
	 */
	public String  getBeanName()
	{
		return beanName;
	}

	public void    setBeanName(String name)
	{
		if((name = s2s(name)) == null)
			throw new IllegalArgumentException();

		this.beanName = name;
	}

	/**
	 * Tells that all the methods of the shunt unit
	 * are invoked on the same shunt instance.
	 *
	 * If this flag is defined, it takes priority
	 * over {@link SelfShuntUnit#single()}.
	 */
	public Boolean isSingleInstance()
	{
		return singleInstance;
	}

	public void    setSingleInstance(Boolean singleInstance)
	{
		this.singleInstance = singleInstance;
	}

	/* protected: SelfShuntBase interface */

	protected Object  getTarget()
	{
		return getActualTarget();
	}

	protected boolean afterMethod(Method m, SelfShuntTaskReport report)
	{
		clearActualTarget(false);
		return super.afterMethod(m, report);
	}

	protected void    freeShuntEnvironment()
	{
		clearActualTarget(true);
		super.freeShuntEnvironment();
	}

	/* protected: actual targeting */

	public Object     getActualTarget()
	{
		return (actualTarget != null)?(actualTarget)
		  :(actualTarget = obtainActualTarget());
	}

	public Object     obtainActualTarget()
	{
		return bean(getBeanName());
	}

	protected boolean isActualSingle()
	{
		if(isSingleInstance() != null)
			return isSingleInstance();

		SelfShuntUnit su = atShuntUnit();
		return (su != null) && su.single();
	}

	public void       clearActualTarget(boolean last)
	{
		//TODO implement cleanup and startup methods for shunt units

		if(last || !isActualSingle())
			this.actualTarget = null;
	}

	/* private: bean reference */

	private String  beanName;
	private Boolean singleInstance;

	/* private: invocation runtime  */

	private transient Object actualTarget;
}