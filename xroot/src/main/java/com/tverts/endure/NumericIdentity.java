package com.tverts.endure;

/**
 * Tells that the database object is stored by
 * the primary key with Long type.
 *
 * Each [leaf] class of entities may has own keys
 * generator, but all the instances of the same
 * [leaf] class share the same generator instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface NumericIdentity
{
	/* Entity with Numeric Identity */

	public Long getPrimaryKey();

	public void setPrimaryKey(Long primaryKey);
}
