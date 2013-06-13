package com.tverts.endure.secure;

/* standard Java classes */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: secure */

import com.tverts.secure.SecService;
import com.tverts.secure.force.SecForce;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.sXe;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * View on Secure Able and the related Secure Rule
 * defining that user (Auth Login) has some rights.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "secure-able")
public class SecAbleView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: SecAbleView (bean) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getAbleTime()
	{
		return ableTime;
	}

	public void setAbleTime(Date ableTime)
	{
		this.ableTime = ableTime;
	}

	public String getSetName()
	{
		return setName;
	}

	public void setSetName(String setName)
	{
		this.setName = setName;
	}

	public String getSetComment()
	{
		return setComment;
	}

	public void setSetComment(String setComment)
	{
		this.setComment = setComment;
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


	/* public: init interface */

	public SecAbleView init(Object obj)
	{
		if(obj instanceof SecAble)
			return this.init((SecAble) obj);

		if(obj instanceof SecRule)
			return this.init((SecRule) obj);

		if(obj instanceof SecSet)
			return this.init((SecSet) obj);


		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		return this;
	}

	public SecAbleView init(SecAble a)
	{
		objectKey = a.getPrimaryKey();
		ableTime  = a.getAbleTime();

		return this;
	}

	public SecAbleView init(SecRule r)
	{
		//~: access the force
		SecForce f = SecService.INSTANCE.force(r.getForce());
		if(f == null) throw EX.state(
		  "No Secure Force [", r.getForce(), "] exists!");

		force = r.getForce();
		title = r.getTitle();
		descr = f.getDescr(r);

		return this;
	}

	public SecAbleView init(SecSet s)
	{
		setName    = sXe(s.getName())?("По умолчанию"):(s.getName());
		setComment = s.getComment();

		return this;
	}


	/* secure able attributes */

	private Long   objectKey;
	private Date   ableTime;
	private String setName;
	private String setComment;


	/* secure rule attributes */

	private String force;
	private String title;
	private String descr;
}