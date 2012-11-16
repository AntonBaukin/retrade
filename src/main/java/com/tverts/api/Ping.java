package com.tverts.api;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Payload object that is sent to the server.
 *
 * Contains the request object that must
 * be a Java Bean compatible with JAXB
 * Object-XML mapping protocol.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "ping")
public class Ping
{
	/* public: Ping (bean) interface */

	/**
	 * Optional key of the request assigned
	 * by the client. Length may not exceed 255.
	 */
	public String  getKey()
	{
		return key;
	}

	public void    setKey(String key)
	{
		this.key = key;
	}

	/**
	 * The name of the request object class.
	 * Assigned automatically. Checked by the server.
	 */
	public String  getType()
	{
		return type;
	}

	public void    setType(String type)
	{
		this.type = type;
	}

	/**
	 * The request object.
	 */
	public Object  getRequest()
	{
		return request;
	}

	public void    setRequest(Object request)
	{
		this.request = request;
	}


	/* private: payload */

	private String key;
	private String type;
	private Object request;
}