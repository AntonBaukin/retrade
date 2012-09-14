package com.tverts.support.xml;

/* standard Java classes */

import javax.xml.parsers.SAXParserFactory;

/* SAX Parser */

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Strategy of processing XML documents
 * with SAX parser.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SaxProcessor<State>
{
	/* public: SaxProcessor interface */

	public void process(InputSource source)
	{
		try
		{
			SAXParserFactory.newInstance().newSAXParser().
			  parse(source, createHandler());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void process(String uri)
	{
		try
		{
			SAXParserFactory.newInstance().newSAXParser().
			  parse(uri, createHandler());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/* protected: SAX events processing */

	/**
	 * Factory method of creating processing state
	 * for the given event. The current event
	 * (without a state) is already on the top.
	 */
	protected abstract void   createState();

	/**
	 * Invoked on each event open after pushing it
	 * to the stack top.
	 */
	protected abstract void   open();

	/**
	 * Invoked on each event open before removing
	 * it from the stack top.
	 */
	protected abstract void   close();

	/**
	 * Invoked when the document processing finished.
	 * The stack is empty here. The root event is
	 * passed as the argument.
	 */
	protected void            done(SaxEvent<State> root)
	{}

	protected SaxStack<State> stack()
	{
		return (this.stack != null)?(this.stack):
		  (this.stack = createStack());
	}

	protected SaxStack<State> createStack()
	{
		return new SaxStackBase<State>();
	}

	protected SaxEvent<State> createEvent
	  (String uri, String lName, String qName, Attributes attrs)
	{
		return new SaxEvent<State>(uri, lName, qName, attrs, stack().size());
	}

	protected DefaultHandler  createHandler()
	{
		return new SaxHandler();
	}


	/* public: document handler implementation */

	public class SaxHandler extends DefaultHandler
	{
		/* public: DefaultHandler interface */

		public void startElement
		  (String uri, String lName, String qName, Attributes attrs)
		{
			//~: create and place event to the stack
			stack().push(createEvent(uri, lName, qName, attrs));
			createState();

			//~: open the event in the handler
			open();
		}

		public void endElement(String uri, String lname, String qname)
		{
			//~: close the event in the handler
			close();

			//~: pop the event from the stack
			SaxEvent<State> e = stack().pop();

			//?: {document is finished}
			if(stack().empty())
				done(e);
		}

		public void characters(char[] ch, int start, int length)
		{
			if(!stack.empty())
				stack.top().text(ch, start, length);
		}
	}


	/* protected: helpers */

	/**
	 * Level of the top event.
	 */
	protected int              level()
	{
		return stack().size() - 1;
	}

	protected SaxEvent<State>  event()
	{
		return stack().top();
	}

	protected State            state()
	{
		return stack().top().state();
	}

	protected State            state(int i)
	{
		return stack().get(i).state();
	}


	/* private: events stack */

	private SaxStack<State> stack;
}
