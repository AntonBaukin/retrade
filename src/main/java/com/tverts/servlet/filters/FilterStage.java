package com.tverts.servlet.filters;

/**
 * A stage of Servlet Filters invocation.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public enum FilterStage
{
	/**
	 * The stage of processing the initial HTTP
	 * request issued to the server.
	 */
	REQUEST,

	/**
	 * Internally included resource (subquery).
	 */
	INCLUDE,

	/**
	 * Internal forward redirection.
	 */
	FORWARD,

	/**
	 * Error response handling.
	 */
	ERROR
}