package com.tverts.endure.report;

/* com.tverts: endure (core, catalogues) */

import com.tverts.endure.Remarkable;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;
import com.tverts.endure.cats.CatItem;


/**
 * A combination of two items: Data Source
 * ID, and the reporting system binary file.
 *
 * @author anton.baukin@gmail.com.
 */
public class      ReportTemplate
       extends    Entity
       implements CatItem, Remarkable
{
	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isSystem()
	{
		return system;
	}

	private boolean system;

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

	private String did;

	public void setDid(String dataSource)
	{
		this.did = dataSource;
	}

	public String getRemarks()
	{
		return remarks;
	}

	private String remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public boolean isReady()
	{
		return ready;
	}

	private boolean ready;

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	public byte[] getTemplate()
	{
		return template;
	}

	private byte[] template;

	public void setTemplate(byte[] template)
	{
		this.template = template;
		this.ready = (template != null);
	}
}