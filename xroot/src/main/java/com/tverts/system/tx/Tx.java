package com.tverts.system.tx;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;


/**
 * Transactional context of the request to the system.
 * Global transactional context is created when starting
 * processing the request by the system level components.
 * It is accessible through {@link TxPoint}.
 *
 * Note that tx context may be created before the
 * transaction actually starts. hence, it never stores
 * direct references to the transactional resources.
 *
 * Tx Context may be used only by the thread created it!
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Tx
{
	/* public: TxContext interface */

	/**
	 * The transaction number. Created on demand
	 * for the updating transactions.
	 *
	 * The number is selected from database' atomic
	 * sequence that is not bound to a transaction.
	 */
	public long           txn();

	public void           free();

	/**
	 * The factory of Hibernate sessions to access
	 * the system' primary database.
	 */
	public SessionFactory getSessionFactory();

	public boolean        isRollbackOnly();

	/**
	 * Marks the transaction that instead the commit
	 * it would be rolled back. A soft variant to
	 * rollback the transaction.
	 */
	public void           setRollbackFlag();

	/**
	 * Forces the transaction to be rolled back on
	 * the transactional layer. The rollback flag
	 * is also set here. A strict variant to
	 * rollback the transaction.
	 */
	public void           setRollbackOnly();

	/**
	 * Identifier of the transaction object.
	 */
	public String         txid();

	/**
	 * Adapts the context to the given interface.
	 */
	public <I> void       set(Class<I> cls, I instance);

	public <I> I          get(Class<I> cls);

	public Object         val(Object key);

	/**
	 * Updates the value of the context.
	 *
	 * Values of all the contexts in the stack
	 * are shared in the default wrapper implementation.
	 */
	public void           val(Object key, Object val);
}