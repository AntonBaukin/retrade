package com.tverts.objects;

/**
 * Incapsulates strategy of accessing object instance.
 *
 * NOTE that it is not guaranteed that the same instance
 * would be returned from call to call!
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ObjectAccess<O>
{
	/* public: ObjectAccess interface */

	public O accessObject();
}