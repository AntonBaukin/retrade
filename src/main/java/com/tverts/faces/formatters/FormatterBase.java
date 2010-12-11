package com.tverts.faces.formatters;

/* Java Server Faces */

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class FormatterBase<T>
       implements     Converter
{
	/* public: Converter interface */

	public Object  getAsObject (
	                 FacesContext context,
	                 UIComponent  component,
	                 String       string
	               )
	{
		throw new UnsupportedOperationException(
		  "Formatter is not a full JSF Converter!");
	}

	@SuppressWarnings("unchecked")
	public String  getAsString (
	                 FacesContext context,
	                 UIComponent  uicomponent,
	                 Object       value
	               )
	{
		if(value == null)
		{
			handleNullValue();
			return "";
		}

		checkExpectedClass(value);
		return format((T)value);
	}

	public Class   getExpectedClass()
	{
		return Object.class;
	}

	public abstract String
	               format(T value);

	/* protected: formatting */

	@SuppressWarnings("unchecked")
	protected void checkExpectedClass(Object value)
	{
		if(!getExpectedClass().isAssignableFrom(value.getClass()))
			throw new IllegalArgumentException(String.format(
			  "formatter expected %s class, but got %s",

			  getExpectedClass().getSimpleName(),
			  value.getClass().getSimpleName()
			));
	}

	protected void handleNullValue()
	{}
}