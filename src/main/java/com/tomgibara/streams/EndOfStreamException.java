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
 * <p>
 * Instances of this exception are thrown when a {@link ReadStream} encounters
 * an end-of-stream condition. {@link WriteStream} implementations with
 * fixed-size output may also choose to employ this exception type to indicate
 * that the stream is 'full'.
 *
 * <p>
 * In the interests of efficiency, stream implementations are encouraged to
 * throw a fixed instance of this exception type.
 *
 * @author Tom Gibara
 *
 * @see #EOS
 *
 */

public class EndOfStreamException extends StreamException {

	private static final long serialVersionUID = 5128955277305418544L;

	/**
	 * A shared instance of the exception that provides better performance for
	 * streams that need to indicate an end-of-stream condition.
	 */

	public static final EndOfStreamException EOS = new EndOfStreamException("EOS", null, false, false);

	public EndOfStreamException() { }

	public EndOfStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EndOfStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public EndOfStreamException(String message) {
		super(message);
	}

	public EndOfStreamException(Throwable cause) {
		super(cause);
	}

}
