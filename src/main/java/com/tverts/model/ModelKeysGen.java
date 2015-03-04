package com.tverts.model;

/**
 * Strategy generating keys for Model Beans.
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelKeysGen
{
	/* Model Keys Generator */

	public String genModelKey(ModelBean bean);
}