package com.tverts.api.core;

/**
 * Defines an instance that is not removed
 * from the database, but is marked as removed.
 *
 * Also, allows to pass the fact of removing.
 */
public interface RemovableObject
{
	/* Removable Object */

	public Boolean getRemoved();

	public void    setRemoved(Boolean removed);
}