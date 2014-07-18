package com.tverts.retrade.web.views.system;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ViewWithModes;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: retrade views */

import com.tverts.retrade.web.views.FacesDesktopView;


/**
 * JavaServer Faces view for index page of
 * special System domain.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSystemDesktopView extends FacesDesktopView
{
	/* public: checks */

	public String getCheckSystemDomain()
	{
		SecPoint.checkSystemDomain();
		return "";
	}
}