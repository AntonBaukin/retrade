package com.tverts.retrade.web.views.firms;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

//import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: models */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.FirmEntity;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.ContractorModelBean;

/* com.tverts: support */

//import com.tverts.support.EX;
//import com.tverts.support.SU;


/**
 * Controller for viewing, editing, and creating
 * {@link FirmEntity} instances and the related
 * {@link Contractor}s.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesFirmViewEdit extends UnityModelView
{

	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new ContractorModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ContractorModelBean);
	}
}