package com.tverts.servlet;

/* Java */

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;


/**
 * Interface of strategy to upload
 * files to the server.
 *
 * To implement by UI Models or
 * Spring Components.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface Upload
{
	/* public: Upload */

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

		/**
		 * Tells that this content item is the last.
		 * For multi-part requests this is the final
		 * block of the request. For body requests
		 * this is the whole body of the POST.
		 */
		public boolean     isLast();

		public InputStream stream()
		  throws IOException;

		public String      contentType();

		/**
		 * The name of the content part (the file).
		 * Defined only for multi-part requests,
		 * but this is not guaranteed also...
		 */
		public String      fileName();
	}


	public void upload(UploadCtx ctx)
	  throws IOException;

	public void commit()
	  throws IOException;
}