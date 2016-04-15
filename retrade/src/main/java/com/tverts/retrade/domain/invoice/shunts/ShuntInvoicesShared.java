package com.tverts.retrade.domain.invoice.shunts;

/* standard Java classes */

import java.util.Date;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* JUnit */

import static org.junit.Assert.fail;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;


/**
 * Shared tests and utilities for Invoices self-shunting.
 *
 * @author anton.baukin@gmail.com
 */
public class ShuntInvoicesShared
{
	/* public: constructor */

	public ShuntInvoicesShared(Session session, Domain domain)
	{
		this.session = session;
		this.domain  = domain;
	}


	/* public: self-shunts parts */

	/**
	 * Assumes that the Invoices of the type given has the same
	 * index-based order as the timestamp-based one.
	 */
	public void testInvoicesOrder(UnityType invoiceType)
	{
/*

select count(i.id) from Invoice i where (i.domain = :domain) and
  (i.invoiceType = :invoiceType) and (i.orderIndex is null)

select count(i.id) from Invoice i where (i.domain = :domain) and
  (i.invoiceType = :invoiceType) and (i.invoiceDate is null)

select i.id, i.orderIndex, i.invoiceDate from Invoice i where
  (i.domain = :domain) and (i.invoiceType = :invoiceType)
order by i.invoiceDate

*/

		//A: {has invoices with undefined order index}
		long n = ((Number) session.createQuery(

"select count(i.id) from Invoice i where (i.domain = :domain) and\n" +
"  (i.invoiceType = :invoiceType) and (i.orderIndex is null)"

		).
		  setParameter("domain", domain).
		  setParameter("invoiceType", invoiceType).
		  uniqueResult()).longValue();

		if(n > 0) fail("Has invoices with undefined order index! Invoice type is [" +
		  invoiceType.toString() + "].");


		//B: {has invoices with undefined timestamp}
		n = ((Number) session.createQuery(

"select count(i.id) from Invoice i where (i.domain = :domain) and\n" +
"  (i.invoiceType = :invoiceType) and (i.invoiceDate is null)"

		).
		  setParameter("domain", domain).
		  setParameter("invoiceType", invoiceType).
		  uniqueResult()).longValue();

		if(n > 0) fail("Has invoices with undefined timestamp! Invoice type is [" +
		  invoiceType.toString() + "].");


		//~: select the order
		Long pid = null, eid = null, ind = null;
		Date pdt = null;

		List sel = session.createQuery(

"select i.id, i.orderIndex, i.invoiceDate from Invoice i where\n" +
"  (i.domain = :domain) and (i.invoiceType = :invoiceType)\n" +
"order by i.invoiceDate"

		).
		  setParameter("domain", domain).
		  setParameter("invoiceType", invoiceType).
		  list();

		for(Object row : sel)
		{
			Long id = (Long) ((Object[])row)[0];
			Long ix = (Long) ((Object[])row)[1];
			Date dt = (Date) ((Object[])row)[2];

			if(pid == null)
			{
				pid = id;
				ind = ix;
				pdt = dt;
				continue;
			}

			//!: indices may not be the same even for equals timestamps
			if(ix.equals(ind))
			{
				eid = id;
				break;
			}

			//?: {the following index smaller (must be greater)}
			if(ix < ind)
				//?: {timestamps are not the same} index is incorrect!
				if(pdt.getTime() != dt.getTime())
				{
					eid = id;
					break;
				}

			pid = id;
			ind = ix;
			pdt = dt;
		}

		//C: test that order-dependent accumulations are the same
		if(eid != null) fail("Wrong order of Invoices with type [" +
		  invoiceType.toString() + "] between [" + pid + "] and [" +
		  eid + "]!");
	}

	public void testInvoicesSharedOrder(UnityType orderType)
	{

/*

select i.id, i.orderIndex, i.invoiceDate from Invoice i where
  (i.domain = :domain) and (i.orderType = :orderType)
order by i.invoiceDate

*/

		//~: select the order
		Long pid = null, eid = null, ind = null;
		Date pdt = null;

		List sel = session.createQuery(

"select i.id, i.orderIndex, i.invoiceDate from Invoice i where\n" +
"  (i.domain = :domain) and (i.orderType = :orderType)\n" +
"order by i.invoiceDate"

		).
		  setParameter("domain", domain).
		  setParameter("orderType", orderType).
		  list();

		for(Object row : sel)
		{
			Long id = (Long) ((Object[])row)[0];
			Long ix = (Long) ((Object[])row)[1];
			Date dt = (Date) ((Object[])row)[2];

			if(pid == null)
			{
				pid = id;
				ind = ix;
				pdt = dt;
				continue;
			}

			//!: indices may not be the same even for equals timestamps
			if(ix.equals(ind))
			{
				eid = id;
				break;
			}

			//?: {the following index smaller (must be greater)}
			if(ix < ind)
				//?: {timestamps are not the same} index is incorrect!
				if(pdt.getTime() != dt.getTime())
				{
					eid = id;
					break;
				}

			pid = id;
			ind = ix;
			pdt = dt;
		}

		//C: test that order-dependent accumulations are the same
		if(eid != null) fail("Wrong order of Invoices between [" +
		  pid + "] and [" + eid + "]!");
	}


	/* protected: parameters */

	protected final Session session;
	protected final Domain  domain;
}