package com.tverts.endure.person;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.clients.Person;

/* com.tverts: model */

import com.tverts.model.UnityModelBean;

/* com.tverts: support */

import com.tverts.support.IO;
import com.tverts.support.SU;


/**
 * Edit state of a {@link PersonEntity}.
 *
 * Note that entity referred by this model
 * may be a Person, or an Auth Login.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "person-model")
public class EditPersonModelBean extends UnityModelBean
{
	/* Edit Person Model (bean) */

	public Person getPerson()
	{
		return (person != null)?(person):(person = new Person());
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	@XmlElement(name = "genderMale")
	public Boolean getMale()
	{
		return (getPerson().getGender() == null)?(null):(getPerson().getGender().equals('M'));
	}

	public void setMale(Boolean male)
	{
		getPerson().setGender((male == null)?(null):Boolean.TRUE.equals(male)?('M'):('F'));
	}

	@XmlTransient
	public String getMaleStr()
	{
		return (getMale() == null)?(""):(getMale().toString());
	}

	public void setMaleStr(String male)
	{
		setMale(((male = SU.s2s(male)) == null)?(null):Boolean.valueOf(male));
	}


	/* private: encapsulated data */

	private Person person;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.xml(o, person);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		person = IO.xml(i, Person.class);
	}
}