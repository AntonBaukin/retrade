package com.tverts.servlet;

/* Java */

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/* com.tverts: objects */

import com.tverts.objects.BinarySource.Biny;


/**
 * Interface of strategy to upload
 * files to the server.
 *
 * To implement by UI Models or
 * Spring Components.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Upload
{
	/**
	 * The context of uploading the files of
	 * multi-part and body HTTP POST requests.
	 */
	public static interface UploadCtx
	{
		/* public: Upload Context */

		public Set<String> paramsNames();

		public Set<String> headersNames();

		public String      param(String name);

		public String[]    params(String name);

		public String      header(String name);

		public InputStream stream()
		  throws IOException;

		public String      contentType();

		/**
		 * The name of the content part (the file).
		 * Defined only for multi-part requests,
		 * but this is not guaranteed also...
		 */
		public String      fileName();

		public String      fieldName();

		/**
		 * The position of this file (or field) in the stream.
		 */
		public int         index();
	}


	/* public: Upload interface */

	public void upload(UploadCtx ctx)
	  throws IOException;

	public void commitUpload(Biny biny)
	  throws IOException;
}