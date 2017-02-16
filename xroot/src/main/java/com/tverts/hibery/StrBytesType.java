package com.tverts.hibery;

/* Java */

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* Hibernate Persistence Layer */

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.streams.BytesStream;


/**
 * Hibernate type for UTF-8 encoded strings
 * that are stored in the database as
 * GZIP-compressed byte arrays.
 *
 * @author anton.baukin@gmail.com
 */
public class StrBytesType implements UserType
{
	public boolean isMutable()
	{
		return false;
	}

	public int[]   sqlTypes()
	{
		return SQL_TYPES;
	}

	public Class   returnedClass()
	{
		return String.class;
	}

	public boolean equals(Object x, Object y)
	{
		return (x == null && y == null) ||
		  (x != null) && x.equals(y);
	}

	public int     hashCode(Object x)
	{
		return (x == null)?(0):(x.hashCode());
	}

	public Object  nullSafeGet(ResultSet rs, String[] ns,
	    SharedSessionContractImplementor session, Object owner)
	  throws HibernateException, SQLException
	{
		//~: get the bytes array
		EX.assertx(ns.length == 1);
		byte[] bytes = rs.getBytes(ns[0]);

		//?: {has no data}
		if(bytes == null)
			return null;

		//~: decode compressed string
		try(BytesStream bs = new BytesStream())
		{
			bs.write(new GZIPInputStream(new ByteArrayInputStream(bytes)));
			return new String(bs.bytes(), "UTF-8");
		}
		catch(Exception x)
		{
			throw new HibernateException(x);
		}
	}

	public void nullSafeSet(PreparedStatement st, Object v, int i,
	    SharedSessionContractImplementor session)
	  throws HibernateException, SQLException
	{
		//?: {has string undefined}
		if(v == null)
		{
			st.setNull(i, Types.BINARY);
			return;
		}

		//~: compress the string bytes
		BytesStream bs = new BytesStream(); try
		{
			GZIPOutputStream gz = new GZIPOutputStream(bs);

			gz.write(((String)v).getBytes("UTF-8"));
			bs.setNotClose(true);
			gz.close();

			st.setBytes(i, bs.bytes());
		}
		catch(Exception x)
		{
			throw new HibernateException(x);
		}
		finally
		{
			bs.closeAlways();
		}
	}

	public Object  deepCopy(Object v)
	  throws HibernateException
	{
		return v;
	}

	public Object  replace(Object original, Object target, Object owner)
	  throws HibernateException
	{
		return original;
	}

	public Serializable disassemble(Object v)
	  throws HibernateException
	{
		return (String)v;
	}

	public Object  assemble(Serializable v, Object owner)
	  throws HibernateException
	{
		return v;
	}

	private static final int[] SQL_TYPES =
	  new int[] { Types.BINARY };
}
