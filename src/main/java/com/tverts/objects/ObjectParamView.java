package com.tverts.objects;

/* standard Java classes */

import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * View of an object parameter.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "object-param")
@XmlType(propOrder = {
  "name", "descr", "required", "readOnly", "value"
})
public class ObjectParamView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: ObjectParamView (bean) interface */

	@XmlElement(required = true)
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	@XmlAttribute
	public Boolean isRequired()
	{
		return (required)?(Boolean.TRUE):(null);
	}

	public void setRequired(Boolean required)
	{
		this.required = Boolean.TRUE.equals(required);
	}

	@XmlAttribute(name = "read-only")
	public Boolean isReadOnly()
	{
		return (readOnly)?(Boolean.TRUE):(null);
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = Boolean.TRUE.equals(readOnly);
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}


	/* public: ObjectParamView (init) interface */

	public ObjectParamView init(Object obj)
	{
		if((obj instanceof ObjectParam))
			this.init((ObjectParam) obj);
		return this;
	}

	public ObjectParamView init(ObjectParam p)
	{
		name     = p.getName();
		descr    = p.getDescr();
		required = p.isRequired();
		readOnly = !p.isWrite();

		return this;
	}


	/* parameter attributes */

	private String  name;
	private String  descr;
	private boolean required;
	private boolean readOnly;
	private String  value;
}