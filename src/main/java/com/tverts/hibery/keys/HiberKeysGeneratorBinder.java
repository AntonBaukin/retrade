package com.tverts.hibery.keys;

/* standard Java classes */

import java.util.Properties;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysGeneratorBinder;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * TODO comment HiberKeysGeneratorBinder
 *
 * @author anton.baukin@gmail.com
 */
public class      HiberKeysGeneratorBinder
       implements KeysGeneratorBinder
{
	/* public: KeysGeneratorBinder interface */

	public String           getGeneratorName()
	{
		return generatorName;
	}

	public KeysGenerator    getGeneratorBound()
	{
		return generatorBound;
	}

	public void             bindGenerator()
	{
		if(getGeneratorBound() != null)
			throw new IllegalStateException();

		if(getGeneratorClass() == null)
			throw new IllegalStateException();

		if(getSessionFactory() != null)
			throw new IllegalStateException();

		this.generatorBound = createGenerator();
	}

	/* public: HiberKeysGeneratorBinder interface */

	public void             setGeneratorName(String name)
	{
		if((name = s2s(name)) == null)
			throw new IllegalArgumentException();
		this.generatorName = name;
	}

	public SessionFactory   getSessionFactory()
	{
		return sessionFactory;
	}

	public void             setSessionFactory(SessionFactory sf)
	{
		if(!(sf instanceof SessionFactoryImplementor))
			throw new IllegalArgumentException();
		this.sessionFactory = sf;
	}

	public Class<? extends IdentifierGenerator>
	                        getGeneratorClass()
	{
		return generatorClass;
	}

	public void             setGeneratorClass
	  (Class<? extends IdentifierGenerator> gclass)
	{
		if(gclass == null) throw new IllegalArgumentException();
		this.generatorClass = gclass;
	}

	public Properties       getProperties()
	{
		return (properties != null)?(properties):
		  (properties = new Properties());
	}

	public void             setProperties(Properties props)
	{
		this.properties = props;
	}

	/* protected: build generator */

	protected KeysGenerator createGenerator()
	{
		IdentifierGenerator hgen;

		try
		{
			hgen = getGeneratorClass().newInstance();

			if(hgen instanceof Configurable)
				((Configurable)hgen).configure(
				  getKeyType(), getProperties(),
				  ((SessionFactoryImplementor)getSessionFactory()).getDialect()
				);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return new HiberKeysGeneratorBridge(hgen);
	}

	protected Type          getKeyType()
	{
		return LongType.INSTANCE;
	}

	/* private: the binder state */

	private String          generatorName;
	private KeysGenerator   generatorBound;

	/* private: the binder parameters */

	private Properties      properties;
	private Class<? extends IdentifierGenerator>
	                        generatorClass;

	/* private: hibernate context */

	private SessionFactory  sessionFactory;
}