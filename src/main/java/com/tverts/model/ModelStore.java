package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;
import java.util.Date;


/**
 * Model Store is a main container of the user's data
 * model parts (beans). It is itself a Java Bean.
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelStore extends Serializable
{
	/* public: ModelStore (not Java Bean) interface */

	/**
	 * Adds the bean instance to the model.
	 *
	 * If bean has the model key assigned, checks whether
	 * there is a bean with this key already registered.
	 * If so, does nothing, returns the registered instance.
	 *
	 * If the bean has {@link ModelBeanInfo} annotation, and
	 * is marked there as unique, the key attribute is
	 * inspected instead of {@link ModelBean#getModelKey()}.
	 *
	 * Note that store does not to raise errors on duplicate
	 * keys usage. It just returns not the argument object.
	 */
	public ModelBean addBean(ModelBean bean);

	public ModelBean removeBean(String key);

	/**
	 * As the find operation returns the bean instance
	 * registered by the key given. The last read time
	 * of the bean is automatically set to the present.
	 */
	public ModelBean readBean(String key);
}