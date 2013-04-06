package com.tverts.objects;

/**
 * Exposes parameter of an object and provides
 * methods to read and write it.
 *
 * @author anton.baukin@gmail.com
 */
public interface ObjectParam
{
	/* public: ObjectParam interface */

	/**
	 * Returns the display name of the parameter.
	 * Note that this name may be changed to
	 * simplify handling composite objects.
	 */
	public String  getName();

	public void    setName(String name);

	/**
	 * Optional description text on the parameter
	 * meaning and possible values.
	 */
	public String  getDescr();

	public void    setDescr(String descr);

	public boolean isRequired();

	public boolean isRead();

	/**
	 * Returns the present value of the parameter.
	 */
	public Object  getValue();

	/**
	 * Returns the present value of the parameter
	 * encoded (converted) as string.
	 */
	public String  getString();

	public boolean isWrite();

	public void    setValue(Object v);

	/**
	 * Decodes the value object from the string
	 * and assigns it.
	 */
	public void    setString(String s);
}