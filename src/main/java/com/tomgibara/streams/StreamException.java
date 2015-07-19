package com.tomgibara.streams;

/**
 * Instances of this exception are thrown when errors are encountered reading
 * from (resp. writing to) {@link ReadStream} (resp. {@link WriteStream})
 * instances.
 * 
 * @author Tom Gibara
 * 
 * @see EndOfStreamException
 */

public class StreamException extends RuntimeException {

	private static final long serialVersionUID = -1150629438483125099L;

	public StreamException() { }

	public StreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public StreamException(String message) {
		super(message);
	}

	public StreamException(Throwable cause) {
		super(cause);
	}

}
