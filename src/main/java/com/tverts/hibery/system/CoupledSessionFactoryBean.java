package com.tverts.hibery.system;

/* Hibernate Persistence Layer  */

import org.hibernate.HibernateException;

/* Spring Framework */

import org.springframework.orm.hibernate3.LocalSessionFactoryBean;


/**
 * TODO comment CoupledSessionFactoryBean
 *
 * @author anton.baukin@gmail.com
 */
public class   CoupledSessionFactoryBean
       extends LocalSessionFactoryBean
{
	/* protected: LocalSessionFactoryBean interface */

	protected void postProcessConfiguration()
	  throws HibernateException
	{
		super.postProcessConfiguration();
		HiberSystem.getInstance().setConfiguration(getConfiguration());
	}
}