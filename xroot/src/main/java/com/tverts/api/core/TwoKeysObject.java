package com.tverts.api.core;

/**
 * Interface defining objects intended for
 * export + integration purposes. Such an
 * objects has two primary keys: pkey for
 * ReTrade database, and xkey for external
 * integration database.
 *
 * Note that ReTrade doesn't store the
 * external keys as more then one
 * integration database may exist.
 */
public interface TwoKeysObject extends PkeyObject
{
	/* Two Keys Object */

	/**
	 * Primary key in an external system.
	 */
	public String getXkey();

	public void   setXkey(String xkey);
}
