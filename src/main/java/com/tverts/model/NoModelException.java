package com.tverts.model;

/**
 * While processing HTTP request, it no model reference
 * provided, or the reference is for obsolete model instance,
 * the vew implementation may raise this exception to redirect
 * the request to the actual model reference.
 *
 * This exception is handled by {@link NoModelFilter}.
 *
 * Note that this error must be raised before any printing
 * to the HTTP response, or redirection header may be skipped.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class NoModelException extends RuntimeException
{
	/* public: constructor */

	public NoModelException(String message)
	{
		super(message);
	}

	public NoModelException(ModelBean model)
	{
		this.model = model;
	}


	/* public: NoModelException interface */

	public ModelBean getModel()
	{
		return model;
	}


	/* private: actual model reference */

	private ModelBean model;
}