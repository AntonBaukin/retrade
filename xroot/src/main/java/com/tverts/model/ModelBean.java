package com.tverts.model;

/**
 * Each UI model element implements this interface.
 *
 * Implementation class may provide {@link ModelInfo}
 * instance to affect the models persistent container
 * and the in-memory cache.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelBean extends java.io.Externalizable
{
	/* Model Bean (primary) */

	/**
	 * The key of this model element unique within the container.
	 * Assigned automatically by the model and may not altered.
	 */
	public String    getModelKey();

	public void      setModelKey(String key);

	/**
	 * Inactive models are removed from
	 * the database and the memory cache.
	 */
	public boolean   isActive();

	public void      setActive(boolean active);

	public Long      getDomain();


	/* Model Bean (data access) */

	/**
	 * Returns the data access object derived from
	 * this model bean. Undefined result means that
	 * this model has no data model related.
	 *
	 * Data objects must not be stored in the model.
	 */
	public ModelData modelData();
}