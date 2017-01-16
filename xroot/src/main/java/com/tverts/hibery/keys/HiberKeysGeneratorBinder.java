package com.tverts.hibery.keys;

/* Java */

import java.util.Properties;

/* Hibernate Persistence Layer */

import org.hibernate.boot.model.relational.ExportableProducer;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.type.LongType;

/* com.tverts: springer */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysGenerator;
import com.tverts.endure.keys.KeysGeneratorBinder;

/* com.tverts: hibery system */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: transactions */

import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Access strategy wrapping Hibernate
 * Identifier Generator implementation.
 *
 * @author anton.baukin@gmail.com
 */
public class      HiberKeysGeneratorBinder
       implements KeysGeneratorBinder
{
	/* Keys Generator Binder */

	public String        getGeneratorName()
	{
		return generatorName;
	}

	public KeysGenerator getGeneratorBound()
	{
		return generatorBound;
	}

	public void          bindGenerator()
	{
		EX.assertx(generatorBound == null);
		EX.assertn(generatorClass);

		this.generatorBound = createGenerator();
	}

	protected KeysGenerator generatorBound;


	/* Hibernate Keys Generator Binder */

	public void setGeneratorName(String name)
	{
		EX.assertn(name = SU.s2s(name));
		this.generatorName = name;
	}

	private String generatorName;

	public void setGeneratorClass(Class<? extends IdentifierGenerator> gclass)
	{
		this.generatorClass = gclass;
	}

	protected Class<? extends IdentifierGenerator> generatorClass;

	public void setProperties(Properties props)
	{
		this.properties = props;
	}

	protected Properties properties;


	/* protected: build generator */

	protected KeysGenerator
	                 createGenerator()
	{
		//~: create the generator instance
		IdentifierGenerator ig = createInstance();

		//~: initialize it
		init(ig);

		return new HiberKeysGeneratorBridge(ig);
	}

	protected IdentifierGenerator
	                 createInstance()
	{
		IdentifierGenerator ig;

		try
		{
			//~: a new instance
			ig = generatorClass.newInstance();

			//?: {configurable} do it
			if(ig instanceof Configurable)
				((Configurable)ig).configure(
				  LongType.INSTANCE, OU.clone(properties),
				  HiberSystem.serviceRegistry()
				);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return ig;
	}

	@SuppressWarnings("deprecation")
	protected void   init(IdentifierGenerator ig)
	{
		//~: initialize the query
		if(ig instanceof ExportableProducer)
			((ExportableProducer)ig).registerExportables(
			  HiberSystem.meta().getDatabase());

		//?: {not persistent} nothing more
		if(!(ig instanceof PersistentIdentifierGenerator))
			return;

		//TODO find better way to get sql to create db sequences

		//~: create database sequence
		final String[] qs = ((PersistentIdentifierGenerator)ig).
		  sqlCreateStrings(HiberSystem.dialect());

		//~: execute DDL updates
		bean(TxBean.class).setNew().execute(() ->
		{
			try
			{
				for(String q : qs)
					TxPoint.txSession().createNativeQuery(q).executeUpdate();

				LU.I(getLog(), "created db keys sequence ",
				  ((PersistentIdentifierGenerator)ig).generatorKey());
			}
			catch(Throwable e)
			{
				LU.I(getLog(), "found db keys sequence ",
				  ((PersistentIdentifierGenerator)ig).generatorKey());
			}
		});
	}

	protected String getLog()
	{
		return LU.getLogBased(HiberSystem.class, this);
	}
}