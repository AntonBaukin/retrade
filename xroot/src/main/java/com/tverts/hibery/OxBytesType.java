package com.tverts.hibery;

/* Java */

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/* Hibernate Persistence Layer */

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Hibernate type for better storage
 * of byte arrays.
 *
 * @author anton.baukin@gmail.com
 */
public class OxBytesType implements UserType
{
	private static final int[] SQL_TYPES =
	  new int[] { Types.BINARY };

	public boolean isMutable()
	{
		return true;
	}

	public int[]   sqlTypes()
	{
		return SQL_TYPES;
	}

	public Class   returnedClass()
	{
		return OxBytes.class;
	}

	public boolean equals(Object x, Object y)
	  throws HibernateException
	{
		return (x != null) && ((OxBytes)check(x)).equals((OxBytes) check(y));
	}

	public int     hashCode(Object x)
	  throws HibernateException
	{
		return (x == null)?(0):(check(x).hashCode());
	}

	public Object  nullSafeGet(ResultSet rs, String[] ns,
	    SharedSessionContractImplementor session, Object owner)
	  throws HibernateException, SQLException
	{
		//~: get the bytes array
		EX.assertx(ns.length == 1);
		byte[] bytes = rs.getBytes(ns[0]);

		//?: {has data}
		return (bytes == null)?(null):(new OxBytes(bytes));
	}

	public void    nullSafeSet(PreparedStatement st, Object v, int i,
	    SharedSessionContractImplementor session)
	  throws HibernateException, SQLException
	{
		byte[] bytes = (v == null)?(null):((OxBytes) check(v)).oxBytes();

		if(bytes == null)
			st.setNull(i, Types.BINARY);
		else
			st.setBytes(i, bytes);
	}

	public Object  deepCopy(Object v)
	  throws HibernateException
	{
		return (v == null)?(null):(new OxBytes((OxBytes) check(v)));
	}

	public Object  replace(Object original, Object target, Object owner)
	  throws HibernateException
	{
		return this.deepCopy(original);
	}

	public Serializable disassemble(Object v)
	  throws HibernateException
	{
		return (v == null)?(null):(((OxBytes) check(v)).oxBytes());
	}

	public Object       assemble(Serializable v, Object owner)
	  throws HibernateException
	{
		if(v == null) return null;

		if(!(v instanceof byte[]))
			throw EX.ass("OxBytesType got not a byte[] cached state!");

		return new OxBytes((byte[]) v);
	}

	protected Object    check(Object v)
	{
		if((v != null) && !OxBytes.class.equals(v.getClass()))
			throw EX.ass("Not an OxBytes property value!");
		return v;
	}
}