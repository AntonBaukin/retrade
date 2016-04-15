package com.tverts.jsx;

/* Java */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/* Java Scripting */

import javax.script.ScriptContext;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.streams.BytesStream;
import com.tverts.support.streams.Streams.EmptyReader;
import com.tverts.support.streams.Streams.NullWriter;
import com.tverts.support.streams.Streams.ReadWrapper;
import com.tverts.support.streams.Streams.WriteWrapper;


/**
 * Collection of three streams of a scripting
 * context: input, output, and error.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsStreams implements AutoCloseable
{
	/* Initialization */

	public JsStreams input(Reader r)
	{
		this.input = (r == null)?(new EmptyReader()):(r);
		return this;
	}

	public JsStreams input(InputStream s)
	{
		if(s == null)
			this.input = new EmptyReader();
		else try
		{
			this.input = new InputStreamReader(s, "UTF-8");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}

	public JsStreams input(byte[] bytes)
	{
		return this.input(new ByteArrayInputStream(bytes));
	}

	/**
	 * Default output to bytes buffer.
	 */
	public JsStreams output()
	{
		if(this.output != null)
			return this;

		BytesStream s = new BytesStream();
		this.output(s);
		this.outputBytes = s;
		return this;
	}

	public JsStreams output(Writer w)
	{
		closeByteStreams(true, false);
		this.output = (w == null)?(new NullWriter()):(w);
		return this;
	}

	public JsStreams output(OutputStream s)
	{
		closeByteStreams(true, false);

		if(s == null)
			this.output = new NullWriter();
		else try
		{
			this.output = new OutputStreamWriter(s, "UTF-8");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}

	/**
	 * Default errors stream to bytes buffer.
	 */
	public JsStreams error()
	{
		if(this.error != null)
			return this;

		BytesStream s = new BytesStream();
		this.error(s);
		this.errorBytes = s;
		return this;
	}

	public JsStreams error(Writer w)
	{
		closeByteStreams(false, true);
		this.error = (w == null)?(new NullWriter()):(w);
		return this;
	}

	public JsStreams error(OutputStream s)
	{
		closeByteStreams(false, true);

		if(s == null)
			this.error = new NullWriter();
		else try
		{
			this.error = new OutputStreamWriter(s, "UTF-8");
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}

		return this;
	}

	public JsStreams wrappers()
	{
		if(this.input == null)
			this.input = new ReadWrapper().
			  setClose(false);

		if(this.output == null)
			this.output = new WriteWrapper().
			  setClose(false);

		if(this.error == null)
			this.error = new WriteWrapper().
			  setClose(false);

		return this;
	}

	/**
	 * If this streams are wrappers,
	 * assigns source streams to them.
	 */
	public JsStreams wrap(JsStreams src)
	{
		if(this.input instanceof ReadWrapper)
			((ReadWrapper)this.input).setReader(src.getInput());

		if(this.output instanceof WriteWrapper)
			((WriteWrapper)this.output).setWriter(src.getOutput());

		if(this.error instanceof WriteWrapper)
			((WriteWrapper)this.error).setWriter(src.getError());

		return this;
	}


	/* Streams Access */

	public Reader getInput()
	{
		return (input != null)?(input):
		  (input = new EmptyReader());
	}

	protected Reader input;

	public Writer getOutput()
	{
		return (output != null)?(output):
		  (output = new NullWriter());
	}

	protected Writer output;

	public Writer getError()
	{
		return (error != null)?(error):
		  (error = new NullWriter());
	}

	protected Writer error;

	/**
	 * Returns Bytes Stream created by the default
	 * initialization procedure. Undefined when
	 * user installs own stream.
	 *
	 * The text is encoded in UTF-8.
	 */
	public BytesStream getOutputBytes()
	{
		return outputBytes;
	}

	protected BytesStream outputBytes;

	public BytesStream getErrorBytes()
	{
		return errorBytes;
	}

	protected BytesStream errorBytes;

	public JsStreams assign(ScriptContext ctx)
	{
		//=: reader
		ctx.setReader(this.getInput());

		//=: writer
		ctx.setWriter(this.getOutput());

		//=: error writer
		ctx.setErrorWriter(this.getError());

		return this;
	}


	/* Closeable */

	/**
	 * Closes all the streams assigned
	 * to this context.
	 */
	public void close()
	{
		new IO.Closer(input, output, error,
		  outputBytes, errorBytes).close();
	}

	public void flush()
	{
		try
		{
			if(output != null) try
			{
				output.flush();
			}
			finally
			{
				if(error != null)
					error.flush();
			}
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}


	/* protected: helpers */

	protected void closeByteStreams(boolean out, boolean err)
	{
		if(out && (outputBytes != null))
		{
			outputBytes.closeAlways();
			outputBytes = null;
		}

		if(err && (errorBytes != null))
		{
			errorBytes.closeAlways();
			errorBytes = null;
		}
	}
}