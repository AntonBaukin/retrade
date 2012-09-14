package com.tverts.support.xml;

/**
 * Stack of {@link SaxEvent}s of SAX processing
 * an XML document.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface SaxStack<State>
{
	/* public: SaxStack interface */

	public SaxEvent<State> top();

	/**
	 * Returns stack event in the position given.
	 * Zero position points to the root tag event,
	 * the top event has (size - 1) position.
	 */
	public SaxEvent<State> get(int i);

	public SaxEvent<State> pop();

	public SaxStack<State> push(SaxEvent<State> e);

	public int             size();

	public boolean         empty();
}
