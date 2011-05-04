package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/* JUnit */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/* com.tverts: hibernate */

import static com.tverts.hibery.HiberPoint.session;

/* com.tverts: self-shunting */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;

import static com.tverts.shunts.SelfShuntPoint.LOG_SHARED;

/* com.tverts: support */

import com.tverts.support.LU;
import static com.tverts.support.OU.sig;

@Component("shuntTxScopes") @Scope("prototype")
@SelfShuntUnit(single = true)
@SelfShuntGroups({"system:tx", "system:tx:scopes"})
@SelfShuntDescr("Tests binding the Spring Declarative " +
 "Tx Management Layer with Hibernate Database Connectivity.")
public class ShuntTxScopes
{
	@Autowired
	private SessionFactory sessionFactory;

	@SelfShuntMethod(order = 10, critical = true, descrEn =
	  "Is SessionFactory was injected via @Autowired?")
	public void testAutowired()
	{
		assertNotNull(
		  "SessionFactory must be injected to the private field",
		  sessionFactory
		);

		if(LU.isD(LOG_SHARED)) LU.D(LOG_SHARED, String.format(
		  "ShuntTxScopes: @Autowired [%s] SessionFactory",
		  sig(sessionFactory)
		));
	}

	private Session session;

	@Transactional
	@SelfShuntMethod(order = 20, critical = true, descrEn =
	  "Checks the main transaction.")
	public void testMainTransactionScope()
	{
		checkCurrentSession();
		this.session = session();
	}

	private Session sessionNested;

	@Transactional(propagation = REQUIRES_NEW)
	@SelfShuntMethod(order = 30, critical = true, descrEn =
	  "Creates and tests a nested transaction scope.")
	public void testTransactionScopeNested()
	{
		assertNotNull(
		  "Outer (main) Hibernate session scope must exist!",
		  this.session
		);

		checkCurrentSession();

		assertTrue(
		  "Nested Hibernate session must differ from the outer one!",
		  session() != this.session
		);

		this.sessionNested = session();
	}

	@Transactional
	@SelfShuntMethod(order = 40, critical = true, descrEn =
	  "Checks the outer scope is the same after the nested scope.")
	public void testTransactionScopeContinue()
	{
		assertNotNull(
		  "Outer (main) Hibernate session scope must exist!",
		  this.session
		);

		assertTrue(
		  "We still have the nested session active!",
		  session() != this.sessionNested
		);

		checkCurrentSession();

		assertTrue(
		  "We must have the same session continued.",
		  session() == this.session
		);
	}

	protected void checkCurrentSession()
	{
		assertNotNull(
		  "Hibernate session provided by SessionFactory must be defined!",
		  sessionFactory.getCurrentSession()
		);

		assertNotNull(
		  "Hibernate session provided by HiberPoint must be defined!",
		  session()
		);

		assertTrue(
		  "HiberPoint.session() == SessionFactory.getCurrentSession()",
		  session() == sessionFactory.getCurrentSession()
		);
	}
}