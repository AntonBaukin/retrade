package com.tverts.spring;

/* Spring Framework */

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/* com.tverts: servlets, support */

import com.tverts.servlet.RequestPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Point to access Spring Context and the beans.
 *
 * @author anton.baukin@gmail.com
 */
public class SpringPoint
{
	/* Spring Point Singleton */

	public static final SpringPoint INSTANCE =
	  new SpringPoint();

	public static SpringPoint getInstance()
	{
		return INSTANCE;
	}

	private SpringPoint()
	{}


	/* Spring Framework Access Interface */

	public static Object         bean(String name)
	{
		EX.asserts(name);
		return getInstance().getSpringContext().getBean(name);
	}

	/**
	 * The same as {@link #bean(String)}, but returns
	 * {@code null} if the bean is not registered.
	 */
	public static Object         beanOrNull(String name)
	{
		EX.asserts(name);

		try
		{
			return getInstance().getSpringContext().getBean(name);
		}
		catch(NoSuchBeanDefinitionException e)
		{
			return null;
		}
	}

	/**
	 * Returns the Spring bean registered by the name
	 * taken from the simple name of the class with
	 * the first letter lower-cased.
	 */
	@SuppressWarnings("unchecked")
	public static <B> B          bean(Class<B> beanClass)
	{
		String        sn = beanClass.getSimpleName();
		StringBuilder sb = new StringBuilder(sn);

		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return (B)bean(sb.toString());
	}

	/**
	 * The same as {@link #bean(Class)}, but returns
	 * {@code null} if the bean is not registered.
	 */
	public static <B> B          beanOrNull(Class<B> beanClass)
	{
		try
		{
			return bean(beanClass);
		}
		catch(NoSuchBeanDefinitionException e)
		{
			return null;
		}
	}

	public WebApplicationContext getSpringContext()
	{
		return EX.assertn(WebApplicationContextUtils.
		  getWebApplicationContext(RequestPoint.context()),
		  "Spring Framework web context does not exist!"
		);
	}
}