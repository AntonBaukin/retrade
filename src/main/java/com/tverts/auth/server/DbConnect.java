package com.tverts.auth.server;

/* standard Java classes */

import java.io.IOException;
import java.io.InputStream;
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

	public void    connect()
	{
		try
		{
			connection = dataSource.getConnection();

			//!: disable auto commits
			if(connection.getAutoCommit())
				connection.setAutoCommit(false);
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void    disconnect(Boolean commit)
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


	/* public: DbConnect (authentication) interface */

	public void     initDatabase()
	{
		try
		{
			createSequence("auth_protocol_index");
			createSequence("pkeys_exec_request");
		}
		catch(SQLException e)
		{
			throw new RuntimeException(
			  "Error occurred while initializing Database for Auth Protocol!", e);
		}
	}

	public String   nextSidPrefix()
	{
		if(connection == null)
			throw new IllegalStateException();

		PreparedStatement ps;
		long              si;

		try
		{

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

	/**
	 * Returns array where [0] is Long primary key of the
	 * Domain, and [1] String is the password hash.
	 */
	public Object[] getDomainPassword(String domainCode, String login)
	  throws SQLException
	{
		if(connection == null)
			throw new IllegalStateException();

		Object[] result = null;


/*

select lo.fk_domain, lo.passhash from
  auth_login lo
    join
  core_domain d
    on (lo.fk_domain = d.pk_entity)
where
  (d.domain_code = ?) and (lo.ux_login = ?) and
  (lo.close_time is null)

*/

		PreparedStatement ps = connection.prepareStatement(

"select lo.fk_domain, lo.passhash from\n" +
"  auth_login lo\n" +
"    join\n" +
"  core_domain d\n" +
"    on (lo.fk_domain = d.pk_entity)\n" +
"where\n" +
"  (d.domain_code = ?) and (lo.ux_login = ?) and\n" +
"  (lo.close_time is null)"

		);

		ps.setString(1, domainCode);
		ps.setString(2, login);

		ps.execute();
		if(ps.getResultSet().next())
		{
			result    = new Object[2];
			result[0] = ps.getResultSet().getLong(1);
			result[1] = ps.getResultSet().getString(2);
		}
		ps.close();

		return result;
	}

	public boolean  existsRs(String domainCode, String login, String Rs)
	  throws SQLException
	{
/*

select true from
  auth_session se
    join
  auth_login lo
    on (se.fk_login = lo.pk_entity)
    join
  core_domain d
    on (lo.fk_domain = d.pk_entity)
where
  (d.domain_code = ?) and (lo.ux_login = ?) and
  (se.server_random = ?)

*/
		PreparedStatement ps = connection.prepareStatement(

"select true from\n" +
"  auth_session se\n" +
"    join\n" +
"  auth_login lo\n" +
"    on (se.fk_login = lo.pk_entity)\n" +
"    join\n" +
"  core_domain d\n" +
"    on (lo.fk_domain = d.pk_entity)\n" +
"where\n" +
"  (d.domain_code = ?) and (lo.ux_login = ?) and\n" +
"  (se.server_random = ?)"

		);

		//[1]: domain
		ps.setString(1, domainCode);

		//[2]: login
		ps.setString(2, login);

		//[3]: Rs
		ps.setString(3, Rs);

		//!: execute the query
		ps.execute();
		boolean result = ps.getResultSet().next();
		ps.close();

		return result;
	}

	public void     loadSession(AuthSession session)
	  throws SQLException
	{
		if(connection == null)
			throw new IllegalStateException();

		if((session == null) || session.getSessionId() == null)
			throw new IllegalArgumentException();

/*

select se.close_time, lo.fk_domain,
  lo.ux_login, se.access_time, se.session_key,
  se.sequence_number, se.server_random, se.bind_key
from auth_session se join auth_login lo
  on se.fk_login = lo.pk_entity
where (se.session_id = ?)

*/

		PreparedStatement ps = connection.prepareStatement(

"select se.close_time, lo.fk_domain,\n" +
"  lo.ux_login, se.access_time, se.session_key,\n" +
"  se.sequence_number, se.server_random, se.bind_key\n" +
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

		//[7]: Rs
		session.setRs(rs.getString(7));

		//[8]: bind key
		session.setBind(rs.getString(8));

		ps.close();
	}

	public void     saveSession(AuthSession session)
	  throws SQLException
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
  sequence_number, server_random

) values (?, ?, ?, ?, ?, ?, ?, ?)

*/
		ps = connection.prepareStatement(

"insert into auth_session (\n" +
"\n" +
"  session_id, fk_login, session_key,\n" +
"  create_time, access_time, close_time,\n" +
"  sequence_number, server_random\n" +
"\n" +
") values (?, ?, ?, ?, ?, ?, ?, ?)"

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

		//[8]: Rs
		ps.setString(8, session.getRs());


		//!: execute insert
		if(ps.executeUpdate() != 1)
		{
			ps.close();
			throw new IllegalStateException();
		}

		ps.close();
	}

	public void     touchSession(AuthSession session)
	  throws SQLException
	{
		if(connection == null)
			throw new IllegalStateException();

		if((session == null) || session.getSessionId() == null)
			throw new IllegalArgumentException();

		//~: timestamp
		long timestamp     = System.currentTimeMillis();

		//~: close timestamp
		Timestamp closets  = !session.isClosed()?(null)
		  :(new Timestamp(timestamp));

		//~: access timestamp
		Timestamp accessts = new Timestamp(session.getServerTime());


/*

update auth_session set
  access_time = ?, close_time = ?,
  sequence_number = ?, bind_key = ?
where (session_id = ?) and (close_time is null)

*/
		PreparedStatement ps = connection.prepareStatement(

"update auth_session set\n" +
"  access_time = ?, close_time = ?,\n" +
"  sequence_number = ?, bind_key = ?\n" +
"where (session_id = ?) and (close_time is null)"

		);

		//[1]: access time
		ps.setTimestamp(1, accessts);

		//[2]: close time
		ps.setTimestamp(2, closets);

		//[3]: sequence number
		ps.setLong     (3, session.getSequence());

		//[4]: bind key
		ps.setString   (4, session.getBind());

		//[5]: Session ID
		ps.setString   (5, session.getSessionId());


		//!: execute
		if(ps.executeUpdate() != 1)
		{
			ps.close();
			throw new IllegalStateException();
		}

		ps.close();
	}


	/* public: DbConnect (requests) interface */

	public static class AuthRequest
	{
		/* public: Request bean interface */

		public AuthSession  getSession()
		{
			return session;
		}

		public AuthRequest  setSession(AuthSession session)
		{
			this.session = session;
			return this;
		}

		public String       getClientKey()
		{
			return clientKey;
		}

		public AuthRequest  setClientKey(String clientKey)
		{
			this.clientKey = clientKey;
			return this;
		}

		public BytesStream  getInput()
		{
			return input;
		}

		public AuthRequest  setInput(BytesStream input)
		{
			this.input = input;
			return this;
		}

		public BytesStream  getOutput()
		{
			return output;
		}

		public AuthRequest  setOutput(BytesStream output)
		{
			this.output = output;
			return this;
		}

		public Runnable     getCommit()
		{
			return commit;
		}

		public AuthRequest  setCommit(Runnable commit)
		{
			this.commit = commit;
			return this;
		}

		/**
		 * This flag is set on receive request when the
		 * client key is set and the request is still
		 * pending: the request record exists, but no
		 * response object is written.
		 */
		public boolean     isHasRecord()
		{
			return hasRecord;
		}

		public AuthRequest setHasRecord(boolean hasRecord)
		{
			this.hasRecord = hasRecord;
			return this;
		}

		/* private: request state */

		private AuthSession session;
		private String      clientKey;
		private BytesStream input;
		private BytesStream output;
		private Runnable    commit;
		private boolean     hasRecord;
	}


	public void    receive(AuthRequest ar)
	  throws SQLException, IOException
	{
/*

select er.pk_exec_request, er.response_object
from exec_request er where
  (er.fk_domain = ?) and (er.session_id = ?) and
  (er.is_executed = true) and (er.was_delivered = false)
order by er.pk_exec_request limit 1

select er.pk_exec_request, er.response_object
from exec_request er where
  (er.fk_domain = ?) and (er.session_id = ?) and
  (er.client_key = ?)

*/
		final String A =

"select er.pk_exec_request, er.response_object\n" +
"from exec_request er where\n" +
"  (er.fk_domain = ?) and (er.session_id = ?) and\n" +
"  (er.is_executed = true) and (er.was_delivered = false)\n" +
"order by er.pk_exec_request limit 1";

		final String B =

"select er.pk_exec_request, er.response_object\n" +
"from exec_request er where\n" +
"  (er.fk_domain = ?) and (er.session_id = ?) and\n" +
"  (er.client_key = ?)";

		PreparedStatement ps = connection.prepareStatement(
		  (ar.getClientKey() == null)?(A):(B)
		);

		//[1]: domain key
		ps.setLong(1, ar.getSession().getDomain());

		//[2]: Session ID
		ps.setString(2, ar.getSession().getSessionId());

		//[3]: client key
		if(ar.getClientKey() != null)
			ps.setString(3, ar.getClientKey());

		//!: execute query
		ResultSet rs = ps.executeQuery();

		//?: {no record found}
		if(!rs.next())
		{
			rs.close(); ps.close();
			return;
		}
		else
			ar.setHasRecord(true);

		//[1]: exec request key
		long rkey = rs.getLong(1);

		//[2]: read the response object (XML bytes)
		InputStream is;  if((is = rs.getBinaryStream(2)) != null)
		{
			ar.getOutput().write(is);
			is.close();
		}

		rs.close(); ps.close();

		//!: set delivery commit post-operation
		ar.setCommit(new ReceiveCommit(this, rkey));
	}

	public void    request(AuthRequest ar)
	  throws SQLException
	{
		//~: get next primary key value

		PreparedStatement ps;
		long              pk;


// select nextval('pkeys_exec_request')

		ps = connection.prepareStatement(

  "select nextval('pkeys_exec_request')"

		);

		ps.execute();
		ps.getResultSet().next();
		pk = ps.getResultSet().getLong(1);
		ps.close();

		//?: {has no client key} create own
		if(ar.getClientKey() == null)
			ar.setClientKey("@" + pk);


		//~: insert the record

/*

insert into exec_request  (

  pk_exec_request, fk_domain, session_id,
  client_key, request_time, request_object

) values (?, ?, ?, ?, ?, ?)

*/
		ps = connection.prepareStatement(

  "insert into exec_request  (\n" +
  "\n" +
  "  pk_exec_request, fk_domain, session_id,\n" +
  "  client_key, request_time, request_object\n" +
  "\n" +
  ") values (?, ?, ?, ?, ?, ?)"

		);

		//[1]: primary key
		ps.setLong(1, pk);

		//[2]: domain
		ps.setLong(2, ar.getSession().getDomain());

		//[3]: Session ID
		ps.setString(3, ar.getSession().getSessionId());

		//[4]: client key
		ps.setString(4, ar.getClientKey());

		//[5]: request time
		ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

		//[6]: request object bytes
		try
		{
			ps.setBinaryStream(6, ar.getInput().inputStream(),
			  (int) ar.getInput().length());
		}
		catch(IOException e)
		{
			throw new SQLException(e);
		}


		//!: execute
		if(ps.executeUpdate() != 1)
		{
			ps.close();
			throw new IllegalStateException();
		}

		ps.close();
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


	/* protected: database support */

	protected void createSequence(String name)
	  throws SQLException
	{
		PreparedStatement ps;


// select true from pg_class where (relname = ?)

		ps = connection.prepareStatement(

  "select true from pg_class where (relname = ?)"

		);

		ps.setString(1, name);
		ps.execute();

		boolean exists = ps.getResultSet().next();
		ps.close();

		//?: {there is no such sequence yet} create it
		if(!exists)
		{

// create sequence NAME minvalue 1 no cycle

			ps = connection.prepareStatement(

  "create sequence " + name + " minvalue 1 no cycle"

			);

			ps.execute();
			ps.close();
		}
	}

	protected void commitReceive(long request)
	  throws SQLException
	{

/*

update exec_request set was_delivered = true where
  (pk_exec_request = ?) and (was_delivered = false)

*/
		PreparedStatement ps = connection.prepareStatement(

"update exec_request set was_delivered = true where \n" +
"  (pk_exec_request = ?) and (was_delivered = false)"

		);

		//[1]: exec request key
		ps.setLong(1, request);

		//!: execute
		if(ps.executeUpdate() != 1)
		{
			ps.close();
			throw new IllegalStateException();
		}

		ps.close();
	}

	protected static class ReceiveCommit implements Runnable
	{
		/* public: constructor */

		public ReceiveCommit(DbConnect dbc, Long request)
		{
			this.dbc     = dbc;
			this.request = request;
		}


		/* public: Runnable interface */

		public void run()
		{
			boolean commit = false;

			try
			{
				//~: connect to the database
				dbc.connect();

				//!: execute receive commit
				dbc.commitReceive(request);
				commit = true;
			}
			catch(SQLException e)
			{
				throw new RuntimeException(String.format(
				  "Unable to commit Execution Request Receive [%d]!",
				  request
				), e);
			}
			finally
			{
				dbc.disconnect(commit);
			}
		}

		/* private: commit parameters */

		private DbConnect dbc;
		private Long      request;

	}


	/* (prototype state): the data source */

	private DataSource dataSource;


	/* (instance state): the connection */

	private Connection connection;
}