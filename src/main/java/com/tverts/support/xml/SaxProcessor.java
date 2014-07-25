package com.tverts.support.xml;

/* Java */

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;

/* SAX Parser */

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


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


	/* protected: document handler implementation */

	protected class SaxHandler extends DefaultHandler
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
			//?: {collect tags}
			if(collectTags)
				tag(stack().top());

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


	/* protected: access tags */

	/**
	 * Set this flag to collect the text
	 * values of all the tags processed.
	 * Tags are used to simplify assigning
	 * object attributes.
	 */
	protected boolean collectTags;

	protected String      tag(String name)
	{
		return (tags == null)?(null):(tags.get(name));
	}

	protected void        tag(String name, String text)
	{
		//?: {has no text} delete the tag
		if((text = SU.s2s(text)) == null)
		{
			if(tags != null)
				tags.remove(name);
		}
		//~: {text is not empty} add tag
		else
		{
			if(tags == null)
				tags = new HashMap<String, String>(11);

			tags.put(name, text);
		}
	}

	protected void        tag(SaxEvent<State> e)
	{
		tag(e.tag(), e.text());
	}

	protected Set<String> tags()
	{
		return tags.keySet();
	}

	protected void        clearTags(Collection<String> names)
	{
		if(names == null)
			this.tags = null;
		else if(tags != null)
			for(String n : names)
				tags.remove(n);
	}

	protected void        requireTags(String... names)
	{
		for(String name : names) EX.assertx(
		  (tags != null) && tags.containsKey(name),
		  "Required text tag <", name, "> does not present!"
		);
	}

	protected void        fillWithTags(Object obj, Collection<String> used)
	{
		EX.assertn(obj);

		//?: {has no tags}
		if((tags == null) || tags.isEmpty())
			return;

		//~: collect the names
		Map<String, String> names =
		  new HashMap<String, String>(tags.size());
		for(String tag : tags.keySet())
			names.put(SU.camelize(tag), tag);

		//~: process the objects
		try
		{
			PropertyDescriptor[] pds = Introspector.
			  getBeanInfo(obj.getClass()).getPropertyDescriptors();

			for(PropertyDescriptor pd : pds)
			{
				Method wm = pd.getWriteMethod();
				if(wm == null) continue;

				String tag   = names.get(pd.getName());
				if(tag == null) continue;

				String text  = EX.asserts(tags.get(tag));
				Object value = SU.s2v(pd.getPropertyType(), text);
				EX.assertn(value);

				wm.invoke(obj, value);

				//?: {collect used names}
				if(used != null)
					used.add(tag);
			}
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}

	protected void        requireFillClearTags(Object obj, boolean all, String... required)
	{
		//~: check the tags required
		requireTags(required);

		//~: set the properties
		Set<String> used = (all)?(null):(new HashSet<String>(required.length));
		fillWithTags(obj, used);

		//~: remove the tags used
		clearTags(used);
	}


	/* protected: helpers */

	protected final SaxStack<State> stack()
	{
		return (this.stack != null)?(this.stack):
		  (this.stack = createStack());
	}

	/**
	 * Level of the top event.
	 */
	protected final int        level()
	{
		return stack().size() - 1;
	}

	protected final boolean    islevel(int level)
	{
		return (stack().size() - 1) == level;
	}

	protected final boolean    istag(String name)
	{
		return stack().top().istag(name);
	}

	/**
	 * Checks the tags nesting started from the level given.
	 */
	protected final boolean    istag(int level, String... names)
	{
		if(level >= stack.size())
			return false;

		for(int i = 0;(i < names.length);i++)
			if(!stack.get(level + i).istag(names[i]))
				return false;

		return true;
	}

	protected final String     attr(String qname)
	{
		return stack().top().attr(qname);
	}

	protected final SaxEvent<State> event()
	{
		return stack().top();
	}

	protected final State      state()
	{
		return stack().top().state();
	}

	protected final State      state(int i)
	{
		return stack().get(i).state();
	}

	protected RuntimeException wrong()
	{
		return EX.state(
		  "Unknown (wrong) tag at level [",
		  level(), "]: <", event().tag(), ">!"
		);
	}


	/* private: events stack + tags */

	private SaxStack<State>     stack;
	private Map<String, String> tags;
}
