package com.tverts.endure.auth;

/* standard Java classes */

import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: endure (catalogues + persons) */

import com.tverts.endure.cats.CatItemView;
import com.tverts.endure.person.Person;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Read view on {@link AuthLogin} entity.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class AuthLoginView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* public: AuthLoginView (bean) interface */

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public String getTypeNameLo()
	{
		return typeNameLo;
	}

	public void setTypeNameLo(String typeNameLo)
	{
		this.typeNameLo = typeNameLo;
	}

	public String getDescr()
	{
		return descr;
	}

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	@XmlElement(name = "genderMale")
	public Boolean getMale()
	{
		return male;
	}

	public void setMale(Boolean male)
	{
		this.male = male;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhoneMob()
	{
		return phoneMob;
	}

	public void setPhoneMob(String phoneMob)
	{
		this.phoneMob = phoneMob;
	}

	public String getPhoneWork()
	{
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork)
	{
		this.phoneWork = phoneWork;
	}


	/* public: initialization interface */

	public AuthLoginView init(Object obj)
	{
		if(obj instanceof AuthLogin)
			init((AuthLogin) obj);

		return (AuthLoginView) super.init(obj);
	}

	public AuthLoginView init(AuthLogin l)
	{
		if(l.getPerson() != null)
		{
			init(l.getPerson());

			typeName   = "Person";
			typeNameLo = "Персона";
		}

		if(l.getComputer() != null)
		{
			init(l.getComputer());

			typeName   = "Computer";
			typeNameLo = "Компьютер";
		}

		//~: create time
		createTime = l.getCreateTime();

		//~: close time
		closeTime = l.getCloseTime();

		//~: description
		descr = l.getDescr();

		return this;
	}

	public AuthLoginView init(Person p)
	{
		this.lastName   = p.getLastName();
		this.firstName  = p.getFirstName();
		this.middleName = p.getMiddleName();
		this.male       = (p.getGender() == null)?(null):(p.getGender().equals('M'));
		this.email      = p.getEmail();
		this.phoneMob   = p.getPhoneMob();
		this.phoneWork  = p.getPhoneWork();

		return this;
	}

	public AuthLoginView init(Computer c)
	{
		return this;
	}


	/* login attributes */

	private Date    createTime;
	private Date    closeTime;

	private String  typeName;
	private String  typeNameLo;
	private String  descr;

	private String  lastName;
	private String  firstName;
	private String  middleName;
	private Boolean male;
	private String  email;
	private String  phoneMob;
	private String  phoneWork;
}