package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* Hibernate Persistence Layer */

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;

/* com.tverts: endure (core + locks)*/

import com.tverts.endure.UnityType;
import com.tverts.endure.locks.Locks;


/**
 * Service method of accessing unique
 * (incremented) values defined by
 * the triple: domain, type, and selector.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("incValues")
public class IncValues extends GetObjectBase
{
	/* Get IncValue */

	public IncValue getIncValue(Domain d, UnityType type, String selector)
	{

/*

from IncValue iv where (iv.domain = :domain) and
  (iv.valueType = :valueType) and (iv.selector = :selector)

from IncValue iv where (iv.domain = :domain) and
  (iv.valueType = :valueType) and (iv.selector is null)

 */
		final String Q1 =

"from IncValue iv where (iv.domain = :domain) and\n" +
"  (iv.valueType = :valueType) and (iv.selector = :selector)";

		final String Q2 =

"from IncValue iv where (iv.domain = :domain) and\n" +
"  (iv.valueType = :valueType) and (iv.selector is null)";

		Query        q = Q((selector == null)?(Q2):(Q1)).
		  setParameter("domain", d).
		  setParameter("valueType", type);

		if(selector != null)
			q.setParameter("selector", selector);

		return (IncValue) q.uniqueResult();
	}


	/* public: increment operations */

	/**
	 * Returns present value and saves back incremented one.
	 *
	 * Note that {@link IncValue} instance may not yet exist.
	 * In this case it would be created-saved. The initial
	 * value is always 1.
	 */
	public long incValue(Domain d, UnityType type, String selector, int delta)
	{
		if(delta <= 0)   throw new IllegalArgumentException();
		if(d == null)    throw new IllegalArgumentException();
		if(type == null) throw new IllegalArgumentException();

		IncValue i = getIncValue(d, type, selector);

		//?: {it does not exist} create
		if(i == null)
		{
			//!: obtain codes lock of the domain
			Locks.lock(d, Domains.LOCK_CODES);

			i = new IncValue();

			//~: primary key
			HiberPoint.setPrimaryKey(session(), i,
			  HiberPoint.isTestInstance(d));

			//~: domain
			i.setDomain(d);

			//~: type
			i.setValueType(type);

			//~: selector
			i.setSelector(selector);

			//~: present value
			i.setValue(1L + delta);

			//!: do save
			session().save(i);

			return 1L;
		}

		//!: obtain lock for the value
		session().buildLockRequest(LockOptions.UPGRADE).
		  setLockMode(LockMode.PESSIMISTIC_WRITE).
		  lock(i);

		long     r = i.getValue();

		//~: do increment
		i.setValue(r + delta);

		return r;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public long txIncValue(Domain d, UnityType type, String selector, int delta)
	{
		return incValue(d, type, selector, delta);
	}
}