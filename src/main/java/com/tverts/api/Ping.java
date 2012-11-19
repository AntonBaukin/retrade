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
 * If the request object is undefined, this
 * Ping request is for receiving the result
 * of the previous request object sent
 * in this session.
 *
 * When receiving the response the server
 * would return the next undelivered
 * result object. After this the result
 * is marked and not sent again. But it
 * is still possible within some period
 * of time to get the same instance again
 * by sending receiving Ping with the
 * key set to the key request of interest.
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
	 *
	 * For receiving Pings set the key undefined
	 * to return the next undelivered response.
	 *
	 * Define the key to return the ready response
	 * regardless the delivery mark.
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
	 * The request object instance. When it is
	 * specified, the server would place it
	 * in the processing queue for the execution.
	 *
	 * If the request is undefined, this Ping
	 * instance is a response receiver.
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