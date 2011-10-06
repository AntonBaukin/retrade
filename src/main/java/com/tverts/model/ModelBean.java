package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;
import java.util.Date;


/**
 * Each model element must implement this interface.
 * It must be a valid Java Bean object ready.
 *
 * Implementation class may have {@link ModelBeanInfo}
 * annotation to affect the model internal container.
 *
 * Note that model bean has no reference to the model
 * as this may cause XML saving of the whole model when
 * saving just this bean.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelBean extends Serializable
{
	/* public: ModelBean (not Java Bean) interface */

	/**
	 * The key of this model element within the container.
	 * Assigned automatically by the model and may not
	 * be changed outside.
	 */
	public String    getModelKey();

	public void      setModelKey(String key);

	/**
	 * When the state of this model element is changed,
	 * it must update the last modification timestamp.
	 *
	 * Note that this value may be send as a last
	 * modification time to the remote clients instead
	 * of the actual content.
	 */
	public Date      getUpdateTime();


	/* public: ModelBean (data access) interface */

	/**
	 * Returns the data access object derived from
	 * this model bean. Undefined result means that
	 * this model has no data model related.
	 */
	public ModelData modelData();
}