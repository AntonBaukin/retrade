package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: system (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: self-shunting */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;

import static com.tverts.shunts.SelfShuntPoint.LOG_SHARED;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Tests Tx-Scopes Bean. Note that Self-Shunts are
 * executed in the same way as an ordinary HTTP
 * requests, so outer Tx already exists.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@Component @Scope("prototype")
@SelfShuntUnit(single = true)
@SelfShuntGroups({"system", "system:tx"})
@SelfShuntDescr("Tests binding the Spring Declarative " +
 "Tx Management Layer with Hibernate Database Connectivity.")
public class ShuntTxScopes
{
	@Autowired
	protected SessionFactory sessionFactory;


	@SelfShuntMethod(order = 10, critical = true, descrEn =
	  "Is SessionFactory was injected via @Autowired?")
	public void testAutowired()
	{
		EX.assertn(sessionFactory, "SessionFactory must be injected to the private field");

		LU.D(LOG_SHARED, "ShuntTxScopes: @Autowired [",
		  LU.sig(sessionFactory), "] SessionFactory"
		);
	}

	private Session session;

	@SelfShuntMethod(order = 20, critical = true, descrEn =
	  "Checks the main transaction.")
	public void testTransactionScopeMain()
	{
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				checkCurrentSession();
				session = txSession();
			}
		});
	}

	private Session sessionNested;

	@SelfShuntMethod(order = 30, critical = true, descrEn =
	  "Creates and tests a nested transaction scope.")
	public void testTransactionScopeNested()
	{
		EX.assertn(this.session, "Outer (main) Hibernate session scope must exist!");


		bean(TxBean.class).setNew().execute(new Runnable()
		{
			public void run()
			{
				checkCurrentSession();

				EX.assertx(txSession() != session,
				  "Nested Hibernate session must differ from the outer one!"
				);

				sessionNested = txSession();
			}
		});

	}

	@SelfShuntMethod(order = 40, critical = true, descrEn =
	  "Checks the outer scope is the same after the nested scope.")
	public void testTransactionScopeContinue()
	{
		EX.assertn(this.session, "Outer (main) Hibernate session scope must exist!");

		EX.assertx(txSession() != this.sessionNested,
		  "We still have the nested session active!"
		);

		checkCurrentSession();

		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				checkCurrentSession();

				EX.assertx( txSession() == session,
				  "We must have the same session continued!"
				);
			}
		});
	}

	protected void checkCurrentSession()
	{
		EX.assertn( sessionFactory.getCurrentSession(),
		  "Hibernate session provided by SessionFactory must be defined!"
		);

		EX.assertn(txSession(),
		           "Hibernate session provided by HiberPoint must be defined!"
		);

		EX.assertx( txSession() == sessionFactory.getCurrentSession(),
		  "HiberPoint.session() != SessionFactory.getCurrentSession()"
		);
	}
}