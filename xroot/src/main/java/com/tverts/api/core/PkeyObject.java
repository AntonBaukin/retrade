package com.tverts.api.core;

/**
 * Interface denoting database entities
 * having Long primary keys. Note that
 * almost every entity has Long key.
 */
public interface PkeyObject
{
	/* Primary Key Object */

	/**
	 * Primary key in ReTrade system.
	 */
	public Long getPkey();

	public void setPkey(Long pkey);
}