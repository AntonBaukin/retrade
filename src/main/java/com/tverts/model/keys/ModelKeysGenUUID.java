package com.tverts.model.keys;

/* standard Java classes */

import java.util.UUID;

/* com.tverts: model */

import com.tverts.model.ModelBean;


/**
 * Model keys generator creating random keys
 * with standard UUIDs generator.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ModelKeysGenUUID extends ModelKeysGenBase
{
	public static final long serialVersionUID = 0L;


	/* protected: ModelKeysGenBase interface */

	protected String generateSerialKey(ModelBean bean)
	{
		return UUID.randomUUID().toString();
	}
}