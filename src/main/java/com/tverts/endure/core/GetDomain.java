package com.tverts.endure.core;

/* standard Java classes */

import java.util.List;

/* Hibernate Persistence Layer */

import com.tverts.endure.UnityType;
import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Read access to the {@link Domain} instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("getDomain")
public class GetDomain
{
	/* Get Domain */

	@Transactional
	public Domain getTestDomain()
	{

/*

from Domain d where (d.primaryKey < 0)

*/

		final String Q =
		  "from Domain d where (d.primaryKey < 0)";


		List domains = sessionFactory.getCurrentSession().
		  createQuery(Q).list();

		return (Domain)(domains.isEmpty()?(null):(domains.get(0)));
	}

	/* public: Session Factory */

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected SessionFactory sessionFactory;
}