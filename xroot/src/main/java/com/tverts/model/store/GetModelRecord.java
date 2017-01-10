package com.tverts.model.store;

/* Java */

import java.util.Date;
import java.util.Map;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.SQLQuery;
import org.hibernate.type.BinaryType;
import org.hibernate.type.TimestampType;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Read and write Model Store records.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetModelRecord extends GetObjectBase
{
	/* Get, Write Model Record */

	public byte[] load(ModelEntry e)
	{
		final String Q =
"select domain, login, accessTime, bean from ModelRecord where (id = :key)";

		//~: select
		Object[] r = first(Object[].class, Q, "key", key(e.key));
		if(r == null) return null;

		//[0]: domain
		e.domain = (Long)r[0];

		//[1]: login
		e.login = (Long)r[1];

		//[2]: access time
		e.accessTime = ((Date)r[2]).getTime();

		//[3]: bean bytes
		return (byte[])r[3];
	}

	public void   save(Map<ModelEntry, byte[]> es)
	{
		SQLQuery u = null, i = null;

		final String U =
"update model_record set bean_bytes = ?, access_type = ? where (pk_bean_key = ?)";

		final String I =
"insert into model_record (pk_bean_key, access_type, " +
"  domain, login, bean_bytes) values (?, ?, ?, ?, ?)";

		//c: for each entry
		for(Map.Entry<ModelEntry, byte[]> e : es.entrySet())
		{
			//?: {has this entry loaded} try update it
			if(e.getKey().loaded)
			{
				if(u == null)
					u = session().createSQLQuery(U);

				//[0]: bytes
				u.setParameter(0, e.getValue(), BinaryType.INSTANCE);

				//[1]: access time
				u.setParameter(1, new Date(e.getKey().accessTime), TimestampType.INSTANCE);

				//[2]: primary key
				u.setParameter(2, key(e.getKey().key));

				//?: {updated it} take the next
				if(u.executeUpdate() == 1)
					continue;
			}

			//~: proceed with insert
			if(i == null)
				i = session().createSQLQuery(I);

			//[0]: primary key
			i.setParameter(0, key(e.getKey().key));

			//[1]: access time
			i.setParameter(1, new Date(e.getKey().accessTime), TimestampType.INSTANCE);

			//[2]: domain
			i.setParameter(2, e.getKey().domain);

			//[3]: login
			i.setParameter(3, e.getKey().login);

			//[4]: bytes
			i.setParameter(4, e.getValue(), BinaryType.INSTANCE);

			//!: do insert it
			i.executeUpdate();
		}
	}

	public void   remove(Set<String> keys)
	{
		final String D =
"delete from model_record (pk_bean_key = ?)";

		SQLQuery d = session().createSQLQuery(D);

		//c: for each key
		for(String k : keys)
			d.setParameter(0, key(k)).executeUpdate();
	}

	/**
	 * Removes the records older than
	 * the timeout value given.
	 */
	public void   sweep(long timeout)
	{
		final String Q =
"delete from ModelRecord where (accessTime < :at)";

		Q(Q, "at", new Date(System.currentTimeMillis() - timeout)).
		  executeUpdate();
	}

	/**
	 * Converts key of format 'SimpleClassName-HEX'
	 * to regular Long value.
	 */
	public Long   key(String key)
	{
		int i = key.indexOf('-');
		EX.assertx((i > 0) & (i < key.length()));

		return Long.parseLong(key.substring(i+1), 16);
	}
}