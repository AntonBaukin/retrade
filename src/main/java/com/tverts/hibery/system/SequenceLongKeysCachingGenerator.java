package com.tverts.hibery.system;

/* standard Java classes */

import java.io.Serializable;
import java.util.Properties;

/* Hibernate Persistence Layer */

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

/**
 * This generator uses database sequence to efficiently
 * create unique {@code Long} keys.
 *
 * The sequence is assumed to be configured so that each
 * new key is incremented not by default 1, but for greater
 * amount that is the cache size. In the range of these
 * database increment the values are incremented by 1
 * without querying the sequence.
 *
 * @author anton.baukin@gmail.com
 */
public final class SequenceLongKeysCachingGenerator
       extends     SequenceGenerator
{
	/* parameters of the generator */

	/**
	 * Defines the incrementation step configured on the
	 * database level. Within the range of this step the
	 * keys are incremented locally.
	 */
	public static final String PARAM_INCR = "sequence_increment";

	/* public: SequenceGenerator interface */

	public void configure(Type type, Properties params, Dialect dialect)
	  throws MappingException
	{
		if(!LongType.class.isAssignableFrom(type.getClass()))
			throw new MappingException(
			  "SequenceLongKeysCachingGenerator supports only Long keys!");

		super.configure(type, params, dialect);

		//~: read sequence increment option
		seqinc = PropertiesHelper.getInt(PARAM_INCR, params, 0);

		if(seqinc < 1) throw new MappingException(
		  "SequenceLongKeysCachingGenerator's parameter " +
		  "'sequence_increment' has illegal (or not defined) value!");
	}

	public synchronized Serializable
	            generate(SessionImplementor session, Object obj)
	{
		//?: {has no sequence value | got the limit} query the sequence
		if((svalue == null) || (xvalue + 1 == svalue + seqinc))
		{
			svalue = generateHolder(session).makeValue().longValue();
			return xvalue = svalue;
		}

		return ++xvalue; //<-- increment, then return
	}

	/* private: the state of the generator */

	private Long svalue;
	private long xvalue;

	/* private: parameters of the generator */

	private int  seqinc;
}