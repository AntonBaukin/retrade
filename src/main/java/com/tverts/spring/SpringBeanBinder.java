package com.tverts.spring;

/* com.tverts: objects */

import com.tverts.objects.ObjectFactory;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

public class      SpringBeanBinder<O>
       implements ObjectFactory<O>
{
	/* public: ObjectFactory interface */

	@SuppressWarnings("unchecked")
	public <X extends O> X createInstance(Class<X> c)
	{
		String beanName = getBeanName();

		//?: {the bean name is not set}
		if(beanName == null) throw new IllegalStateException(
		  "Spring Bean Binder has no bean name specified!");

		Object object   = SpringPoint.beanOrNull(beanName);

		//?: {the object is not found}
		if(object == null) throw new IllegalStateException(String.format(
		  "Spring Bean Binder couldn't able to find the bean with name '%s'",
		  beanName));

		//?: {the object is of an illegal class}
		if((c != null) && !c.isAssignableFrom(object.getClass()))
			throw new IllegalStateException(String.format(
			  "Spring Bean Binder got '%s' bean of the class '%c' " +
			  "that is not assignable from the class requested: '%s'",
			  beanName, object.getClass().getName(), c.getName()));

		return (X)object;
	}

	public O               createInstance()
	{
		return this.<O> createInstance(null);
	}

	/* public: SpringBeanBinder interface */

	/**
	 * Defines the name of a Spring Bean this
	 * factory returns.
	 */
	public String getBeanName()
	{
		return beanName;
	}

	public void   setBeanName(String beanName)
	{
		if((beanName = s2s(beanName)) == null)
			throw new IllegalStateException();

		this.beanName = beanName;
	}

	/* private: the bean name */

	private String beanName;
}