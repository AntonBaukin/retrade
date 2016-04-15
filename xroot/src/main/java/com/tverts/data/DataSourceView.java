package com.tverts.data;

/* Java */

import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A read view of a Data Source.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "data-source")
public class DataSourceView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String getDid()
	{
		return did;
	}

	public void setDid(String did)
	{
		this.did = did;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNameLo()
	{
		return nameLo;
	}

	public void setNameLo(String nameLo)
	{
		this.nameLo = nameLo;
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	public String getDescrLo()
	{
		return descrLo;
	}

	public void setDescrLo(String descrLo)
	{
		this.descrLo = descrLo;
	}

	@XmlElement
	public boolean isSystem()
	{
		return system;
	}


	/* public: initialization */

	public DataSourceView init(DataSource ds)
	{
		this.did = ds.did();
		this.name = ds.getName();
		this.nameLo = ds.getNameLo();
		this.descr = ds.getDescr();
		this.descrLo = ds.getDescrLo();
		this.system = ds.isSystem();

		return this;
	}


	/* private: the view */

	private String  did;
	private String  name;
	private String  nameLo;
	private String  descr;
	private String  descrLo;
	private boolean system;
}