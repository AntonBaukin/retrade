package com.tverts.endure;

/**
 * This interface denotes entity
 * having it's state written as
 * object that is stored in the
 * database as XML document with
 * compressed bytes stream.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface Ox
{
	/* Object Extraction */

	/**
	 * Provides the object. Note that it
	 * is decoded from the bytes store on
	 * the first call, but then the same
	 * link is always exist even when the
	 * object is written back to the bytes
	 * store, and re-read again. The link is
	 * updated with {@link #setOx(Object)}.
	 */
	public Object  getOx();

	public void    setOx(Object ox);

	/**
	 * Invoke that method to indicate
	 * that the Ox object was updated
	 * without changing it's link.
	 *
	 * When the object is updated, it is
	 * later re-written to the database.
	 *
	 * Note that you must also invoke this
	 * method for new objects.
	 */
	public void    updateOx();

	/**
	 * Tells whether the object was updated.
	 *
	 * Note that new object not loaded from
	 * the backend is always marked as updated.
	 *
	 * But when updating it you still must
	 * invoke {@link #updateOx()} method!
	 */
	public boolean isUpdatedOx();
}