package com.tverts.objects;

/**
 * Returns an instance of the classes this factory
 * is related with. The result may be a new private
 * instance, or a shared one.
 *
 * @author anton.baukin@gmail.com
 */
public interface ObjectFactory<O>
{
	/* public: ObjectFactory interface */

	/**
	 * This factory method takes the class of the desired
	 * instance. As a factory may be set to create the
	 * instances of hierarchy-related types (subclasses),
	 * this feature may be handy.
	 *
	 * A factory may return {@code null} when it is
	 * not able to obtain an instance of the class given
	 * instead of raising an exception.
	 */
	public <X extends O> X createInstance(Class<X> c);

	/**
	 * Obtains an instance of the default root class this
	 * factory is designed for. This call is equivalent
	 * to call to {@link #createInstance(Class)} with
	 * the class argument is {@code null}.
	 */
	public O               createInstance();
}