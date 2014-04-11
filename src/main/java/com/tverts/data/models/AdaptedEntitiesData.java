package com.tverts.data.models;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure core */

import com.tverts.endure.United;
import com.tverts.endure.core.GetUnity;

/* com.tverts: models */

import com.tverts.hibery.HiberPoint;
import com.tverts.model.ModelData;

/* com.tverts: objects */

import com.tverts.objects.Adapter;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "count", "entities"})
public class AdaptedEntitiesData implements ModelData
{
	/* public: constructors */

	public AdaptedEntitiesData()
	{}

	public AdaptedEntitiesData(AdaptedEntitiesSelected model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public AdaptedEntitiesSelected getModel()
	{
		return model;
	}

	@XmlElement
	public int getCount()
	{
		return bean(GetUnity.class).count(model);
	}

	@XmlElement(name = "entity")
	@XmlElementWrapper(name = "entities")
	@SuppressWarnings("unchecked")
	public List getEntities()
	{
		Adapter      adt = EX.assertn(model.getAdapter());
		List<United> sel = bean(GetUnity.class).select(model);
		List         res = new ArrayList(sel.size());

		//~: adapt the entities selected to their views
		for(United u : sel)
			res.add(EX.assertn( adt.adapt(u),

			  "Adapter of class [", adt.getClass().getName(),
			  "] was unable to adapt United [", u.getPrimaryKey(),
			  "] of class [", HiberPoint.type(u).getName(), "]!"
			));

		return res;
	}


	/* private: the model */

	private AdaptedEntitiesSelected model;
}