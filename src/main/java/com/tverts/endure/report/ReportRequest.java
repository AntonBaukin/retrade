package com.tverts.endure.report;

/* com.tverts: endure (core) */

import java.util.Date;

import com.tverts.endure.NumericBase;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Instance of report requested by a user.
 *
 * @author anton.baukin@gmail.com.
 */
public class      ReportRequest
       extends    NumericBase
       implements DomainEntity
{
	public Domain getDomain()
	{
		return (template == null)?(null):(template.getDomain());
	}

	public ReportTemplate getTemplate()
	{
		return template;
	}

	public void setTemplate(ReportTemplate template)
	{
		this.template = template;
	}

	/**
	 * The user had issued this request.
	 * For internal reports must refer
	 * the system user ('System' login).
	 */
	public AuthLogin getOwner()
	{
		return owner;
	}

	public void setOwner(AuthLogin owner)
	{
		this.owner = owner;
	}

	/**
	 * The timestamp of this request.
	 */
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	/**
	 * The UI Model saved as XML-serialized Java Bean.
	 * Defined only for Data Sources working with UI.
	 * (That have configuration window.)
	 */
	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public ReportFormat getFormat()
	{
		return format;
	}

	public void setFormat(ReportFormat format)
	{
		this.format = format;
	}

	/**
	 * Set when the user had downloaded the report result.
	 * In this case the system may remove the report
	 * not waiting the download timeout.
	 */
	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	/**
	 * The resulting report file.
	 */
	public byte[] getReport()
	{
		return report;
	}

	public void setReport(byte[] report)
	{
		this.report = report;
	}


	/* persisted attributes */

	private ReportTemplate template;
	private AuthLogin      owner;
	private Date           time;
	private String         model;
	private ReportFormat   format;
	private boolean        loaded;
	private byte[]         report;
}