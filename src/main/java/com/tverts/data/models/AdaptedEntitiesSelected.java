package com.tverts.data.models;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: hibery */

import com.tverts.data.DataCtx;
import com.tverts.data.ReportFormat;
import com.tverts.data.ReportModel;
import com.tverts.hibery.qb.TuneQuery;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: objects */

import com.tverts.objects.Adapter;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * This dedicated Model Bean is for select single
 * entity of the configured Unity Type from the list
 * of the entities in the selection set provided.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
@XmlType(name = "adapted-entities-selected-model")
public class      AdaptedEntitiesSelected
       extends    DataSelectModelBean
       implements ReportModel
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	public Long getTemplate()
	{
		return template;
	}

	public void setTemplate(Long template)
	{
		this.template = template;
	}

	public ReportFormat getFormat()
	{
		return (format == null)?(ReportFormat.PDF):(format);
	}

	public void setFormat(ReportFormat format)
	{
		this.format = format;
	}

	public String getSelSet()
	{
		return selSet;
	}

	public void setSelSet(String selSet)
	{
		this.selSet = SU.sXs(SU.s2s(selSet));
	}

	@XmlTransient
	public String getSecSession()
	{
		return secSession;
	}

	public void setSecSession(String secSession)
	{
		this.secSession = secSession;
	}

	public Long getLogin()
	{
		return login;
	}

	public void setLogin(Long login)
	{
		this.login = login;
	}

	public Long getUnityType()
	{
		return unityType;
	}

	public void setUnityType(Long unityType)
	{
		this.unityType = unityType;
	}

	public Long getEntity()
	{
		return entity;
	}

	public void setEntity(Long entity)
	{
		this.entity = entity;
	}

	@XmlTransient
	public Adapter getAdapter()
	{
		return adapter;
	}

	public void setAdapter(Adapter adapter)
	{
		this.adapter = adapter;
	}

	@XmlTransient
	public TuneQuery getQuery()
	{
		return query;
	}

	public void setQuery(TuneQuery query)
	{
		this.query = query;
	}


	/* public: initialization */

	public AdaptedEntitiesSelected init(String dataSource, DataCtx ctx, UnityType type)
	{
		//~: data source
		this.dataSource = EX.asserts(dataSource);

		//~: domain
		setDomain(EX.assertn(ctx.getDomain()));

		//~: secure session
		this.secSession = EX.asserts(ctx.getSecSession());

		//~: the login
		this.login = EX.assertn(ctx.getLogin());

		//~: the unity type of the entities
		this.unityType = EX.assertn(type).getPrimaryKey();

		return this;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new AdaptedEntitiesData(this);
	}


	/* private: the model state */

	private String       dataSource;
	private Long         template;
	private ReportFormat format;
	private String       selSet;
	private String       secSession;
	private Long         login;
	private Long         unityType;
	private Long         entity;
	private Adapter      adapter;
	private TuneQuery    query;
}