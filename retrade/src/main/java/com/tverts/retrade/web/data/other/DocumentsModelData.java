package com.tverts.retrade.web.data.other;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (documents, firms) */

import com.tverts.retrade.domain.doc.DocView;
import com.tverts.retrade.domain.doc.DocsSearchModelBean;
import com.tverts.retrade.domain.doc.GetDocumentView;


/**
 * Data provider for view all the Domain documents model bean.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "documentsNumber", "documents"})
public class DocumentsModelData implements ModelData
{
	/* public: constructors */

	public DocumentsModelData()
	{}

	public DocumentsModelData(DocsSearchModelBean model)
	{
		this.model = model;
	}


	/* public: data bean interface */

	@XmlElement
	public DocsSearchModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getDocumentsNumber()
	{
		return bean(GetDocumentView.class).countDocuments(getModel());
	}

	@XmlElement(name = "document")
	@XmlElementWrapper(name = "documents")
	@SuppressWarnings("unchecked")
	public List<DocView> getDocuments()
	{
		List          sel = bean(GetDocumentView.class).selectDocuments(getModel());
		List<DocView> res = new ArrayList<DocView>(sel.size());

		for(Object rec : sel)
			res.add(new DocView().init(rec));

		return res;
	}


	/* private: model */

	private DocsSearchModelBean model;
}