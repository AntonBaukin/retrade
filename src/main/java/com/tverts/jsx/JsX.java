package com.tverts.jsx;

/* Java */

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Entry point to JavaScript execution layer
 * based on Oracle Nashorn implementation.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsX
{
	/* Singleton  */

	public static JsX getInstance()
	{
		return INSTANCE;
	}

	public static final JsX INSTANCE =
	  new JsX();

	protected JsX()
	{}


	/* Java Bean (configuration) */

	protected String[] roots;

	/**
	 * Configures the root packages (directories)
	 * to search for JavaScript files from using
	 * the application Class Loader.
	 *
	 * The names may be presented in Java package
	 * dot-separator notation, or with Unix '/'.
	 */
	public void setRoots(String roots)
	{
		String[] rs = SU.s2aws(roots);
		EX.asserte(rs, "No JavaScript roots are given!");

		for(int i = 0;(i < rs.length);i++)
		{
			String r = rs[i];

			if(r.indexOf('/') == -1)
				r = r.replace('.', '/');
			if(!r.startsWith("/"))
				r = "/" + r;
			while(r.endsWith("/"))
				r = r.substring(0, r.length() - 1);

			rs[i] = EX.asserts(r);
		}

		LU.I(getLog(), "Using the wollowing roots: ", rs);
		this.roots = rs;
	}


	/* Scripts Execution */

	/**
	 * Searches for the script file having the name
	 * given related to some of the roots. All the
	 * roots are scanned to exclude duplicates.
	 *
	 * As the root name, the file path may contain
	 * dots as a package names separator.
	 */
	public JsFile find(String path)
	{
		EX.asserts(path);

		if(path.indexOf('/') == -1)
			path = path.replace('.', '/');
		if(!path.startsWith("/"))
			path = "/" + path;

		//!: use global class loader to search for
		ClassLoader cl = Thread.currentThread().
		  getContextClassLoader();

		List<URL> us = new ArrayList<>(1);
		for(String r : this.roots) try
		{
			us.addAll(Collections.list(
			  cl.getResources(r + path)));
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Can't access resource [", r + path, "]!");
		}

		if(us.isEmpty())
			return null;

		EX.assertx(us.size() == 1, "More than one script resource was found ",
		  "for path [", path, "]: ", us);

		try
		{
			return new JsFile(us.get(0).toURI());
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}


	/* protected: support */

	protected String getLog()
	{
		return this.getClass().getName();
	}
}