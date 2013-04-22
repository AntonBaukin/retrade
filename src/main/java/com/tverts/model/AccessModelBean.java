package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;


/**
 * Implements object access interface (as a factory)
 * to load {@link ModelBean} instances by it's key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      AccessModelBean<M extends ModelBean>
       implements ObjectAccess<M>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public AccessModelBean(String key)
	{
		this.key = key;
		this.cls = null;
	}

	public AccessModelBean(String key, Class cls)
	{
		this.key = key;
		this.cls = cls;
	}

	/* public: ObjectAccess interface */

	@SuppressWarnings("unchecked")
	public M accessObject()
	{
		ModelBean m = (key == null)?(null):
		  ModelAccessPoint.model().readBean(key);

		if((m != null) && (cls != null) && !cls.isAssignableFrom(m.getClass()))
			throw new IllegalStateException(String.format(
			  "Model bean requested by the key '%s' is not a class checked [%s]!",
			  key, cls.getName()
			));

		return (M)m;
	}


	/* protected: model bean key */

	protected final String key;
	protected final Class  cls;
}