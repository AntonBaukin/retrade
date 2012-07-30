package com.tverts.hibery.system;

/* Hibernate Persistence Layer  */

import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;


/**
 * COMMENT CoupledSessionFactoryBean
 *
 * @author anton.baukin@gmail.com
 */
public class   CoupledSessionFactoryBean
       extends LocalSessionFactoryBean
{
	/* protected: LocalSessionFactoryBean interface */

    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder config)
    {
        HiberSystem.getInstance().setConfiguration(config);
        return super.buildSessionFactory(config);
    }
}