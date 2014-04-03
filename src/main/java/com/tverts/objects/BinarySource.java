package com.tverts.objects;

/* Java */

import java.io.IOException;
import java.io.OutputStream;

import com.tverts.servlet.Download;


/**
 * Alternate variant of providing binary data
 * generated in the strategy. Similar to
 * {@link Download} interface, but doesn't
 * expose HTTP Servlet interfaces.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface BinarySource
{
	/* public: Binary Source */

	public static interface Biny
	{
		/* public: stream flags */

		/**
		 * When opening stream tells to deflate it,
		 * if this is supported by the consumer.
		 * Corresponding headers would be added.
		 */
		public static final int DEFLATE = 0x01;


		/* public: Binary Data Context */

		/**
		 * Opens the output stream. May be invoked only once!
		 * The flags defines the features of the stream.
		 *
		 * It is allowed to close the stream any number
		 * of times, or not to close it at all.
		 */
		public OutputStream stream(int flags)
		  throws IOException;

		/**
		 * Returns the request parameter.
		 * For HTTP request: the parameter.
		 */
		public String       get(String param);

		/**
		 * Sets the parameters of the response.
		 * Returns the previous value of the parameter.
		 *
		 * For HTTP request: sets the header.
		 * 'Content-Type' stands for MIME-type.
		 *
		 * Also, 'Content-Disposition' header
		 * defines the name of the file and
		 * orders to download and save it.
		 *
		 * Note that in HTTP implementation the
		 * names of the headers are case-sensitive!
		 */
		public String       set(String param, String value);
	}

	public void download(Biny biny)
	  throws IOException;
}