package com.tverts.objects;

/* Java */

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
@XmlType(name = "object-param", propOrder = {
  "name", "descr", "required", "readOnly", "value"
})
public class ObjectParamView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* Object Param View */

	@XmlElement(required = true)
	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescr()
	{
		return descr;
	}

	private String descr;

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	@XmlAttribute
	public Boolean getRequired()
	{
		return (required)?(Boolean.TRUE):(null);
	}

	private boolean required;

	public void setRequired(Boolean required)
	{
		this.required = Boolean.TRUE.equals(required);
	}

	@XmlAttribute(name = "read-only")
	public Boolean getReadOnly()
	{
		return (readOnly)?(Boolean.TRUE):(null);
	}

	private boolean readOnly;

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = Boolean.TRUE.equals(readOnly);
	}

	public String getValue()
	{
		return value;
	}

	private String value;

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
		value    = p.getString();

		return this;
	}
}