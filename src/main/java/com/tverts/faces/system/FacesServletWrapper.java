package com.tverts.faces.system;

/* standard Java classes */

import java.io.IOException;

/* Java Servlet api */

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/* JavaServer Faces */

import javax.faces.webapp.FacesServlet;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


/**
 * Wraps {@link FacesServlet} to bind
 * System Class Loader.
 *
 * @author anton.baukin@gmail.com
 */
public class FacesServletWrapper implements Servlet
{
	/* public: Servlet interface */

	public void init(ServletConfig config)
	  throws ServletException
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: create Faces servlet
			if(servlet != null)
				throw new IllegalStateException();
			servlet = new FacesServlet();

			//~: initialize Faces servlet
			servlet.init(config);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void   destroy()
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: destroy Faces servlet
			servlet.destroy();
		}
		finally
		{
			servlet = null;

			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public ServletConfig getServletConfig()
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			return servlet.getServletConfig();
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void service(ServletRequest req, ServletResponse res)
	  throws ServletException, IOException
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			servlet.service(req, res);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public String getServletInfo()
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			return servlet.getServletInfo();
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* private: faces servlet */

	private volatile Servlet servlet;
}