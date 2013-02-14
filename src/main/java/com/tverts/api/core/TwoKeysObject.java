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
public interface TwoKeysObject
{
	/* public: TwoKeysObject interface */

	/**
	 * Primary key in ReTrade system.
	 */
	public Long   getPkey();

	public void   setPkey(Long pkey);

	/**
	 * Primary key in an external system.
	 */
	public String getXkey();

	public void   setXkey(String xkey);
}
