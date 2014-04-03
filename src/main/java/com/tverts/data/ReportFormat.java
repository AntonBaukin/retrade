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
	XML,

	/**
	 * Reports is a Portable Document.
	 */
	PDF,

	/**
	 * Reports is a spreadsheet.
	 * (Such as MS Excel file.)
	 */
	XLS
}