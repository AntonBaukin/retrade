package com.tverts.faces;

/* standard Java classes */

import java.util.concurrent.atomic.AtomicLong;

/* JavaServer Faces */

import javax.faces.component.UIForm;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelAccessPoint;
import com.tverts.model.ModelBean;
import com.tverts.model.ModelPoint;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * An implementation base for JavaServer Faces views.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelView extends ViewWithModes
{
	/* constants */

	/**
	 * Model key parameter in the HTTP request
	 */
	public static final String MODEL_PARAM  = "model";

	public static final String VIEWID_PARAM = "view";


	/* public: ModelView (view shared) interface */

	public String    getId()
	{
		return (this.id != null)?(this.id):
		  (this.id = obtainViewId());
	}

	public ModelBean getModel()
	{
		//?: {got the model reference}
		if(model != null) return model;

		//~: obtain the model referred by the HTTP parameter
		model = obtainModel();
		if(model != null) return model;

		//~: create the model
		model = createModel();

		//?: {had created it} save into the point
		if(model != null)
			modelPoint().addBean(model);

		//?: {the model is not defined}
		if(model == null) throw new IllegalStateException(String.format(
		  "User data Model bean instance can't be created for the view " +
		  "accessed by the URL [%s]!", request().getRequestURL().toString()));

		return model;
	}

	public String    getModelKey()
	{
		return getModel().getModelKey();
	}

	/**
	 * Tells that the model requested in the same model
	 * (with the same key) that is currently set for the view.
	 */
	public boolean   isModelRequested()
	{
		ModelBean model = obtainRequestModel();
		return (model != null) &&
		  model.getModelKey().equals(getModel().getModelKey());
	}

	public String    getModelParam()
	{
		return MODEL_PARAM;
	}

	public String    getViewIdParam()
	{
		return VIEWID_PARAM;
	}

	public UIForm    getForm()
	{
		return form;
	}

	public void      setForm(UIForm form)
	{
		this.form = form;
		if(form == null) return;

		form.setPrependId(false);
	}


	/* protected: view support interface */

	protected abstract ModelBean createModel();

	protected String             obtainViewId()
	{
		String id = obtainRequestViewId();
		if(id == null) id = genViewId();
		return id;
	}

	protected String             genViewId()
	{
		return String.format(
		  "v%x", VIEWID.incrementAndGet());
	}

	protected String             obtainRequestViewId()
	{
		return s2s(request().getParameter(getViewIdParam()));
	}

	protected ModelBean          obtainModel()
	{
		return obtainRequestModel();
	}

	protected ModelBean          obtainRequestModel()
	{
		String key = s2s(request().getParameter(getModelParam()));
		return (key == null)?(null):(modelPoint().readBean(key));
	}

	protected ModelPoint         modelPoint()
	{
		return ModelAccessPoint.model();
	}


	/* private: the view state */

	private String    id;
	private ModelBean model;
	private UIForm    form;


	/* private static: view ids generator  */

	private static AtomicLong VIEWID =
	  new AtomicLong();
}