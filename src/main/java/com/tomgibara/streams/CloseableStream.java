package com.tomgibara.streams;

/**
 * Common auto-closable interface for {@link ReadStream} and {@link WriteStream}
 * .
 * 
 * @author Tom Gibara
 *
 */

public interface CloseableStream extends AutoCloseable {

	/**
	 * Closes a {@link ReadStream} or {@link WriteStream}. The effect of calling
	 * this method will vary between implementations.
	 * 
	 * @throws StreamException
	 *             if an error occurs closing the stream
	 */

	@Override
	public void close() throws StreamException;

}
