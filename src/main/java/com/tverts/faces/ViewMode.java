package com.tverts.faces;

/**
 * Defines various request processing and rendering modes.
 *
 *
 * @author anton.baukin@gmail.com
 */
public enum ViewMode
{
	/**
	 * Page view mode is the default one when HTTP GET
	 * request is asking to render regular HTML page.
	 */
	PAGE,

	/**
	 * In body mode only the body part (without body tag)
	 * of the page is displayed. Also no pages shared scripts
	 * are te be loaded.
	 */
	BODY,

	/**
	 * Means HTTPS POST request made via asynchronous call.
	 * The content of the page is not shown except the
	 * validation and request status results.
	 */
	AJAX_POST
}