package com.tverts.model.store;

/* com.tverts: models */

import com.tverts.model.ModelsStore;


/**
 * Incapsulates strategy of accessing {@link ModelsStore}
 * instance associated with the pending request, the user,
 * or the global instance with cache and the backend.
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelsStoreAccess
{
	/* Models Store Access */

	public ModelsStore accessStore();
}