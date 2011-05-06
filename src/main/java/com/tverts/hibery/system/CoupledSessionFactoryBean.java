package com.tverts.hibery.system;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

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

	protected void postProcessConfiguration(Configuration config)
	  throws HibernateException
	{
		super.postProcessConfiguration(config);
		HiberSystem.getInstance().setConfiguration(config);
	}
}