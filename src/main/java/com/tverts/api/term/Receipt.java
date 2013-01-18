package com.tverts.api.term;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.TimestampAdapter;


/**
 * A cheque (sell terminal receipt)
 * with the goods was sold.
 */
@XmlType(name = "terminal-receipt")
public class Receipt
{
	/**
	 * Terminal primary key provided by the
	 * source database and configured on
	 * the terminal computer.
	 */
	@XmlAttribute(name = "terminal-key", required = true)
	public long getTerminal()
	{
		return terminal;
	}

	public void setTerminal(long terminal)
	{
		this.terminal = terminal;
	}

	/**
	 * The session key unique within the terminal.
	 */
	@XmlElement(name = "session-key", required = true)
	public String getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	/**
	 * The index of the receipt unique within the session.
	 */
	@XmlElement(name = "receipt-index", required = true)
	public String getIndex()
	{
		return index;
	}

	public void setIndex(String index)
	{
		this.index = index;
	}

	/**
	 * Sell operation timestamp.
	 */
	@XmlElement(name = "sell-time", required = true)
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	@XmlElementWrapper(name = "items", required = true)
	public List<GoodSell> getItems()
	{
		return items;
	}

	public void setItems(List<GoodSell> items)
	{
		this.items = items;
	}


	/* Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		Receipt receipt = (Receipt)o;

		return (terminal == receipt.terminal) &&
		  index.equals(receipt.index) &&
		  sessionKey.equals(receipt.sessionKey);
	}

	public int hashCode()
	{
		int result = (int)(terminal ^ (terminal >>> 32));
		result = 31*result + sessionKey.hashCode();
		result = 31*result + index.hashCode();
		return result;
	}


	/* attributes */

	private long   terminal;
	private String sessionKey;
	private String index;
	private Date   time;


	/* the goods list */

	private List<GoodSell> items =
	  new ArrayList<GoodSell>(8);
}