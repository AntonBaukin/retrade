package com.tverts.faces;

/* com.tverts: model */

import com.tverts.model.ModelAccessPoint;
import com.tverts.model.ModelPoint;


/**
 * An implementation base for JavaServer Faces views.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelViewBase
{
	/* protected: view support interface */

	protected ModelPoint modelPoint()
	{
		return ModelAccessPoint.model();
	}
}