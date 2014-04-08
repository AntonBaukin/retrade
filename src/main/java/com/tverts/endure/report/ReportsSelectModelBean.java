package com.tverts.endure.report;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Intermediate model to display and search
 * existing Report Templates.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlType
public abstract class ReportsSelectModelBean
       extends        DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String[] getSearchWords()
	{
		return SU.sXe(searchWordsStr)?(null):(SU.s2s(searchWordsStr).split("\\s+"));
	}

	public String getSearchWordsStr()
	{
		return searchWordsStr;
	}

	public void setSearchWordsStr(String s)
	{
		this.searchWordsStr = SU.s2s(s);
	}


	/* private: model attributes */

	private String searchWordsStr;
}