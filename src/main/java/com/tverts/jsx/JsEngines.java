package com.tverts.jsx;

/* Java */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Collections of Engines related to the
 * scripts defined by their files.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsEngines
{
	/* Engines Collection */

	public JsEngine take(JsFile file)
	{
		return null;
	}

	protected final Map<JsFile, JsEngine> engines =
	  new ConcurrentHashMap<>();

	public void     free(JsFile file)
	{
	}
}