package com.tverts.faces.formatters;

/* Java Server Faces */

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;


/**
 * Implementation base for JSF formatter and/or converter.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ConverterFormatterBase<T>
       implements     Converter
{
	/* public: Converter interface */

	public Object  getAsObject (
	                 FacesContext context,
	                 UIComponent  component,
	                 String       string
	               )
	{
		Request<T> request = new Request<T>(context, component);
		request.setString(string);

		if(sXe(string))
			convertEmptyString(request);
		else
			convert(request);

		return request.getValue();
	}

	@SuppressWarnings("unchecked")
	public String  getAsString (
	                 FacesContext context,
	                 UIComponent  component,
	                 Object       value
	               )
	{
		Request<T> request = new Request<T>(context, component);
		request.setValue((T)value);

		if(value == null)
			formatNullValue(request);
		else
			format(request);

		return request.getString();
	}


	/* protected static: format request */

	protected static class Request<ValueType>
	{
		/* public: constructor */

		public Request(FacesContext context, UIComponent component)
		{
			this.context   = context;
			this.component = component;
		}

		/* public: FormatRequest interface */

		public FacesContext getContext()
		{
			return context;
		}

		public UIComponent  getComponent()
		{
			return component;
		}

		public String       getString()
		{
			return string;
		}

		public void         setString(String string)
		{
			this.string = string;
		}

		public ValueType    getValue()
		{
			return value;
		}

		public void         setValue(ValueType value)
		{
			this.value = value;
		}


		/* private: request attributes */

		private FacesContext context;
		private UIComponent  component;
		private String       string;
		private ValueType    value;
	}


	/* protected: formatting */

	protected void    format(Request<T> request)
	{
		throw new UnsupportedOperationException(
		  "Converter is not a full JSF Formatter!");
	}

	protected void    formatNullValue(Request<T> request)
	{}

	protected Class   getValueClass()
	{
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	protected void    checkExpectedClass(Object value)
	{
		if(!getValueClass().isAssignableFrom(value.getClass()))
			throw new IllegalArgumentException(String.format(
			  "formatter expected %s class, but got %s",

			  getValueClass().getSimpleName(),
			  value.getClass().getSimpleName()
			));
	}


	/* protected: converting */

	protected void    convert(Request<T> request)
	{
		throw new UnsupportedOperationException(
		  "Formatter is not a full JSF Converter!");
	}

	protected void    convertEmptyString(Request<T> request)
	{}
}