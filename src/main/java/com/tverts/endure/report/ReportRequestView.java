package com.tverts.endure.report;

/* Java */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Read-only view on the report request
 * issued by a user.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "report-request")
public class ReportRequestView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public boolean isReady()
	{
		return ready;
	}

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	public Long getTemplate()
	{
		return template;
	}

	public void setTemplate(Long template)
	{
		this.template = template;
	}

	public String getTemplateCode()
	{
		return templateCode;
	}

	public void setTemplateCode(String templateCode)
	{
		this.templateCode = templateCode;
	}

	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public String getTemplateDid()
	{
		return templateDid;
	}

	public void setTemplateDid(String templateDid)
	{
		this.templateDid = templateDid;
	}


	/* public: initialization interface */

	public ReportRequestView init(Object obj)
	{
		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		if(obj instanceof ReportTemplate)
			return init((ReportTemplate)obj);

		if(obj instanceof ReportRequest)
			return init((ReportRequest)obj);

		return this;
	}

	public ReportRequestView init(ReportTemplate t)
	{
		this.template = t.getPrimaryKey();
		this.templateCode = t.getCode();
		this.templateName = t.getName();
		this.templateDid = t.getDid();

		return this;
	}

	public ReportRequestView init(ReportRequest r)
	{
		this.objectKey = r.getPrimaryKey();
		this.time = r.getTime();
		this.format = r.getFormat().name();
		this.ready = (r.getReady() != null);
		this.loaded = (r.getLoaded() != null);

		return this;
	}


	/* view attributes */

	private Long    objectKey;
	private Date    time;
	private String  format;
	private boolean ready;
	private boolean loaded;
	private Long    template;
	private String  templateCode;
	private String  templateName;
	private String  templateDid;
}