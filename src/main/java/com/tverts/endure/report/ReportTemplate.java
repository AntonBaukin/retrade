package com.tverts.endure.report;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;
import com.tverts.endure.cats.CatItem;


/**
 * A combination of two items: Data Source
 * ID, and the reporting system binary file.
 *
 * @author anton.baukin@gmail.com.
 */
public class ReportTemplate extends Entity implements CatItem
{
	public Domain getDomain()
	{
		return domain;
	}

	public void   setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCode()
	{
		return code;
	}

	public void   setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		this.name = name;
	}

	public boolean isSystem()
	{
		return system;
	}

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	/**
	 * The Data Source ID.
	 */
	public String getDid()
	{
		return did;
	}

	public void   setDid(String dataSource)
	{
		this.did = dataSource;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public byte[] getTemplate()
	{
		return template;
	}

	public void   setTemplate(byte[] template)
	{
		this.template = template;
	}


	/* persisted attributes */

	private Domain  domain;
	private String  code;
	private String  name;
	private boolean system;
	private String  remarks;
	private String  did;
	private byte[]  template;
}