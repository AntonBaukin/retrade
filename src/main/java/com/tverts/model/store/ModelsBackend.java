package com.tverts.model.store;

/* com.tverts: models */

import com.tverts.model.ModelsStore.ModelEntry;


/**
 * Strategy to permanently store UI Models.
 *
 * @author anton.baukin@gmail.com.
 */
public interface ModelsBackend
{
	/* Models Backend */

	public void find(ModelEntry e);

	public void store(ModelEntry e);
}