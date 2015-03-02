package com.tverts.retrade.web.views.firms;

/* Java */

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: models */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.FirmEntity;

/* com.tverts: retrade api */

import com.tverts.api.clients.Firm;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.ActContractor;
import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.firm.ContractorModelBean;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


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
	/* Actions */

	public String doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		codeExists = (code == null) || !checkCodeExists(code);
		return null;
	}

	public String doSubmitEdit()
	{
		//sec: is allowed to edit
		forceSecureModelEntity("edit");

		//?: {code exists}
		EX.asserts(getFirm().getCode());
		if(codeExists = !checkCodeExists(getFirm().getCode()))
			return null;

		//=: firm code
		getEntity().setCode(getFirm().getCode());

		//=: firm name
		EX.asserts(getFirm().getName());
		getEntity().setName(getFirm().getName());

		//?: {firm full name is set}
		EX.asserts(getFirm().getFullName());

		//?: {has proper types}
		EX.assertn(firmType);
		EX.assertx(firmType.size() == 1);
		String type = firmType.iterator().next();
		EX.assertx(getFirmTypeLabels().containsKey(type));

		//=: firm type
		getFirm().setType(type);

		//~: update firm ox-object
		getEntity().getFirm().updateOx();

		//?: {editing} update
		if(isEdit())
			actionRun(ActionType.UPDATE, getEntity());
		else
			actionRun(ActionType.SAVE, getEntity(),
			  ActContractor.SAVE_FIRM, true
			);

		return null;
	}


 	/* Faces Interface */

	public ContractorModelBean getModel()
	{
		return (ContractorModelBean) super.getModel();
	}

	public Contractor getEntity()
	{
		return (Contractor) super.getEntity();
	}

	public Firm       getFirm()
	{
		return (firm != null)?(firm):(firm = EX.assertn(getEntity().getFirm(),
		  "Contractor [", getEntity().getPrimaryKey(), "], code [",
		  getEntity().getCode(), "] has no Firm assigned!"
		).getOx());
	}

	private Firm firm;


	/* public: view [edit] interface */

	public boolean isEdit()
	{
		return (getModel().getPrimaryKey() != null);
	}

	public String getWinmainTitleEdit()
	{
		return "Добавление контрагента";
	}

	public boolean isValid()
	{
		return SU.sXe(errorEvent) && !codeExists;
	}

	protected String errorEvent;

	public String  getErrorEvent()
	{
		return errorEvent;
	}

	protected boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}

	public boolean checkCodeExists(String code)
	{
		Contractor c = bean(GetContractor.class).
		  getContractor(loadModelDomain(), code);

		return (c == null) || c.equals(getEntity());
	}


	/* public: view [info] interface */

	public String getWinmainTitle()
	{
		return isEdit()?(getWinmainTitleEdit()):(getWinmainTitleInfo());
	}

	public String getWinmainTitleInfo()
	{
		return SU.cats(
		  "Контрагент №", getEntity().getCode(),
		  ", ", getEntity().getName()
		);
	}

	public void forceSecure()
	{
		if(isEdit())
			forceSecure("create-contractor-firm");
		else
			forceSecureModelEntity("view");
	}

	public Map<String, String> getFirmTypeLabels()
	{
		if(firmTypeLabels != null)
			return firmTypeLabels;

		Map<String, String> m = firmTypeLabels =
		  new LinkedHashMap<String, String>(5);

		m.put("ОРГ", "Организация");
		m.put("ИП",  "Инд. Предприниматель");
		m.put("ГОС", "Гос. Учреждение");

		return firmTypeLabels;
	}

	private Map<String, String> firmTypeLabels;

	public Set<String> getFirmType()
	{
		if(firmType != null)
			return firmType;

		firmType = new HashSet<String>(1);
		if(getFirm().getType() == null)
			getFirm().setType("ОРГ");

		firmType = new HashSet<String>(1);
		firmType.add(getFirm().getType());

		return firmType;
	}

	private Set<String> firmType;

	public void setFirmType(Set<String> firmType)
	{
		this.firmType = firmType;
	}


	/* protected: ModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new ContractorModelBean();
	}

	/**
	 * Controller works in the edit mode.
	 */
	protected void fallbackModelKey(UnityModelBean model)
	{
		//~: create contractor instance
		Contractor c = new Contractor();

		//=: domain
		c.setDomain(loadDomain());

		//=: firm entity
		c.setFirm(new FirmEntity());

		//=: firm code
		c.getFirm().getOx().setCode(
		  Contractors.createContractorCode(c.getDomain())
		);
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ContractorModelBean);
	}
}