package com.tverts.faces.system;

/* Java */

import java.io.IOException;

/* JavaServer Faces */

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

/* JavaServer Faces Reference Implementation */

import com.sun.faces.application.view.FaceletViewHandlingStrategy;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * JSF system level class. It's present goal is
 * to treat exceptions during the JSF processing.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   LocalFaceletViewHandlingStrategy
       extends FaceletViewHandlingStrategy
{
	/* protected: FaceletViewHandlingStrategy (error handling) */

	protected void handleRenderException(FacesContext context, Exception e)
	  throws IOException
	{
		//?: {this error must be logged}
		if(!EX.isTransparent(e))
		{
			super.handleRenderException(context, e);
			return;
		}

		//!: throw the error
		if(e instanceof RuntimeException)
			throw (RuntimeException)e;

		if(e instanceof IOException)
			throw (IOException)e;

		throw new FacesException(e);
	}
}