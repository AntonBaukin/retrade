package com.tverts.hibery;

/* Hibernate Persistence Layer */

import org.hibernate.Session;


/**
 * Defines abstract for the database objects loader
 * that MUST be registered only as a prototype bean.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GetObjectPrototypeBase
       extends        GetObjectBase
{
	/* public: GetObjectPrototypeBase interface */

	public void setSession(Session session)
	{
		this.session = session;
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		if(this.session != null)
			return this.session;

		return super.session();
	}


	/* private: direct session reference */

	private Session session;
}