package com.tverts.model.keys;

/* Java */

import java.util.UUID;

/* com.tverts: model */

import com.tverts.model.ModelBean;


/**
 * Model keys generator creating random keys
 * with standard UUIDs generator.
 *
 * @author anton.baukin@gmail.com
 */
public class ModelKeysGenUUID extends ModelKeysGenBase
{
	/* protected: Model Keys Generator Base */

	protected String nextModelKey(ModelBean bean)
	{
		return UUID.randomUUID().toString().toUpperCase();
	}
}