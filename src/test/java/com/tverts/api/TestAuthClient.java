package com.tverts.api;

/**
 * Simple console application to invoke
 * {@link AuthClient} implementation.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TestAuthClient
{
	public static void main(String[] argv)
	  throws Exception
	{
		if(argv.length != 4)
		{
			System.out.println(
			  "Usage: TestAuthClient url domain login password"
			);

			return;
		}

		String url    = argv[0];
		String domain = argv[1];
		String login  = argv[2];
		String pass   = argv[3];

		//~: create the client
		AuthClient client = new AuthClient();

		client.initURL(url);
		client.initDomain(domain);
		client.initLogin(login);
		client.initPassword(pass);
		client.initTimeout(10000);

		//~: do login
		client.login();

		//~: touch the session
		client.touch();

		//~: close the session
		client.close();

		//~: check session is closed
		boolean was_error = false;

		try
		{
			client.touch();
		}
		catch(AuthClient.AuthError e)
		{
			was_error = true;
		}

		if(!was_error) throw new IllegalArgumentException(
		  "Session was not closed!");
	}
}