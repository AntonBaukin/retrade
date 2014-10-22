package com.tverts.model;

/* Java */

import java.io.Serializable;


/**
 * Each UI model element implements this interface.
 * It also may be a Java Bean object.
 *
 * Implementation class may have {@link ModelBeanInfo}
 * annotation to affect the models persistent container.
 *
 * Note that model bean has no reference to the Model
 * Data as this may cause XML saving of the whole model
 * when saving just this bean.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelBean extends Serializable
{
	/* Model Bean (primary) */

	/**
	 * The key of this model element unique within the container.
	 * Assigned automatically by the model and may not altered.
	 */
	public String    getModelKey();

	public void      setModelKey(String key);

	public boolean   isActive();

	public void      setActive(boolean active);

	public Long      getDomain();


	/* Model Bean (data access) */

	/**
	 * Returns the data access object derived from
	 * this model bean. Undefined result means that
	 * this model has no data model related.
	 */
	public ModelData modelData();
}