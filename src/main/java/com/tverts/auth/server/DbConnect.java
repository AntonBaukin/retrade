package com.tverts.auth.server;

/* standard Java classes */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;


/**
 * Data Access Strategy controlling
 * own connection and transaction
 * to the authentication database.
 *
 * The initial strategy object is a
 * prototype instance.
 *
 * The database server must be PostgreSQL!
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DbConnect implements Cloneable
{
	/* public: constructors */

	public DbConnect(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public DbConnect()
	{
		this(AuthConfig.INSTANCE.getDataSource());
	}


	/* public: DbConnect (connection) interface */

	public void connect()
	{
		try
		{
			connection = dataSource.getConnection();

			//!: disable auto commits
			connection.setAutoCommit(false);
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void disconnect(Boolean commit)
	{
		if(connection != null) try
		{
			if(Boolean.TRUE.equals(commit))
				connection.commit();

			if(Boolean.FALSE.equals(commit))
				connection.rollback();

			connection.close();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			connection = null;
		}
	}


	/* public: DbConnect (data access) interface */

	public String nextSidPrefix()
	{
		if(connection == null)
			throw new IllegalStateException();

		PreparedStatement ps;
		long              si;

		try
		{

// select true from pg_class where relname = 'auth_protocol_index'

			ps = connection.prepareStatement(

"select true from pg_class where relname = 'auth_protocol_index'"

			);

			ps.execute();

			boolean exists = ps.getResultSet().next();
			ps.close();

			//?: {there is no such sequence yet} create it
			if(!exists)
			{

// create sequence auth_protocol_index minvalue 1 no cycle

				ps = connection.prepareStatement(

				  "create sequence auth_protocol_index minvalue 1 no cycle"

				);

				ps.execute();
				ps.close();
			}

			//!: select the next value

// select nextval('auth_protocol_index')

			ps = connection.prepareStatement(

"select nextval('auth_protocol_index')"

			);

			ps.execute();
			ps.getResultSet().next();
			si = ps.getResultSet().getLong(1);
			ps.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}

		return Long.toString(si);
	}

	public String getPassword(Long domain, String login)
	{
		if(connection == null)
			throw new IllegalStateException();

		String result = null;


/*

select lo.passhash from auth_login lo where
  (lo.fk_domain = ?) and (lo.ux_login = ?) and
  (lo.close_time is null)

*/

		try
		{
			PreparedStatement ps = connection.prepareStatement(

"select lo.passhash from auth_login lo where\n" +
"  (lo.fk_domain = ?) and (lo.ux_login = ?) and\n" +
"  (lo.close_time is null)"

			);

			ps.setLong  (1, domain);
			ps.setString(2, login);

			ps.execute();
			if(ps.getResultSet().next())
				result = ps.getResultSet().getString(1);

			ps.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}

		return result;
	}

	public void   loadSession(AuthSession session)
	{
		if(connection == null)
			throw new IllegalStateException();

		if((session == null) || session.getSessionId() == null)
			throw new IllegalArgumentException();

/*

select se.close_time, lo.fk_domain, lo.ux_login,
  se.access_time, se.session_key, se.sequence_number
from auth_session se join auth_login lo
  on se.fk_login = lo.pk_entity
where (se.session_id = ?)

*/

		try
		{
			PreparedStatement ps = connection.prepareStatement(

"select se.close_time, lo.fk_domain, lo.ux_login,\n" +
"  se.access_time, se.session_key, se.sequence_number\n" +
"from auth_session se join auth_login lo\n" +
"  on se.fk_login = lo.pk_entity\n" +
"where (se.session_id = ?)"

			);

			ps.setString(1, session.getSessionId());
			ps.execute();

			ResultSet rs = ps.getResultSet();

			//?: {the session is not found}
			if(!rs.next())
			{
				session.setClosed(true);

				ps.close();
				return;
			}

			//[1]: is closed
			rs.getTimestamp(1);
			session.setClosed(!rs.wasNull());

			//[2]: domain
			session.setDomain(rs.getLong(2));

			//[3]: login
			session.setLogin(rs.getString(3));

			//[4]: server time
			Timestamp ts = rs.getTimestamp(4);
			if(!rs.wasNull())
				session.setServerTime(ts.getTime());

			//[5]: session key
			session.setSessionKey(rs.getString(5));

			//[6]: sequence number
			session.setSequence(rs.getLong(6));

			ps.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void   saveSession(AuthSession session)
	{
		if(connection == null)
			throw new IllegalStateException();

		if((session == null) || session.getSessionId() == null)
			throw new IllegalArgumentException();

		if(session.getDomain() == 0L)
			throw new IllegalArgumentException();

		if(session.getLogin() == null)
			throw new IllegalArgumentException();

		if(session.getSessionKey() == null)
			throw new IllegalArgumentException();


		//~: timestamp
		long timestamp = System.currentTimeMillis();

		//~: close time
		Timestamp closets  = (!session.isClosed())?(null)
		  :(new Timestamp(timestamp));

		//~: access timestamp
		Timestamp accessts = (session.getServerTime() < timestamp)
		  ?(new Timestamp(timestamp))
		  :(new Timestamp(session.getServerTime()));
		session.setServerTime(accessts.getTime());

		try
		{
			//~: search for the login

/*

select lo.pk_entity from auth_login lo where
  (lo.fk_domain = ?) and (lo.ux_login = ?) and
  (lo.close_time is null)

*/
			long              lo;
			PreparedStatement ps = connection.prepareStatement(

"select lo.pk_entity from auth_login lo where\n" +
"  (lo.fk_domain = ?) and (lo.ux_login = ?) and\n" +
"  (lo.close_time is null)"

			);

			ps.setLong  (1, session.getDomain());
			ps.setString(2, session.getLogin());
			ps.execute();

			//?: {no actual login found}
			if(!ps.getResultSet().next())
			{
				ps.close();

				throw new IllegalStateException(String.format(
				  "No actual record found for domain [%d] and login '%s'!",
				  session.getDomain(), session.getLogin()
				));
			}

			lo = ps.getResultSet().getLong(1);
			ps.close();


			//~: save the session
/*

insert into auth_session (

  session_id, fk_login, session_key,
  create_time, access_time, close_time,
  sequence_number

) values (?, ?, ?, ?, ?, ?, ?)

*/
			ps = connection.prepareStatement(

"insert into auth_session (\n" +
"\n" +
"  session_id, fk_login, session_key,\n" +
"  create_time, access_time, close_time,\n" +
"  sequence_number\n" +
"\n" +
") values (?, ?, ?, ?, ?, ?, ?)"

			);

			//[1]: session_id (primary key)
			ps.setString(1, session.getSessionId());

			//[2]: login
			ps.setLong(2, lo);

			//[3]: session key
			ps.setString(3, session.getSessionKey());

			//[4]: create time
			ps.setTimestamp(4, new Timestamp(timestamp));

			//[5]: access time
			ps.setTimestamp(5, accessts);

			//[6]: close time
			ps.setTimestamp(6, closets);

			//[7]: sequence number
			ps.setLong(7, session.getSequence());


			//!: execute insert
			if(ps.executeUpdate() != 1)
			{
				ps.close();
				throw new IllegalStateException();
			}

			ps.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void   touchSession(AuthSession session)
	{
		if(connection == null)
			throw new IllegalStateException();

		if((session == null) || session.getSessionId() == null)
			throw new IllegalArgumentException();

		//~: timestamp
		long timestamp = System.currentTimeMillis();

		//~: close timestamp
		Timestamp closets = !session.isClosed()?(null)
		  :(new Timestamp(timestamp));

		//~: access timestamp
		Timestamp accessts = (session.getServerTime() < timestamp)
		  ?(new Timestamp(timestamp))
		  :(new Timestamp(session.getServerTime()));
		session.setServerTime(accessts.getTime());



		try
		{

/*

update auth_login set
  access_time = ?, close_time = ?,
  sequence_number = ?
where (session_id = ?) and (close_time is null)

*/
			PreparedStatement ps = connection.prepareStatement(

"update auth_login set\n" +
"  access_time = ?, close_time = ?,\n" +
"  sequence_number = ?\n" +
"where session_id = ?"

			);

			//[1]: access time
			ps.setTimestamp(1, accessts);

			//[2]: close time
			ps.setTimestamp(2, closets);

			//[3]: sequence number
			ps.setLong     (3, session.getSequence());


			//!: execute
			if(ps.executeUpdate() != 1)
			{
				ps.close();
				throw new IllegalStateException();
			}

			ps.close();

		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
	}


	/* public: Cloneable interface */

	public DbConnect clone()
	{
		try
		{
			DbConnect dbc  = (DbConnect) super.clone();
			dbc.connection = null;

			return dbc;
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/* (prototype state): the data source */

	private DataSource dataSource;


	/* (instance state): the connection */

	private Connection connection;
}