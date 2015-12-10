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

	static void raiseClosed() {
		throw new StreamException("stream closed");
	}

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
