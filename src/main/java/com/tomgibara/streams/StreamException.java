package com.tomgibara.streams;

public class StreamException extends RuntimeException {

	private static final long serialVersionUID = -1150629438483125099L;

	// nasty, but probably worthwhile for performance reasons
	public static final StreamException EOS = new StreamException("EOS");

	StreamException() { }

	StreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	StreamException(String message, Throwable cause) {
		super(message, cause);
	}

	StreamException(String message) {
		super(message);
	}

	StreamException(Throwable cause) {
		super(cause);
	}

}
