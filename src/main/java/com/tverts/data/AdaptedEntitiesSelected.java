package com.tverts.data;

/* Java XML Binding */


/* com.tverts: endure (core) */

import javax.xml.bind.annotation.XmlTransient;

import com.tverts.endure.UnityType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: objects */

import com.tverts.objects.Adapter;

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
public class AdaptedEntitiesSelected extends DataSelectModelBean
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

	public String getSelSet()
	{
		return selSet;
	}

	public void setSelSet(String selSet)
	{
		this.selSet = SU.sXs(SU.s2s(selSet));
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

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}


	/* public: initialization */

	public AdaptedEntitiesSelected init
	  (String dataSource, Long domain, Long login, UnityType type, Adapter adapter)
	{
		//~: data source
		this.dataSource = EX.asserts(dataSource);

		//~: domain
		setDomain(EX.assertn(domain));

		//~: the login
		this.login = EX.assertn(login);

		//~: the unity type of the entities
		this.unityType = EX.assertn(type).getPrimaryKey();

		//~: the adaptor
		this.adapter = EX.assertn(adapter);

		return this;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new AdaptedEntitiesData(this);
	}


	/* private: the model state */

	private String  dataSource;
	private String  selSet;
	private Long    login;
	private Long    unityType;
	private Long    entity;
	private Adapter adapter;
	private String  orderBy;
}