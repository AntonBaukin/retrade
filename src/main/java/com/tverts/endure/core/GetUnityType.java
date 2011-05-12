package com.tverts.endure.core;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: endure */

import com.tverts.endure.UnityType;


/**
 * Read access to the {@link UnityType} instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("getUnityType")
public class GetUnityType
{
	/* Get UnityType */

	@Transactional
	public UnityType getUnityType(Long id)
	{
		return (UnityType)sessionFactory.getCurrentSession().
		  get(UnityType.class, id);
	}

	@Transactional
	public UnityType findUnityType(Class typeClass, String typeName)
	{
/*

from UnityType ut where
  ut.typeClass = :typeClass and ut.typeName = :typeName

*/
		final String Q =
		  "from UnityType ut where\n" +
		  "  ut.typeClass = :typeClass and ut.typeName = :typeName";

		return (UnityType)sessionFactory.getCurrentSession().createQuery(Q).
		  setParameter("typeClass", typeClass).
		  setParameter("typeName",  typeName).
		  uniqueResult();
	}



	/* public: Session Factory */

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected SessionFactory sessionFactory;
}