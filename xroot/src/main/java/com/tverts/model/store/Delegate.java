package com.tverts.model.store;

/**
 * Interface to additionally wrap
 * store strategies.
 *
 * @author anton.baukin@gmail.com
 */
public interface Delegate
{
	/* Models Store Delegate */

	public void find(ModelEntry e);

	public void found(ModelEntry e);

	public void remove(ModelEntry e);

	public void save(ModelEntry e);

	public void create(ModelEntry e);
}
