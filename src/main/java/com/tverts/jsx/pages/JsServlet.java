package com.tverts.jsx.pages;

/* Java Servlets */


/* com.tverts: jsx */

import com.tverts.jsx.JsCtx;
import com.tverts.jsx.JsX;


/**
 * This servlet forwards the processing to
 * JavaScript file having '.jsx' extension.
 *
 * It prepares the execution context and invokes
 * {@link JsX#invoke(String, String, JsCtx, Object...)}
 * having the first argument the script requested,
 * second argument the name of the HTTP method
 * in the lower case (get, post, put, and else),
 * and the call arguments are Servlet request
 * and response objects.
 *
 * Servlet input stream is wrapped with Reader
 * in the encoding of the body request. Do
 * not call it in the case of binary stream.
 * To access the input Reader in the script
 * call {@code JsX.in()}.
 *
 * Response may be written directly from the
 * Servlet response object, or via wrapped Writer.
 * To access it call {@code JsX.out()}.
 *
 * Any error text written to error Writer via
 * {@code JsX.err()} has priority over the
 * ordinary output text and responded with
 * server internal error 500 status.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class JsServlet
{
}