package com.tverts.model;

/**
 * Implement this interface in model beans to
 * indicate that model supports selection of
 * data objects with given limit and start
 * position.
 *
 * @author anton.baukin@gmail.com
 */
public interface DataSelectModel
{
	/* constants */

	/**
	 * The name of HTTP request parameter to change
	 * the limit stored in the model.
	 *
	 * WARNING! Not allow for the limit to be too big!
	 */
	public static final String LIMIT_PARAM  = "limit";

	public static final int    LIMIT_MAX    = 100;

	public static final String START_PARAM  = "start";


	/* Data Select Model */

	public Integer  getDataStart();

	public void     setDataStart(Integer start);

	public Integer  getDataLimit();

	public void     setDataLimit(Integer start);

	/**
	 * Tells whether this selection model applies
	 * the given restriction (may be a flag object,
	 * string code, else). Returns restriction
	 * object, or null or true in simple.
	 */
	default Object  getRestriction(Object flag)
	{
		return null;
	}
}