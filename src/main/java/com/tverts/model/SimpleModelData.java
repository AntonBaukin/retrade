package com.tverts.model;

/* Java */

import java.io.Serializable;


/**
 * Data provider special for Simple Models.
 *
 * @author anton.baukin@gmail.com.
 */
public interface SimpleModelData extends ModelData, Serializable
{
	/* Simple Model Data */

	public SimpleModelBean getModel();

	/**
	 * Simple Model instance is always transient
	 * in Simple Data classes! When reading back
	 * externalized model, it assigns itself to
	 * the data restored.
	 */
	public void            setModel(SimpleModelBean mb);
}