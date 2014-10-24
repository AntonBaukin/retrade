package com.tverts.retrade.domain.doc;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade domain (invoices + sells) */

import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.sells.Sells;

/* com.tverts: retrade models data */

import com.tverts.retrade.data.other.DocumentsModelData;


/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.SU;
import com.tverts.support.jaxb.DateAdapter;


/**
 * Parts of a model that selected the documents,
 * or document related instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "docs-model")
public class DocsSearchModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date         getMinDate()
	{
		return minDate;
	}

	public void         setMinDate(Date minDate)
	{
		if(minDate != null)
			minDate = DU.cleanTime(minDate);
		this.minDate = minDate;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date         getMaxDate()
	{
		return maxDate;
	}

	public void         setMaxDate(Date maxDate)
	{
		if(maxDate != null)
			maxDate = DU.lastTime(maxDate);
		this.maxDate = maxDate;
	}

	public Set<String>  getDocTypes()
	{
		if(docTypes != null)
			return docTypes;

		docTypes = new HashSet<String>(
		  getDocTypesLabels().keySet());

		return docTypes;
	}

	public void         setDocTypes(Set<String> docTypes)
	{
		this.docTypes = docTypes;
	}

	public String       getDocTypesStr()
	{
		return SU.cat(null, ",", getDocTypes()).toString();
	}

	public void         setDocTypesStr(String s)
	{
		getDocTypes().clear();
		getDocTypes().addAll(Arrays.asList(SU.s2a(s, ',')));
	}

	public Map<String, String>
	                    getDocTypesLabels()
	{
		if(docTypesLabels != null)
			return docTypesLabels;

		Map<String, String> r = new LinkedHashMap<String, String>(5);

		r.put("ib",  "Накладные закупки");
		r.put("is",  "Накладные продажи");
		r.put("isa", "Накладные продажи авто-пр.");
		r.put("ips", "Накладные POS-продаж");
		r.put("im",  "Накладные перемещения");
		r.put("apr", "Накладные авто-производства");
		r.put("fpr", "Накладные производства");
		r.put("cor", "Накладные корректировки");
		r.put("vc",  "Инвентаризация остатков");
		r.put("sps", "Сессии POS-продаж");

		docTypesMax = r.size();
		return docTypesLabels = r;
	}

	public void         setDocTypesLabels(Map<String, String> dtls)
	{
		this.docTypesLabels = dtls;
	}

	public int          getDocTypesMax()
	{
		return docTypesMax;
	}

	public boolean      isEditState()
	{
		return editState;
	}

	public void         setEditState(boolean editState)
	{
		this.editState = editState;
	}

	public boolean      isFixedState()
	{
		return fixedState;
	}

	public void         setFixedState(boolean fixedState)
	{
		this.fixedState = fixedState;
	}

	public Long         getDocOwnerKey()
	{
		return docOwnerKey;
	}

	public void         setDocOwnerKey(Long docOwnerKey)
	{
		this.docOwnerKey = docOwnerKey;
	}

	public Class        getDocOwnerClass()
	{
		return docOwnerClass;
	}

	public void         setDocOwnerClass(Class docOwnerClass)
	{
		this.docOwnerClass = docOwnerClass;
	}

	public String       getDocOwnerType()
	{
		return docOwnerType;
	}

	public void         setDocOwnerType(String docOwnerType)
	{
		this.docOwnerType = docOwnerType;
	}

	public String       getSelSet()
	{
		return selSet;
	}

	public void         setSelSet(String selSet)
	{
		this.selSet = selSet;
	}

	public boolean      isWithSelSet()
	{
		return withSelSet;
	}

	public void         setWithSelSet(boolean withSelSet)
	{
		this.withSelSet = withSelSet;
	}


	/* public: data selection support */

	/**
	 * Null result means not to restrict the selection
	 * by the types at all.
	 */
	public Long[]       selectDocTypes()
	{
		if(getDocTypes().size() == getDocTypesMax())
			return null;

		Collection<UnityType> s = mapDocTypes(getDocTypes()).values();
		int i = 0; Long[]     r = new Long[s.size()];

		for(UnityType t : s)
			r[i++] = t.getPrimaryKey();

		return r;
	}

	public Map<String, UnityType>
	                    mapDocTypes(Collection<String> keys)
	{
		String                 key;
		Map<String, UnityType> res =
		  new HashMap<String, UnityType>(7);

		if(keys.contains(key = "ib"))
			res.put(key, Invoices.typeInvoiceBuy());

		if(keys.contains(key = "is"))
			res.put(key, Invoices.typeInvoiceSell());

		if(keys.contains(key = "isa"))
			res.put(key, Invoices.typeInvoiceSellAlt());

		if(keys.contains(key = "im"))
			res.put(key, Invoices.typeInvoiceMove());

		if(keys.contains(key = "apr"))
			res.put(key, Invoices.typeInvoiceAutoProduce());

		if(keys.contains(key = "fpr"))
			res.put(key, Invoices.typeInvoiceFreeProduce());

		if(keys.contains(key = "cor"))
			res.put(key, Invoices.typeInvoiceCorrection());

		if(keys.contains(key = "ips"))
			res.put(key, Sells.typeSellsInvoice());

		if(keys.contains(key = "sps"))
			res.put(key, Sells.typeSellsSession());

		if(keys.contains(key = "vc"))
			res.put(key, Invoices.typeVolumeCheck());

		return res;
	}

	/**
	 * Null result means not to restrict the selection
	 * by the states at all.
	 */
	public Long[]       selectDocStates()
	{
		if(isEditState() == isFixedState())
			return null;

		if(isEditState())
			return new Long[]{ Invoices.typeInvoiceStateEdited().getPrimaryKey() };
		else
			return new Long[]{ Invoices.typeInvoiceStateFixed().getPrimaryKey() };
	}

	public void         retainDocTypes(UnityType... types)
	{
		//~: map all the types existing
		Map<String, UnityType> map = mapDocTypes(getDocTypes());

		//~: retain only them
		map.values().retainAll(
		  new HashSet<UnityType>(Arrays.asList(types))
		);

		this.setDocTypes(new HashSet<String>(map.keySet()));
		getDocTypesLabels().keySet().retainAll(map.keySet());
	}

	public void         offDocTypes(UnityType... types)
	{
		//~: map all the types existing
		Map<String, UnityType> map = mapDocTypes(getDocTypes());

		//~: remove them
		map.values().removeAll(
		  new HashSet<UnityType>(Arrays.asList(types))
		);

		this.setDocTypes(new HashSet<String>(map.keySet()));
	}


	/* public: ModelBean (data access) interface */

	public ModelData    modelData()
	{
		return new DocumentsModelData(this);
	}


	/* search attributes */

	private Date        minDate;
	private Date        maxDate;

	private Set<String> docTypes;
	private boolean     editState  = true;
	private boolean     fixedState = true;

	private Long        docOwnerKey;
	private Class       docOwnerClass;
	private String      docOwnerType;

	private String      selSet;
	private boolean     withSelSet;

	private Map<String, String> docTypesLabels;
	private int                 docTypesMax;
}