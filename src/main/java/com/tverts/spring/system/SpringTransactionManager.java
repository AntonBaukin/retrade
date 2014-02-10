package com.tverts.spring.system;

/* Spring Framework */

import org.springframework.transaction.jta.JtaTransactionManager;


/**
 * Singleton with JTA TransactionManager.
 *
 * @author anton.baukin@gmail.com
 */
public class SpringTransactionManager extends JtaTransactionManager
{
	/* TransactionManager Singleton */

	public static final SpringTransactionManager INSTANCE =
	  new SpringTransactionManager();

	public static SpringTransactionManager getInstance()
	{
		return INSTANCE;
	}

	private SpringTransactionManager()
	{}
}