package com.tverts.faces;

/* JavaServer Faces */

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Provides system infrastructure for the views.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ViewWithModes
{
	/* constants */

	public static final String VIEWID_PARAM = "view";

	public static final String VMODE_PARAM  = "mode";


	/* public: ViewWithModes (view mode) interface */

	public ViewMode    getViewMode()
	{
		if(viewMode != null)
			return viewMode;

		viewMode = obtainViewMode();
		if(viewMode == null)
			viewMode = ViewMode.PAGE;
		return viewMode;
	}

	public String      getViewModeStr()
	{
		return getViewMode().toString().toLowerCase();
	}

	/**
	 * Detects what mode to use to send POST requests
	 * from the current view mode.
	 */
	public ViewMode    getViewModePost()
	{
		switch(getViewMode())
		{
			case PAGE: return ViewMode.PAGE_POST;
			case BODY: return ViewMode.BODY_POST;
			default  : return getViewMode();
		}
	}

	public String      getViewModePostStr()
	{
		return getViewModePost().toString().toLowerCase();
	}

	public boolean     isViewModePage()
	{
		return ViewMode.PAGE.equals(getViewMode());
	}

	/**
	 * This view mode is for Ajax request to the components
	 * placed in page mode. This components do receive all
	 * the Faces phases except the render one.
	 *
	 * This method tells {@code true} only in regular page
	 * mode, or in page post mode.
	 */
	public boolean     isViewModePageRendered()
	{
		if(isViewModePage())
			return true;

		if(!isViewModePagePost())
			return false;

		return !isFacesRenderPhase();
	}

	public boolean     isViewModePagePost()
	{
		return ViewMode.PAGE_POST.equals(getViewMode());
	}

	public boolean     isViewModeBody()
	{
		return ViewMode.BODY.equals(getViewMode());
	}

	/**
	 * Having the same idea as page mode rendered,
	 * this method supports only body and body post
	 * modes. In body post mode it tell {@code false}
	 * on the render phase.
	 */
	public boolean     isViewModeBodyRendered()
	{
		if(isViewModeBody())
			return true;

		if(!isViewModeBodyPost())
			return false;

		return !isFacesRenderPhase();
	}

	public boolean     isViewModeBodyPost()
	{
		return ViewMode.BODY_POST.equals(getViewMode());
	}

	public boolean     isFacesRenderPhase()
	{
		return PhaseId.RENDER_RESPONSE.equals(
		  FacesContext.getCurrentInstance().getCurrentPhaseId());
	}


	/* public: ViewWithModes (parameter names) interface */

	public String      getViewIdParam()
	{
		return VIEWID_PARAM;
	}

	public String      getViewModeParam()
	{
		return VMODE_PARAM;
	}


	/* protected: view support interface */

	protected String   obtainRequestedViewId()
	{
		return s2s(request().getParameter(getViewIdParam()));
	}

	protected ViewMode obtainViewMode()
	{
		return obtainRequestViewMode();
	}

	protected ViewMode obtainRequestViewMode()
	{
		String vmp = s2s(request().getParameter(getViewModeParam()));
		if(vmp == null) return null;

		for(ViewMode vm : ViewMode.values())
			if(vm.toString().equalsIgnoreCase(vmp))
				return vm;
		return null;
	}


	/* private: the view state */

	private ViewMode viewMode;
}