package com.tverts.api;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Payload object that is received from the server
 * as the response to {@link Ping} request.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "pong")
public class Pong
{
	/* public: Pong (bean) interface */

	/**
	 * The client-assigned key sent
	 * in the {@link Ping} request.
	 */
	public String getKey()
	{
		return key;
	}

	public void   setKey(String key)
	{
		this.key = key;
	}

	/**
	 * The class of the response object.
	 */
	public String getType()
	{
		return type;
	}

	public void   setType(String type)
	{
		this.type = type;
	}

	/**
	 * Response object. Assigned only
	 * for the receiving Pings.
	 */
	public Object getObject()
	{
		return object;
	}

	public void   setObject(Object object)
	{
		this.object = object;
	}

	/**
	 * The text of the request processing error.
	 * Always defined in the case of errors.
	 * Undefined value means successive processing.
	 */
	public String getError()
	{
		return error;
	}

	public void   setError(String error)
	{
		this.error = error;
	}

	/**
	 * Java error stack trace (as the text)
	 * printed by the server working in the
	 * debug mode. May be undefined for
	 * security and privacy reasons.
	 */
	public String getStack()
	{
		return stack;
	}

	public void   setStack(String stack)
	{
		this.stack = stack;
	}


	/* private: payload */

	private String key;
	private String type;
	private Object object;
	private String error;
	private String stack;
}