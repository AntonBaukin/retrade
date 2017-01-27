package com.tverts.data.models;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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
import com.tverts.support.IO;


/**
 * This dedicated Model Bean is for select single
 * entity of the configured Unity Type from the list
 * of the entities in the selection set provided.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "adapted-entities-selected-model")
public class      AdaptedEntitiesSelected
       extends    DataSelectModelBean
       implements ReportModel
{
	/* Adapted Entities Selected */

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


	/* Initialization */

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


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new AdaptedEntitiesData(this);
	}


	/* private: encapsulated data */

	private String       dataSource;
	private Long         template;
	private ReportFormat format;
	private String       secSession;
	private Long         login;
	private Long         unityType;
	private Long         entity;
	private Adapter      adapter;
	private TuneQuery    query;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.str(o, dataSource);
		IO.longer(o, template);
		IO.obj(o, format);
		IO.str(o, secSession);
		IO.longer(o, login);
		IO.longer(o, unityType);
		IO.longer(o, entity);
		IO.obj(o, adapter);
		IO.obj(o, query);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		dataSource = IO.str(i);
		template   = IO.longer(i);
		format     = IO.obj(i, ReportFormat.class);
		secSession = IO.str(i);
		login      = IO.longer(i);
		unityType  = IO.longer(i);
		entity     = IO.longer(i);
		adapter    = IO.obj(i, Adapter.class);
		query      = IO.obj(i, TuneQuery.class);
	}
}