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
 * @author anton baukin (abaukin@mail.ru)
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

	/* protected: SelfShuntBase interface */

	protected Object  getTarget()
	{
		return getActualTarget();
	}

	protected boolean afterMethod(Method m, SelfShuntTaskReport report)
	{
		clearActualTarget();
		return super.afterMethod(m, report);
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

	public void       clearActualTarget()
	{
		this.actualTarget = null;
	}

	/* private: bean reference */

	private String beanName;

	/* private: invocation runtime  */

	private Object actualTarget;
}