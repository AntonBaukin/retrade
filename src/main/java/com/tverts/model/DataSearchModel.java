package com.tverts.model;

/**
 * Denotes a model that searches the entities
 * by the strings array, or by selection set.
 *
 * @author anton.baukin@gmail.com.
 */
public interface DataSearchModel
{
	/* Data Search Model */

	/**
	 * The search names as they are written
	 * in the search field.
	 */
	public String  getSearchNames();

	public void    setSearchNames(String searchNames);

	/**
	 * Gives the name of the selection set.
	 * Note that in some variants undefined
	 * value means the default set, in else:
	 * the absence of this field.
	 */
	public String  getSelSet();

	public void    setSelSet(String selSet);

	/**
	 * Tells whether to use the selection set filtering.
	 * If this field is used, undefined selection set
	 * means the default one.
	 */
	public boolean isWithSelSet();

	public void    setWithSelSet(boolean withSelSet);
}