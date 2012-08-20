package com.tverts.endure.core;

/**
 * Collection of useful properties and helper
 * functions for {@link Domain}s.
 *
 * @author anton.baukin@gmail.com
 */
public class Domains
{
	/* domain (global) lock types */

	/**
	 * This exclusive lock must be obtained before
	 * creating (saving) a Good Unit or Store instances.
	 *
	 * When creating Good Unit there must be also created
	 * aggregated value of volume in each Store and the
	 * Domain; and the rest cost value in the Domain.
	 *
	 * When creating Store there must be also created
	 * aggregated values for each Good Unit.
	 */
	public static final String LOCK_XGOODS =
	  "Locks: Domain: Exclusive Goods and Stores Lock";

	/**
	 * This lock is obtained when inserting new
	 * IncValue instance.
	 */
	public static final String LOCK_CODES  =
	  "Locks: Domain: Exclusive Codes Lock";
}