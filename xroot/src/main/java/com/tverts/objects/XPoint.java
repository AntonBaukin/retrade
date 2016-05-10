package com.tverts.objects;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Point to access procedures of writing and
 * reading objects marked by JAXB annotations
 * to XML or JSON and back.
 *
 * @author anton.baukin@gmail.com
 */
public class XPoint
{
	/* Singleton */

	public static XPoint getInstance()
	{
		return INSTANCE;
	}

	private static final XPoint INSTANCE =
	  new XPoint();

	protected XPoint()
	{}


	/* X-Point (static) interface */

	public static XStreamer xml()
	{
		return EX.assertn(INSTANCE.streamerXML,
		  "XML X-Streamer strategy is not specified!"
		);
	}

	public static XStreamer json()
	{
		return EX.assertn(INSTANCE.streamerJSON,
		  "JSON X-Streamer strategy is not specified!"
		);
	}


	/* X-Point (bean) interface */

	public void setStreamerXML(XStreamer streamerXML)
	{
		this.streamerXML = streamerXML;
	}

	private XStreamer streamerXML;

	public void setStreamerJSON(XStreamer streamerJSON)
	{
		this.streamerJSON = streamerJSON;
	}

	private XStreamer streamerJSON;
}