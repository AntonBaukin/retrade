package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;


/**
 * Wrapper of transaction context.
 *
 * @author anton.baukin@gmail.com
 */
public class TxWrapperBase implements TxWrapper
{
	/* public: constructor */

	public TxWrapperBase(Tx tx)
	{
		if(tx == null)
			throw new IllegalArgumentException();

		this.tx = tx;
	}


	/* public: TxContext interface */

	public long           txn()
	{
		return tx.txn();
	}

	public void           free()
	{
		tx.free();
	}

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

	public String         txid()
	{
		return tx.txid();
	}


	/* public: TxWrapper interface */

	public final Tx       getWrappedTx()
	{
		return tx;
	}


	/* protected: the context wrapped */

	private final Tx tx;
}
