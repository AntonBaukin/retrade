package com.tverts.endure.secure;

/* standard Java classes */

import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: secure */

import com.tverts.secure.SecService;
import com.tverts.secure.force.SecForce;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * View on Secure Force.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "sec-rule")
public class SecRuleView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: SecForceView (bean) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getForce()
	{
		return force;
	}

	public void setForce(String force)
	{
		this.force = force;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}


	/* public: SecForceView (init) interface */

	public SecRuleView init(SecRule r)
	{
		//~: access the force
		SecForce f = SecService.INSTANCE.force(r.getForce());
		if(f == null) throw EX.state(
		  "No Secure Force [", r.getForce(), "] exists!");

		//~: assign the fields
		this.objectKey = r.getPrimaryKey();
		this.force = r.getForce();
		this.title = r.getTitle();
		this.descr = f.getDescr(r);

		return this;
	}


	/* force attributes */

	private Long   objectKey;
	private String force;
	private String title;
	private String descr;
}