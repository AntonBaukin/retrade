package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;


/**
 * Wrapper of transaction context.
 *
 * @author anton.baukin@gmail.com
 */
public class TxWrapper implements Tx
{
	/* public: constructor */

	public TxWrapper(Tx tx)
	{
		if(tx == null) throw new IllegalArgumentException();
		this.tx = tx;
	}

	/* public: TxContext interface */

	public SessionFactory getSessionFactory()
	{
		return tx.getSessionFactory();
	}

	public boolean        isRollbackOnly()
	{
		return tx.isRollbackOnly();
	}

	public void           setRollbackFlag()
	{
		tx.setRollbackFlag();
	}

	public void           setRollbackOnly()
	{
		tx.setRollbackOnly();
	}


	/* protected: the context wrapped */

	protected final Tx tx;
}
