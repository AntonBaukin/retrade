package com.tverts.faces;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
@ManagedBean @RequestScoped
public abstract class ViewWithModes
{
	/* constants */

	public static final String VMODE_PARAM = "mode";


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

	public String      getViewModeParam()
	{
		return VMODE_PARAM;
	}

	public boolean     isViewModePage()
	{
		return ViewMode.PAGE.equals(getViewMode());
	}

	/**
	 * This tricky method tells that component may be
	 * rendered in every JSF phase when page view mode,
	 * but not rendered on render phase in any other mode.
	 *
	 * This allows to tell that the component is seen when
	 * applying request parameters, but not render it further.
	 */
	public boolean     isViewModePageRendered()
	{
		if(isViewModePage())
			return true;

		PhaseId phase = FacesContext.getCurrentInstance().
		  getCurrentPhaseId();
		return !PhaseId.RENDER_RESPONSE.equals(phase);
	}

	public boolean     isViewModeAjaxPost()
	{
		return ViewMode.AJAX_POST.equals(getViewMode());
	}


	/* protected: view support interface */

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