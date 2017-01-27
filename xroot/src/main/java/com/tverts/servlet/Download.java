package com.tverts.servlet;

/* Java Servlets */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Download strategy acts like a HTTP Servlet
 * as it has direct access to those objects.
 *
 * May be implemented by objects of various
 * implementations: such as Spring Beans,
 * Models and so. See {@link DownloadServlet}
 * for the details.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Download
{
	/* public: Download interface */

	public void download(HttpServletRequest req, HttpServletResponse res)
	  throws java.io.IOException, ServletException;
}