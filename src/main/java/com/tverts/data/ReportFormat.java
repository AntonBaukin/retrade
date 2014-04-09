package com.tverts.data;

/**
 * Supported formats of the report documents.
 *
 * @author anton.baukin@gmail.com.
 */
public enum ReportFormat
{
	/**
	 * Report is XML representation of the data provided.
	 * This is the same document sent to the external
	 * reports printing server to get the documents.
	 */
	XML
	{
		public String contentType()
		{
			return "application/xml;charset=UTF-8";
		}
	},

	/**
	 * Reports is a Portable Document.
	 */
	PDF
	{
		public String contentType()
		{
			return "application/pdf";
		}
	},

	/**
	 * Reports is a spreadsheet.
	 * (Such as MS Excel file.)
	 */
	XLS
	{
		public String contentType()
		{
			return "application/vnd.ms-excel";
		}
	};


	/* public: format interface */

	public abstract String contentType();
}