package com.tverts.faces;

/* standard Java classes */

import java.util.Arrays;

/* JavaServer Faces */

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.s2a;


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


	/* public: ViewWithModes (id) interface */

	public String      getId()
	{
		return (this.id != null)?(this.id):
		  (this.id = obtainViewId());
	}


	/* public: security issues */

	public Domain      getDomain()
	{
		return SecPoint.loadDomain();
	}

	public boolean     isSecure(String key)
	{
		return SecPoint.isSecure(key);
	}

	public boolean     isSecureEntity(NumericIdentity e, String key)
	{
		return SecPoint.isSecure(e.getPrimaryKey(), key);
	}

	public boolean     isSecureEntityKey(Long pk, String key)
	{
		return SecPoint.isSecure(pk, key);
	}

	public void        forceSecure(String key)
	{
		if(!SecPoint.isSecure(key))
			throw EX.forbid();
	}

	public void        forceSecureEntity(NumericIdentity e, String key)
	{
		if(!SecPoint.isSecure(e.getPrimaryKey(), key))
			throw EX.forbid();
	}

	public void        forceSecureEntityKey(Long pk, String key)
	{
		if(!SecPoint.isSecure(pk, key))
			throw EX.forbid();
	}

	/**
	 * Checks whether any of the keys given is secure.
	 * All the keys are encoded into single string and
	 * separated with ';' character.
	 */
	public boolean     isAnySecure(String keys)
	{
		return SecPoint.isAnySecure(Arrays.asList(s2a(keys, ';')));
	}

	public boolean     isAnySecureEntity(NumericIdentity e, String keys)
	{
		return SecPoint.isAnySecure(
		  e.getPrimaryKey(), Arrays.asList(s2a(keys, ';')));
	}

	public boolean     isAllSecure(String keys)
	{
		return SecPoint.isAllSecure(Arrays.asList(s2a(keys, ';')));
	}

	public boolean     isAllSecureEntity(NumericIdentity e, String keys)
	{
		return SecPoint.isAllSecure(
		  e.getPrimaryKey(), Arrays.asList(s2a(keys, ';')));
	}

	public void        forceAnySecure(String keys)
	{
		if(!isAnySecure(keys))
			throw EX.forbid();
	}

	public void        forceAnySecureEntity(NumericIdentity e, String keys)
	{
		if(!isAnySecureEntity(e, keys))
			throw EX.forbid();
	}


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
		return isViewModePage() ||
		  isViewModePagePost() && !isFacesRenderPhase();
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
		return isViewModeBody() ||
		  isViewModeBodyPost() && !isFacesRenderPhase();
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

	protected String   obtainViewId()
	{
		//~: take value from the request
		String id = obtainRequestedViewId();
		if(id != null) return id;

		//~: ask for effective id
		id = bean(RootView.class).getEffectiveViewId();
		if(id == null) throw EX.state(
		  "No effective Faces View ID was generated!");

		return id;
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

	private String   id;
	private ViewMode viewMode;
}