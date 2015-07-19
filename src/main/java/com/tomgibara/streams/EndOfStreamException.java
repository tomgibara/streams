package com.tomgibara.streams;

public class EndOfStreamException extends StreamException {

	private static final long serialVersionUID = 5128955277305418544L;

	// nasty, but probably worthwhile for performance reasons
	public static final EndOfStreamException EOS = new EndOfStreamException("EOS");

	EndOfStreamException() { }

	EndOfStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	EndOfStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	EndOfStreamException(String message) {
		super(message);
	}

	EndOfStreamException(Throwable cause) {
		super(cause);
	}

}
