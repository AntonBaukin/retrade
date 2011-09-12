package com.tverts.model;

/* standard Java classes */

import java.io.Serializable;


/**
 * Strategy generating keys for model beans.
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelKeysGen extends Serializable
{
	/* public: ModelKeysGen interface */

	public String genModelKey(ModelBean bean);
}