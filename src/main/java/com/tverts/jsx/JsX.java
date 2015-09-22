package com.tverts.jsx;

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

	protected JsFiles files;

	/**
	 * Configures the root packages (directories)
	 * to search for JavaScript files from using
	 * the application Class Loader.
	 *
	 * The names may be presented in Java package
	 * dot-separator notation, or with URI's '/'.
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
		this.files = new JsFiles(rs);
	}


	/* Scripts Execution */




	/* protected: support */

	protected String getLog()
	{
		return this.getClass().getName();
	}
}