/*
 * Copyright 2015 Tom Gibara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tomgibara.streams;

/**
 * Implementations of this interface are supplied to the
 * {@link ReadStream#closedWith(StreamCloser)} and
 * {@link WriteStream#closedWith(StreamCloser)} methods to control the behaviour
 * of streams when closed. A number of standard implementations are available
 * via static methods on this class.
 * 
 * @author Tom Gibara
 *
 */

@FunctionalInterface
public interface StreamCloser {

	/**
	 * Closes the underlying stream and reports the stream as closed.
	 * 
	 * @return a standard closer for closing the underlying stream
	 */

	public static StreamCloser closeStream() {
		return stream -> { stream.close(); return true; };
	}

	/**
	 * Leaves the underlying stream open and continues to report the stream as
	 * open.
	 * 
	 * @return a standard closer for making closing a no-op.
	 */

	public static StreamCloser doNothing() {
		return stream -> false;
	}

	/**
	 * Leaves the underlying stream open but reports the stream as closed.
	 * 
	 * @return a standard closer for protecting the underlying stream from
	 *         closing
	 */

	public static StreamCloser reportClosed() {
		return stream -> true;
	}

	/**
	 * Called when closing a stream that wraps an underlying stream. The method
	 * guaranteed to only be called once per stream.
	 * 
	 * @param stream
	 *            the underlying stream
	 * @return true if the overlying stream should report itself as closed,
	 *         false otherwise
	 * @throws StreamException
	 *             if an error occurred closing the stream
	 */

	boolean close(CloseableStream stream) throws StreamException;

}
