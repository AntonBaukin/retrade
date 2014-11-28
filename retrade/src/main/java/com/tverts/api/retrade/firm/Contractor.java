package com.tverts.api.retrade.firm;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api (core + clients) */

import com.tverts.api.core.CatItem;
import com.tverts.api.clients.Firm;
import com.tverts.api.clients.Person;


/**
 * Contractor is a catalogue item that may
 * be a Firm, or a Person, or special instance.
 *
 * Firm or Person instance is embedded in
 * a Contractor instance not to store and
 * synchronize them separately.
 */
@XmlRootElement(name = "contractor")
@XmlType(name = "contractor", propOrder = {
  "firm", "person"
})
public class Contractor extends CatItem
{
	public Firm getFirm()
	{
		return firm;
	}

	private Firm firm;

	public void setFirm(Firm firm)
	{
		this.firm = firm;
	}

	public Person getPerson()
	{
		return person;
	}

	private Person person;

	public void setPerson(Person person)
	{
		this.person = person;
	}
}