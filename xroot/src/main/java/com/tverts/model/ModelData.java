package com.tverts.model;

/**
 * While model bean contains user related input
 * parameters, model data bean stores the data
 * loaded from the database.
 *
 * Model data beans a mapped to XML, may be
 * serialized and transferred, but never stored
 * in the model point.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelData
{
	/* Model Data */

	public ModelBean getModel();
}