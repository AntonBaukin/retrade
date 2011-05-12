package com.tverts.endure.core;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: endure */

import com.tverts.endure.UnityType;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.newPrimaryKey;


/**
 * Updating and saving {@link UnityType} instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("setUnityType")
public class SetUnityType
{
	/* Save UnityType */

	@Transactional
	public void saveUnityType(UnityType ut)
	{
		sessionFactory.getCurrentSession().
		  save(newPrimaryKey(ut));
	}

	/* public: Session Factory */

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected SessionFactory sessionFactory;
}