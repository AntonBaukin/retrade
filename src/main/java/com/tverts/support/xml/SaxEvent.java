package com.tverts.support.xml;

/* SAX Parser */

import org.xml.sax.Attributes;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.AttributesImpl;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.sXe;


/**
 * An event of SAX processing an XML document.
 * Occurs on each XML tag opened.
 *
 * State template parameter  means the state
 * class of the XML stack processing. The state
 * may be associated with each event.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SaxEvent<State>
{
	/* public: constructor */

	/**
	 * Creates SQX event. Attributes are copied,
	 * version 2 is supported.
	 */
	public SaxEvent(String uri, String lName, String qName, Attributes attrs, int level)
	{
		this.uri   = uri;
		this.lName = lName;
		this.qName = qName;
		this.attrs = copyAttrs(attrs);
		this.level = level;
	}


	/* public: SaxEvent interface */

	public String          tag()
	{
		if(!sXe(lName))
			return lName;
		else if(!sXe(qName))
			return qName;
		else
			throw EX.state("XML event has no tag name!");
	}

	public boolean         istag(String... names)
	{
		String tag = this.tag();

		for(String name : names)
			if(name.equals(tag))
				return true;

		return false;
	}

	public boolean         istag(int level, String... names)
	{
		return (level == level()) && istag(names);
	}

	public String          uri()
	{
		return uri;
	}

	public String          lName()
	{
		return lName;
	}

	public String          qName()
	{
		return qName;
	}

	public String          attr(String qname)
	{
		return attrs.getValue(qname);
	}

	public Attributes      attrs()
	{
		return attrs;
	}

	public Attributes2     attrs2()
	{
		if(!(attrs instanceof Attributes2))
			throw new IllegalStateException();
		return (Attributes2)attrs;
	}

	public State           state()
	{
		return state;
	}

	public SaxEvent<State> state(State state)
	{
		this.state = state;
		return this;
	}

	public String          text()
	{
		return (this.text == null)?(""):(this.text.toString());
	}

	public StringBuilder   text(char[] buf, int o, int l)
	{
		if(this.text == null)
			this.text = new StringBuilder(l);
		return this.text.append(buf, o, l);
	}

	/**
	 * Position of this event in the XML document tree.
	 * Root node has zero level.
	 */
	public int             level()
	{
		return level;
	}


	/* protected: internal interface */

	protected Attributes   copyAttrs(Attributes attrs)
	{
		if(attrs instanceof Attributes2)
			return new Attributes2Impl(attrs);
		return new AttributesImpl(attrs);
	}


	/* private: event state */

	private String         uri;
	private String         lName;
	private String         qName;
	private Attributes     attrs;
	private State          state;
	private StringBuilder  text;
	private int            level;
}