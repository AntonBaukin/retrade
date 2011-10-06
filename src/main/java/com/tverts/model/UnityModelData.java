package com.tverts.model;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: endure core */

import com.tverts.endure.United;


/**
 * Data model of {@link UnityModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "entity"})
public class UnityModelData implements ModelData
{
	/* public: constructors */

	public UnityModelData()
	{}

	public UnityModelData(UnityModelBean model)
	{
		if(model == null) throw new IllegalArgumentException();
		this.model = model;
	}


	/* public: UnityModelData (bean) interface */

	@XmlElement
	public UnityModelBean  getModel()
	{
		return model;
	}

	@XmlElement
	public Object          getEntity()
	{
		return (entity != null)?(entity):
		  (entity = model.loadEntity());
	}


	/* private: the model & entity */

	private UnityModelBean model;
	private United         entity;
}