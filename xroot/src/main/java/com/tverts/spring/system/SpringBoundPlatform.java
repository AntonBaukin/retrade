package com.tverts.spring.system;

/* Java Transactions */

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/* Hibernate Persistence Layer */

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Implementation of Hibernate JTA Platform that
 * accesses already configured Spring Tx Manager.
 *
 * @author anton.baukin@gmail.com
 */
public class SpringBoundPlatform extends AbstractJtaPlatform
{
	/* protected: AbstractJtaPlatform interface */

	protected TransactionManager locateTransactionManager()
	{
		return EX.assertn(
		  SpringTransactionManager.INSTANCE.getTransactionManager(),
		  "Spring Transaction Manager is not initialized!"
		);
	}

	protected UserTransaction    locateUserTransaction()
	{
		return EX.assertn(
		  SpringTransactionManager.INSTANCE.getUserTransaction(),
		  "Spring Transaction Manager is not initialized!"
		);
	}
}